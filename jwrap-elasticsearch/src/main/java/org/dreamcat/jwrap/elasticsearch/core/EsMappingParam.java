package org.dreamcat.jwrap.elasticsearch.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.x.jackson.GenericDeserialize;
import org.dreamcat.common.x.jackson.GenericSerialize;

/**
 * Create by tuke on 2021/1/19
 * see the followed links
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html#index-creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsMappingParam {

    /**
     * the property name
     */
    private String name;
    /**
     * data type
     */
    @GenericDeserialize
    @GenericSerialize
    private MappingType type;

    /**
     * build a extra keyword field for text field
     */
    private boolean keyword;

    ///

    /**
     * the nested properties
     */
    private List<EsMappingParam> children;
    /**
     * analyzer for text field
     */
    private String analyzer;
    /**
     * the constant value for constant keyword
     */
    private String constantKeywordValue;
    /**
     * format, such as "yyyy-MM-dd HH:mm:ss"
     */
    private String dateFormat;
    /**
     * scaling factor
     */
    @Builder.Default
    private int scalingFactor = 100;

    public static Map<String, Object> mappings(List<EsMappingParam> fields) {
        Map<String, Object> value = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        for (EsMappingParam field : fields) {
            properties.put(field.getName(), field.mappings());
        }
        value.put("properties", properties);
        return value;
    }

    private Map<String, Object> mappings() {
        Map<String, Object> value = new HashMap<>();

        value.put("type", type.getName());
        switch (type) {
            case CONSTANT_KEYWORD:
                if (constantKeywordValue != null) {
                    value.put("value", constantKeywordValue);
                }
                break;
            case SCALED_FLOAT:
                value.put("scaling_factor", scalingFactor);
                break;
            case DATE:
            case DATE_NANOS:
                if (dateFormat != null) {
                    value.put("format", dateFormat);
                }
                break;
            case NESTED:
                if (ObjectUtil.isNotEmpty(children)) {
                    Map<String, Object> properties = new HashMap<>();
                    for (EsMappingParam child : children) {
                        properties.put(child.getName(), child.mappings());
                    }
                    value.put("properties", properties);
                }
                break;
            case TEXT:
                // for full-text
                if (ObjectUtil.isNotBlank(analyzer)) {
                    value.put("analyzer", analyzer);
                }
                break;
            default:
                break; // nop
        }

        // for sort and aggregation
        if (keyword) {
            value.put("fields", fieldsKeyword());
        }
        return value;
    }

    /**
     * term query searches on keyword fields are often faster than
     * term searches on numeric fields
     */
    @Getter
    @RequiredArgsConstructor
    public enum MappingType implements Supplier<String> {
        BINARY("binary"), // accepts a binary value as a Base64 encoded string.
        BOOLEAN("boolean"),
        // keyword
        KEYWORD("keyword"), // term query
        CONSTANT_KEYWORD("constant_keyword"), // "value": "debug"
        WILDCARD("wildcard"), // wildcard query
        // integer
        BYTE("byte"),
        SHORT("short"),
        INTEGER("integer"),
        LONG("long"),
        UNSIGNED_LONG("unsigned_long"),
        // float
        HALF_FLOAT("half_float"),
        FLOAT("float"),
        DOUBLE("double"),
        SCALED_FLOAT("scaled_float"), // "scaling_factor": 10
        // date
        DATE("date"), // "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        DATE_NANOS("date_nanos"),
        // alias
        ALIAS("alias"), // "path": "target_field"
        // object
        FLATTENED("flattened"),
        NESTED("nested"),
        // range
        INTEGER_RANGE("integer_range"), // {"gte": 1, "lt": 0}
        FLOAT_RANGE("float_range"),
        LONG_RANGE("long_range"),
        DOUBLE_RANGE("double_range"),
        DATE_RANGE("date_range"),
        IP_RANGE("ip_range"),
        // string
        TEXT("text");

        private final String name;

        @Override
        public String get() {
            return name;
        }
    }

    private static Map<String, Object> fieldsKeyword() {
        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> keyword = new HashMap<>();
        keyword.put("type", "keyword");
        fields.put("keyword", keyword);
        return fields;
    }
}

package org.dreamcat.jwrap.elasticsearch.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.jwrap.elasticsearch.core.EsMappingType;
import org.dreamcat.jwrap.elasticsearch.core.EsMappingValue;

/**
 * Create by tuke on 2021/1/15
 */
public final class EsMappingUtil {

    private EsMappingUtil() {
    }

    public static Map<String, Object> mapping(Iterable<EsMappingValue> fields) {
        Map<String, Object> value = new HashMap<>();
        Map<String, Object> properties = new HashMap<String, Object>();
        for (EsMappingValue field : fields) {
            properties.put(field.getName(), mapping(field));
        }
        value.put("properties", properties);
        return value;
    }

    public static Map<String, Object> mapping(EsMappingValue field) {
        Map<String, Object> value = new HashMap<>();

        EsMappingType type = field.getType();
        value.put("type", type.getName());
        switch (type) {
            case CONSTANT_KEYWORD:
                String constantKeywordValue = field.getConstantKeywordValue();
                if (constantKeywordValue != null) {
                    value.put("value", constantKeywordValue);
                }
                break;
            case SCALED_FLOAT:
                int scalingFactor = field.getScalingFactor();
                value.put("scaling_factor", scalingFactor);
                break;
            case DATE:
            case DATE_NANOS:
                String dateFormat = field.getDateFormat();
                if (dateFormat != null) {
                    value.put("format", dateFormat);
                }
                break;
            case NESTED:
                List<EsMappingValue> children = field.getChildren();
                if (ObjectUtil.isNotEmpty(children)) {
                    Map<String, Object> properties = new HashMap<>();
                    for (EsMappingValue child : children) {
                        properties.put(child.getName(), mapping(child));
                    }
                    value.put("properties", properties);
                }
                break;
            case TEXT:
                // for full-text
                String analyzer = field.getAnalyzer();
                if (ObjectUtil.isNotBlank(analyzer)) {
                    value.put("analyzer", analyzer);
                }
                break;
        }

        // for sort and aggregation
        if (field.isKeyword()) {
            value.put("fields", fieldsKeyword());
        }
        return value;
    }

    private static Map<String, Object> fieldsKeyword() {
        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> keyword = new HashMap<>();
        keyword.put("type", "keyword");
        fields.put("keyword", keyword);
        return fields;
    }
}

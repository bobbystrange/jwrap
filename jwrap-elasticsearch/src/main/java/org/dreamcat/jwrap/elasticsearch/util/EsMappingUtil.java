package org.dreamcat.jwrap.elasticsearch.util;

import java.util.HashMap;
import java.util.Map;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.jwrap.elasticsearch.common.EsMappingValue;

/**
 * Create by tuke on 2021/1/15
 */
public final class EsMappingUtil {

    private EsMappingUtil() {
    }

    public static Map<String, Object> mapping(Iterable<EsMappingValue> fields) {
        var value = new HashMap<String, Object>();
        var properties = new HashMap<String, Object>();
        for (var field : fields) {
            properties.put(field.getName(), mapping(field));
        }
        value.put("properties", properties);
        return value;
    }

    public static Map<String, Object> mapping(EsMappingValue field) {
        var value = new HashMap<String, Object>();

        var type = field.getType();
        value.put("type", type.getName());
        switch (type) {
            case CONSTANT_KEYWORD:
                var constantKeywordValue = field.getConstantKeywordValue();
                if (constantKeywordValue != null) {
                    value.put("value", constantKeywordValue);
                }
                break;
            case SCALED_FLOAT:
                var scalingFactor = field.getScalingFactor();
                value.put("scaling_factor", scalingFactor);
                break;
            case DATE:
            case DATE_NANOS:
                var dateFormat = field.getDateFormat();
                if (dateFormat != null) {
                    value.put("format", dateFormat);
                }
                break;
            case NESTED:
                var children = field.getChildren();
                if (ObjectUtil.isNotEmpty(children)) {
                    var properties = new HashMap<String, Object>();
                    for (var child : children) {
                        properties.put(child.getName(), mapping(child));
                    }
                    value.put("properties", properties);
                }
                break;
            case TEXT:
                // for full-text
                var analyzer = field.getAnalyzer();
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
        var fields = new HashMap<String, Object>();
        var keyword = new HashMap<String, Object>();
        keyword.put("type", "keyword");
        fields.put("keyword", keyword);
        return fields;
    }
}

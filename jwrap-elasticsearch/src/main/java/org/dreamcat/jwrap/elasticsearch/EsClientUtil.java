package org.dreamcat.jwrap.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.dreamcat.common.x.jackson.JacksonUtil;

/**
 * Create by tuke on 2021/1/15
 */
public final class EsClientUtil {

    private EsClientUtil() {
    }

    public static String mapping(Map<String, Object> sample) {
        var mapping = new HashMap<String, Object>();
        var properties = new HashMap<String, Object>();
        mapping.put("properties", properties);

        for (Entry<String, Object> entry : sample.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            if (fieldValue == null) continue;

            var value = new HashMap<String, Object>();
            var fieldType = fieldValue.getClass();
            if (String.class.equals(fieldType)) {
                value.put("type", "text");
                value.put("analyzer", "ngram_analyzer");
            } else if (Integer.class.equals(fieldType)) {
                value.put("type", "integer");
            } else if (Long.class.equals(fieldType)) {
                value.put("type", "long");
            } else if (Double.class.equals(fieldType)) {
                value.put("type", "double");
            } else if (Boolean.class.equals(fieldType)) {
                value.put("type", "boolean");
            } else {
                continue;
            }
            properties.put(fieldName, value);
        }
        return JacksonUtil.toJson(mapping);
    }

}

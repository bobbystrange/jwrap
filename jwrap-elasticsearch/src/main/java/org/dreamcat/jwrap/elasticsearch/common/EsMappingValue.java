package org.dreamcat.jwrap.elasticsearch.common;

import java.util.List;
import lombok.Data;

/**
 * Create by tuke on 2021/1/19
 */
@Data
public class EsMappingValue {

    /**
     * doc_values: used for sorting, aggregations, or scripting
     */
    private boolean docValues = true;
    /**
     * enabled: make unsearchable
     */
    private boolean enabled = true;
    /**
     * the key of elasticsearch mapping
     */
    private String name;
    /**
     * data type in elasticsearch
     */
    private EsMappingType type;

    /**
     * build a extra keyword field for text field
     */
    private boolean keyword;

    ///

    /**
     * the nested properties
     */
    private List<EsMappingValue> children;
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
    private int scalingFactor = 100;
}

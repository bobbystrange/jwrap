package org.dreamcat.jwrap.elasticsearch.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.x.jackson.GenericDeserialize;

/**
 * Create by tuke on 2021/1/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsMappingValue {

    /**
     * the key of elasticsearch mapping
     */
    private String name;
    /**
     * data type in elasticsearch
     */
    @GenericDeserialize
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
    @Builder.Default
    private int scalingFactor = 100;
}

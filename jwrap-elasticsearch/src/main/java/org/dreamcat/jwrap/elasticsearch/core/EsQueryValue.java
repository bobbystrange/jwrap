package org.dreamcat.jwrap.elasticsearch.core;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;

/**
 * Create by tuke on 2021/1/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsQueryValue {

    /**
     * the nested path, using in nested query
     */
    private String path;
    /**
     * How to aggregate multiple child hit scores into a single parent score
     * using in nested query
     */
    @Builder.Default
    private ScoreMode scoreMode = ScoreMode.None;
    /**
     * should or must, using in nested query
     */
    @Builder.Default
    private boolean should = false;
    /**
     * the nested query
     */
    private List<EsQueryValue> children;
    /**
     * the key of org.dreamcat.jwrap.elasticsearch mapping
     */
    private String name;
    /**
     * a json value
     */
    private Object value;
    /**
     * exact or fuzzy
     */
    @Builder.Default
    private boolean exact = true;

}

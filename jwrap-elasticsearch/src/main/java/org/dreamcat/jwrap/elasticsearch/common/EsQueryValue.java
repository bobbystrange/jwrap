package org.dreamcat.jwrap.elasticsearch.common;

import java.util.List;
import lombok.Data;
import org.apache.lucene.search.join.ScoreMode;

/**
 * Create by tuke on 2021/1/21
 */
@Data
public class EsQueryValue {

    /**
     * the nested path, using in nested query
     */
    private String path;
    /**
     * How to aggregate multiple child hit scores into a single parent score
     * using in nested query
     */
    private ScoreMode scoreMode = ScoreMode.None;
    /**
     * should or must, using in nested query
     */
    private boolean should = false;
    /**
     * the nested query
     */
    private List<EsQueryValue> children;
    /**
     * the key of elasticsearch mapping
     */
    private String name;
    /**
     * a json value
     */
    private Object value;
    /**
     * exact or fuzzy
     */
    private boolean exact;

}

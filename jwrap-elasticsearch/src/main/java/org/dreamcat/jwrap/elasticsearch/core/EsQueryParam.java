package org.dreamcat.jwrap.elasticsearch.core;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.util.ObjectUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

/**
 * Create by tuke on 2021/1/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsQueryParam {

    /**
     * a json path joined with dot sign
     * it will be ignored when {@link #children} is not empty
     */
    private String name;
    /**
     * a json value
     * it will be ignored when {@link #children} is not empty
     * the query type depends on the java type
     * {@link List}: terms
     * {@link Map.Entry}: range from to
     * {@link String}: match phrase (exact=false) or term (exact=true)
     * other types: term
     */
    private Object value;
    /**
     * exact or fuzzy
     * it will be ignored when {@link #children} is not empty
     */
    @Builder.Default
    private boolean exact = true;

    /**
     * joined type
     */
    @Builder.Default
    private JoinType joinType = JoinType.MUST;
    /**
     * joined nodes
     */
    private List<EsQueryParam> children;

    public QueryBuilder queryBuilder() {
        // leaf node
        if (ObjectUtil.isEmpty(children)) {
            return directQuery();
        }

        // tree node
        BoolQueryBuilder query = new BoolQueryBuilder();
        for (EsQueryParam node : children) {
            QueryBuilder queryBuilder = node.queryBuilder();
            switch (joinType) {
                case SHOULD:
                    query.should(queryBuilder);
                    break;
                case MUST:
                    query.must(queryBuilder);
                    break;
                case MUST_NOT:
                    query.mustNot(queryBuilder);
                    break;
                default:
                    break;
            }
        }
        return query;
    }

    private QueryBuilder directQuery() {
        // containing query
        if (value instanceof List) {
            return new TermsQueryBuilder(name, (List<?>) value);
        }
        // range query
        else if (value instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
            return new RangeQueryBuilder(name)
                    .from(entry.getKey())
                    .to(entry.getValue());
        }
        // equal query
        else {
            if (exact) {
                // A Query that matches documents containing a term.
                return new TermQueryBuilder(name, value);
            } else {
                // Match query is a query that analyzes the text and constructs a phrase query as the result of the analysis
                return new MatchPhraseQueryBuilder(name, value);
            }
        }
    }

    public enum JoinType {
        SHOULD,
        MUST,
        MUST_NOT,
    }

    public static QueryBuilder queryBuilder(List<EsQueryParam> query) {
        return EsQueryParam.builder()
                .children(query)
                .build().queryBuilder();
    }
}

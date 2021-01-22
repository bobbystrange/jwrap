package org.dreamcat.jwrap.elasticsearch.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.jwrap.elasticsearch.common.EsQueryValue;
import org.dreamcat.jwrap.elasticsearch.common.EsSortValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

/**
 * Create by tuke on 2021/1/20
 */
public final class EsQueryUtil {

    private EsQueryUtil() {
    }

    public static BoolQueryBuilder boolMust(Iterable<EsQueryValue> fields) {
        return bool(fields, BoolQueryBuilder::must);
    }

    public static BoolQueryBuilder boolMustNot(Iterable<EsQueryValue> fields) {
        return bool(fields, BoolQueryBuilder::mustNot);
    }

    public static BoolQueryBuilder boolShould(Iterable<EsQueryValue> fields) {
        return bool(fields, BoolQueryBuilder::should);
    }

    public static BoolQueryBuilder bool(
            Iterable<EsQueryValue> fields,
            BiConsumer<BoolQueryBuilder, QueryBuilder> setter) {
        var query = new BoolQueryBuilder();
        for (var field : fields) {
            setter.accept(query, queryBuilder(field));
        }
        return query;
    }

    public static QueryBuilder queryBuilder(EsQueryValue field) {
        var name = field.getName();
        var value = field.getValue();
        var exact = field.isExact();

        // nested query
        var children = field.getChildren();
        if (ObjectUtil.isNotEmpty(children)) {
            var path = field.getPath();
            var should = field.isShould();
            var scoreMode = field.getScoreMode();
            var query = new BoolQueryBuilder();
            for (EsQueryValue child : children) {
                var childQuery = queryBuilder(child);
                if (should) {
                    query.should(childQuery);
                } else {
                    query.must(childQuery);
                }
            }
            return new NestedQueryBuilder(path, query, scoreMode);
        }

        if (exact) {
            if (value instanceof List) {
                return new TermsQueryBuilder(name, (List<?>) value);
            } else {
                // A Query that matches documents containing a term.
                return new TermQueryBuilder(name, value);
            }
        } else {
            // Match query is a query that analyzes the text and constructs a phrase query as the result of the analysis
            return new MatchPhraseQueryBuilder(name, value);
        }
    }

    public static List<SortBuilder<?>> sort(List<EsSortValue> sort) {
        if (sort == null) return null;
        return sort.stream()
                .map(it -> SortBuilders.fieldSort(it.getName()).order(it.getOrder()))
                .collect(Collectors.toList());
    }
}

package org.dreamcat.jwrap.elasticsearch;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.dreamcat.jwrap.elasticsearch.core.EsMappingValue;
import org.dreamcat.jwrap.elasticsearch.util.EsMappingUtil;
import org.elasticsearch.ElasticsearchException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by tuke on 2021/1/15
 */
@Slf4j
@RestController
@RequestMapping(value = "/data")
public class DataController {

    private static final String settings = "{\n"
            + "    \"analysis\": {\n"
            + "        \"analyzer\": {\n"
            + "            \"ngram_analyzer\": {\n"
            + "                \"tokenizer\": \"ngram_tokenizer\"\n"
            + "            }\n"
            + "        },\n"
            + "        \"tokenizer\": {\n"
            + "            \"ngram_tokenizer\": {\n"
            + "                \"type\": \"ngram\",\n"
            + "                \"min_gram\": 1,\n"
            + "                \"max_gram\": 1,\n"
            + "                \"token_chars\": [\n"
            + "                    \"letter\",\n"
            + "                    \"digit\"\n"
            + "                ]\n"
            + "            }\n"
            + "        }\n"
            + "    }\n"
            + "}";

    @Resource
    private EsIndexComponent esIndexComponent;
    @Resource
    private EsDocumentComponent esDocumentComponent;
    @Resource
    private EsSearchComponent esSearchComponent;

    @RequestMapping(value = "/{index}", method = RequestMethod.POST)
    public Object createIndex(
            @PathVariable("index") String index,
            @RequestBody List<EsMappingValue> json) {
        try {
            if (!esIndexComponent.existsIndex(index)) {
                Map<String, Object> mapping = EsMappingUtil.mapping(json);
                log.info("mapping: {}", mapping);
                return esIndexComponent.createIndex(index, mapping, settings);
            }
            return false;
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }

    }

    @RequestMapping(value = "/{index}", method = RequestMethod.GET)
    public Object getIndex(
            @PathVariable("index") String index) {
        try {
            return esIndexComponent.getIndex(index);
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.DELETE)
    public Object deleteIndex(
            @PathVariable("index") String index) {
        try {
            return esIndexComponent.deleteIndex(index);
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.POST)
    public Object insert(
            @PathVariable("index") String index,
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> json) {
        try {
            return esDocumentComponent.insert(
                    index, id, JacksonUtil.toJson(json));
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.PUT)
    public Object update(
            @PathVariable("index") String index,
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> json) {
        if (!esIndexComponent.existsIndex(index)) {
            return false;
        }
        try {
            return esDocumentComponent.update(index, id, JacksonUtil.toJson(json));
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.GET)
    public Object get(
            @PathVariable("index") String index,
            @PathVariable("id") String id) {
        try {
            return esSearchComponent.get(index, id, Map.class);
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/{index}/{id}", method = RequestMethod.DELETE)
    public Object delete(
            @PathVariable("index") String index,
            @PathVariable("id") String id) {
        try {
            return esDocumentComponent.delete(index, id);
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Object search(
            @RequestBody EsSearchValue search) {
        try {
            return esSearchComponent.search(
                    search.getIndex(), search.getQuery(), search.getSort(),
                    search.getOffset(), search.getLimit(),
                    search.getIncludes(), search.getExcludes());
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/query/{index}", method = RequestMethod.POST)
    public Object query(
            @PathVariable String index,
            @RequestBody Collection<String> ids) {
        try {
            return esSearchComponent.mget(index, ids,
                    new String[]{"name", "color"}, null);
        } catch (ElasticsearchException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }
}

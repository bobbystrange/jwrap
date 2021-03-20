package org.dreamcat.jwrap.elasticsearch;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.io.IOUtil;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.dreamcat.jwrap.elasticsearch.core.EsMappingParam;
import org.dreamcat.jwrap.elasticsearch.core.EsSearchParam;
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

    @Resource
    private EsIndexComponent esIndexComponent;
    @Resource
    private EsDocumentComponent esDocumentComponent;
    @Resource
    private EsSearchComponent esSearchComponent;

    @RequestMapping(value = "/{index}", method = RequestMethod.POST)
    public Object createIndex(
            @PathVariable("index") String index,
            @RequestBody List<EsMappingParam> json) {
        try {
            if (esIndexComponent.existsIndex(index)) {
                return false;
            }
            String settings = IOUtil.readAsString(
                    DataController.class.getResourceAsStream("/settings.json"));
            Map<String, Object> mappings = EsMappingParam.mappings(json);
            log.info("mappings: {}", mappings);
            return esIndexComponent.createIndex(index, mappings, settings);
        } catch (Exception e) {
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
            @RequestBody EsSearchParam search) {
        try {
            return esSearchComponent.search(search);
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

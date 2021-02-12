package org.dreamcat.jwrap.mybatis.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.dreamcat.jwrap.mybatis.dao.ComplexMapper;
import org.dreamcat.jwrap.mybatis.dao.SimpleMapper;
import org.dreamcat.jwrap.mybatis.entity.ComplexEntity;
import org.dreamcat.jwrap.mybatis.entity.SimpleEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by tuke on 2020/9/1
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ComplexController {
    private final ComplexMapper complexMapper;
    private final SimpleMapper simpleMapper;

    @RequestMapping(path = "/complex", method = RequestMethod.GET)
    public ResponseEntity<ComplexEntity> select(@RequestParam(name = "id") Long id) {
        var entity = complexMapper.select(id);
        return new ResponseEntity<>(entity, entity != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/complex", method = RequestMethod.POST)
    public Long insert(ComplexQuery query) {
        log.info("POST {}", JacksonUtil.toJson(query));

        var entity = new ComplexEntity();
        entity.setName(query.getName());

        var user = new ComplexEntity.User();
        user.setFirstName(query.getFirstName());
        user.setLastName(query.getLastName());
        entity.setUser(user);

        var tagsStr = query.getTags();
        if (tagsStr != null) {
            var tags = Arrays.stream(tagsStr.split(","))
                    .map(String::trim)
                    .map(ComplexEntity.Tag::new)
                    .collect(Collectors.toList());
            entity.setTags(tags);
        }

        int affectedRows = complexMapper.batchInsert(Collections.singletonList(entity), "complex");
        log.info("affected rows {}", affectedRows);
        return entity.getId();
    }

    @RequestMapping(path = "/simple", method = RequestMethod.GET)
    public ResponseEntity<SimpleEntity> selectSimple(@RequestParam(name = "id") Long id) {
        var entity = simpleMapper.select(id);
        return new ResponseEntity<>(entity, entity != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/simple", method = RequestMethod.POST)
    public SimpleEntity insertSimple(@RequestParam("content") String content) {
        log.info("POST {}", content);
        var entity = new SimpleEntity();
        entity.setType(SimpleEntity.Type.COMMON);
        entity.setContent(content);

        int affectedRows = simpleMapper.insert(entity, "simple");
        log.info("affected rows {}", affectedRows);
        return simpleMapper.select(entity.getId());
    }

}

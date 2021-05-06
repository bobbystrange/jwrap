package org.dreamcat.jwrap.mybatis.controller;

import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.util.DateUtil;
import org.dreamcat.jwrap.mybatis.dao.DDLMapper;
import org.dreamcat.jwrap.mybatis.entity.ComplexEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create by tuke on 2020/9/1
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/ddl")
public class DDLController {

    private final DDLMapper ddlMapper;

    // curl -XPOST -v http://localhost:8080/ddl/table\?timestamp\=1599025683547
    @PostMapping(path = "/table")
    public ResponseEntity<ComplexEntity> createTable(@RequestParam(name = "timestamp") Long timestamp) {
        String time = DateTimeFormatter.ofPattern("yyyy_MM").format(DateUtil.ofEpochMilli(timestamp));
        ddlMapper.createTable(time);
        ddlMapper.createRecordTable(time);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

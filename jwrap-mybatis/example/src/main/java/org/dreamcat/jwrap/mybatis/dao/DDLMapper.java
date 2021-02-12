package org.dreamcat.jwrap.mybatis.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Create by tuke on 2020/9/1
 */
@Mapper
public interface DDLMapper {

    void createTable(@Param("suffix") String suffix);

    void createRecordTable(@Param("suffix") String suffix);

}

package org.dreamcat.jwrap.mybatis.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dreamcat.common.web.features.repository.RepositoryAfterReturning;
import org.dreamcat.common.web.features.repository.RepositoryAfterReturning.Type;
import org.dreamcat.jwrap.mybatis.entity.SimpleEntity;

/**
 * Create by tuke on 2020/7/17
 */
@Mapper
public interface SimpleMapper {

    @RepositoryAfterReturning(type = Type.SELECT, expressions = {
            "'simple'", "#id", "#result.tenantId"
    })
    SimpleEntity select(@Param("id") Long id);

    @RepositoryAfterReturning(type = Type.INSERT, expressions = {
            "'simple'", "#entity.id", "#tenantId"
    })
    int insert(@Param("entity") SimpleEntity entity, @Param("tenantId") String tenantId);

}

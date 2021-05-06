package org.dreamcat.jwrap.mybatis.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dreamcat.common.web.features.repository.RepositoryAfterReturning;
import org.dreamcat.common.web.features.repository.RepositoryAfterReturning.Type;
import org.dreamcat.jwrap.mybatis.entity.ComplexEntity;

/**
 * Create by tuke on 2020/7/8
 */
@Mapper
public interface ComplexMapper {

    @RepositoryAfterReturning(type = Type.SELECT, args = {
            "'complex'", "#id", "#result.tenantId"
    })
    ComplexEntity select(@Param("id") Long id);

    @RepositoryAfterReturning(type = Type.INSERT, args = {
            "'complex'", "#entity.id", "#tenantId"
    })
    int insert(@Param("entity") ComplexEntity entity, @Param("tenantId") String tenantId);

    @RepositoryAfterReturning(type = Type.INSERT, args = {
            "'complex'", "#list.![id]", "#tenantId"
    })
    int batchInsert(@Param("list") List<ComplexEntity> list, @Param("tenantId") String tenantId);

    @RepositoryAfterReturning(type = Type.UPDATE, args = {
            "'complex'", "#entity.id", "#tenantId"
    })
    int update(@Param("entity") ComplexEntity entity, @Param("tenantId") String tenantId);

    @RepositoryAfterReturning(type = Type.UPDATE, args = {
            "'complex'", "#ids", "#tenantId"
    })
    int batchUpdate(@Param("entity") ComplexEntity entity, @Param("ids") List<Long> ids,
            @Param("tenantId") String tenantId);

    @RepositoryAfterReturning(type = Type.DELETE, args = {
            "'complex'", "#id", "#tenantId"
    })
    int delete(@Param("id") Long id, @Param("tenantId") String tenantId);

    @RepositoryAfterReturning(type = Type.DELETE, args = {
            "'complex'", "#ids", "#tenantId"
    })
    int batchDelete(@Param("ids") List<Long> ids, @Param("tenantId") String tenantId);
}

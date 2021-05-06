package org.dreamcat.jwrap.mybatis.aop;

import org.dreamcat.common.web.features.repository.RepositoryAfterReturningAdvice;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/2/8
 */
@Component
public class RepositoryAfterReturningAdviceImpl implements RepositoryAfterReturningAdvice {

    @Override
    public void insert(Object... args) {
        // nop
    }

    @Override
    public void update(Object... args) {
        // nop
    }

    @Override
    public void select(Object... args) {
        // nop
    }

    @Override
    public void delete(Object... args) {
        // nop
    }
}

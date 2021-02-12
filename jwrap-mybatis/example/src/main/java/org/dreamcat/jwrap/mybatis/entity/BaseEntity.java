package org.dreamcat.jwrap.mybatis.entity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Create by tuke on 2020/7/8
 */
@Getter
@Setter
@ToString
public class BaseEntity {
    private Long id;
    private String tenantId;
    private Date createTime;
    private Date updateTime;
}

create
database if not exists jwrap_mybatis;

use
jwrap_mybatis;

create table if not exists `complex`
(
    `id` bigint not null auto_increment, `tenant_id` char
(
    36
) not null,
    `create_time` timestamp not null default current_timestamp,
    `update_time` timestamp not null default current_timestamp on update current_timestamp,
    `name` varchar
    (255),
    `tags` text,
    `user` varchar
    (255),
    primary key
    (`id`)
    ) engine = InnoDB
    default charset = utf8mb4;

create table if not exists `simple`
(
    `id` bigint not null auto_increment, `tenant_id` char
(
    36
) not null,
    `create_time` timestamp not null default current_timestamp,
    `update_time` timestamp not null default current_timestamp on update current_timestamp,
    `content` text,
    `type` tinyint,
    primary key
    (`id`),
    key
    (create_time, tenant_id)
    ) engine = InnoDB
    default charset = utf8mb4;



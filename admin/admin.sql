CREATE database if NOT EXISTS `al_job` default character set utf8mb4 collate utf8mb4_unicode_ci;

use `al_job`;

SET NAMES utf8mb4;

CREATE TABLE `job_executor`
(
    `id`           bigint(20)   unsigned NOT NULL AUTO_INCREMENT,
    `app_name`     varchar(128) NOT NULL COMMENT '应用名称',
    `title`        varchar(255) NOT NULL COMMENT '标题',
    `address_type` tinyint(4)   NOT NULL DEFAULT 0 COMMENT '地址类型：0自动注册；1手动录入',
    `glue_type`    varchar(255) NOT NULL COMMENT '支持的Glue，多个逗号分隔',
    `update_time`  datetime     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_app_name (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '执行器表';


CREATE TABLE `job_registry`
(
    `id`           bigint(20)   unsigned NOT NULL AUTO_INCREMENT,
    `type`         varchar(64)  NOT NULL COMMENT '注册类型：ADMIN调度器、EXECUTOR执行器',
    `app_name`     varchar(128) NOT NULL COMMENT '应用名称',
    `address`      varchar(255) NOT NULL COMMENT '节点地址',
    `address_type` tinyint(4)   NOT NULL DEFAULT 0 COMMENT '地址类型：0自动注册；1手动录入',
    `health`       tinyint(4)   DEFAULT 0 COMMENT '健康状态：-1离线、0未知、1正常',
    `update_time`  datetime     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_registry` (`type`, `app_name`, `address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '节点注册表';


CREATE TABLE `job_info`
(
    `id`                        bigint(20)   unsigned NOT NULL AUTO_INCREMENT,
    `executor_id`               bigint(20)   unsigned NOT NULL COMMENT '执行器ID',
    `name`                      varchar(255) NOT NULL COMMENT '任务名称',
    `group_name`                varchar(255)          DEFAULT NULL COMMENT '任务分组名称',
    `author`                    varchar(64)           DEFAULT NULL COMMENT '责任人',
    `alarm_email`               varchar(255)          DEFAULT NULL COMMENT '报警邮件',
    `glue_type`                 varchar(64)  NOT NULL COMMENT '任务类型',
    `schedule_type`             varchar(64)  NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
    `schedule_conf`             varchar(128)          DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
    `executor_handler`          varchar(255)          DEFAULT NULL COMMENT '执行任务处理器',
    `executor_param`            text                  DEFAULT NULL COMMENT '执行任务参数',
    `misfire_strategy`          varchar(64)  NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
    `route_strategy`            varchar(64)  NOT NULL COMMENT '执行器路由策略',
    `block_strategy`            varchar(64)  NOT NULL COMMENT '执行阻塞策略',
    `timeout`                   int(11)               DEFAULT 0 COMMENT '任务执行超时时间，单位秒',
    `fail_retry`                int(11)               DEFAULT 0 COMMENT '失败重试次数',
    `status`                    tinyint(4)   NOT NULL DEFAULT 0 COMMENT '任务状态：0停止；1运行',
    `remark`                    varchar(255) DEFAULT NULL COMMENT '备注',
    `trigger_last_time`         bigint(20)   NOT NULL DEFAULT 0 COMMENT '上次调度时间',
    `trigger_next_time`         bigint(20)   NOT NULL DEFAULT 0 COMMENT '下次调度时间',
    `create_time`               datetime              DEFAULT NULL,
    `update_time`               datetime              DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY idx_executor_id (`executor_id`),
    KEY idx_job_group_name (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '任务表';


CREATE TABLE `job_child`
(
    `id`       bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `job_id`   bigint(20) unsigned NOT NULL COMMENT '任务ID',
    `child_id` bigint(20) unsigned NOT NULL COMMENT '子任务ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_job_child (`job_id`, `child_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '子任务表';


CREATE TABLE `job_log`
(
    `id`                        bigint(20) unsigned NOT NULL,
    `job_id`                    bigint(20) unsigned NOT NULL COMMENT '任务ID',
    `executor_id`               bigint(20) unsigned NOT NULL COMMENT '执行器ID',
    `executor_address`          varchar(255) DEFAULT NULL COMMENT '执行器地址',
    `executor_handler`          varchar(255) DEFAULT NULL COMMENT '执行处理器',
    `executor_param`            varchar(512) DEFAULT NULL COMMENT '任务参数',
    `sharding_param`            varchar(64)  DEFAULT NULL COMMENT '分片参数，格式如 1/2',
    `fail_retry`                int(11)      DEFAULT 0 COMMENT '失败重试次数',
    `trigger_type`              varchar(64)  DEFAULT NULL COMMENT '触发类型',
    `trigger_time`              datetime   NOT NULL COMMENT '调度时间',
    `trigger_code`              int(11)    NOT NULL COMMENT '调度结果',
    `trigger_msg`               text         DEFAULT NULL COMMENT '调度日志',
    `handle_time`               datetime     DEFAULT NULL COMMENT '执行时间',
    `handle_code`               int(11)      DEFAULT NULL COMMENT '执行结果',
    `handle_msg`                text         DEFAULT NULL COMMENT '执行日志',
    `alarm_status`              tinyint(4)   DEFAULT NULL COMMENT '告警状态：1告警成功、0无需告警、-1告警失败',
    PRIMARY KEY (`id`),
    KEY `idx_job_log` (`job_id`, `trigger_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '任务日志表';


CREATE TABLE `job_report`
(
    `id`            bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `trigger_day`   date       NOT NULL COMMENT '调度时间',
    `running_count` int(11)    NOT NULL DEFAULT 0 COMMENT '运行中-日志数量',
    `success_count` int(11)    NOT NULL DEFAULT 0 COMMENT '执行成功-日志数量',
    `fail_count`    int(11)    NOT NULL DEFAULT 0 COMMENT '执行失败-日志数量',
    `update_time`   datetime   DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_trigger_day` (`trigger_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '任务运行统计表';


CREATE TABLE `job_glue_code`
(
    `id`          bigint(20)   unsigned NOT NULL AUTO_INCREMENT,
    `job_id`      bigint(20)   unsigned NOT NULL COMMENT '任务ID',
    `glue_type`   varchar(64)  NOT NULL COMMENT 'GLUE类型',
    `glue_source` mediumtext   DEFAULT NULL COMMENT 'GLUE源代码',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_glue_code_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '任务脚本代码表';


CREATE TABLE `job_handler_registry`
(
    `id`            bigint(20)   unsigned NOT NULL AUTO_INCREMENT,
    `app_name`      varchar(128) NOT NULL COMMENT '应用名称',
    `name`          varchar(128) NOT NULL COMMENT '名称',
    `title`         varchar(255) DEFAULT NULL COMMENT '标题',
    `update_time`   bigint(20)   DEFAULT NULL COMMENT '更新时间，用作版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_handler_name` (`app_name`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '执行处理器表';


CREATE TABLE `job_handler_param`
(
    `id`            bigint(20)   unsigned NOT NULL AUTO_INCREMENT,
    `app_name`      varchar(128) NOT NULL COMMENT '应用名称',
    `handler_name`  varchar(128) NOT NULL COMMENT '处理器名称（方法名）',
    `name`          varchar(128) NOT NULL COMMENT '名称（参数名）',
    `title`         varchar(255) DEFAULT NULL COMMENT '标题',
    `type`          varchar(255) NOT NULL COMMENT '数据类型',
    `required`      tinyint(1)   DEFAULT 0 COMMENT '是否必须',
    `default_value` varchar(255) DEFAULT NULL COMMENT '默认值',
    `pattern`       varchar(255) DEFAULT NULL COMMENT '日期格式',
    `is_array`      tinyint(1)   DEFAULT 0 COMMENT '是否数组',
    `remark`        varchar(255) DEFAULT NULL COMMENT '备注',
    `update_time`   bigint(20)   DEFAULT NULL COMMENT '更新时间，用作版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_handler_param` (`app_name`, `handler_name`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '处理器参数字段表';

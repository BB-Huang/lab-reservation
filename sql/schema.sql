CREATE TABLE IF NOT EXISTS `device_category` (
    `id`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `sort` INT          NOT NULL DEFAULT 0 COMMENT '排序越小越靠前',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分类';
CREATE TABLE IF NOT EXISTS `device` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '设备号',
    `name`        VARCHAR(128) NOT NULL COMMENT '设备名',
    `model`       VARCHAR(128) COMMENT '型号',
    `category_id` BIGINT       NOT NULL COMMENT '分类ID',
    `lab_room`    VARCHAR(50)  NOT NULL COMMENT '所在教室',
    `status`      VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE' COMMENT '设备状态',
    `description` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '设备描述',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    CONSTRAINT `chk_device_status`
    	CHECK (`status` IN ('AVAILABLE','MAINTENANCE','RETIRED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备';
CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`   VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`   VARCHAR(100) NOT NULL COMMENT 'BCrypt哈希',
    `real_name`  VARCHAR(50)  NOT NULL COMMENT '实名',
    `role`       VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT '角色',
    `enabled`    TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    CONSTRAINT `chk_user_role`
    	CHECK (`role` IN ('STUDENT', 'TEACHER', 'ADMIN')),
    CONSTRAINT `chk_user_enabled`
    	CHECK (`enabled` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';
CREATE TABLE IF NOT EXISTS `reservation` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `user_id`        BIGINT       NOT NULL COMMENT '预约人',
    `device_id`      BIGINT       NOT NULL COMMENT '设备',
    `start_time`     DATETIME     NOT NULL COMMENT '开始时间',
    `end_time`       DATETIME     NOT NULL COMMENT '结束时间',
    `purpose`        VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用途',
    `status`         VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `reviewer_id`    BIGINT       DEFAULT NULL COMMENT '审批人',
    `review_comment` VARCHAR(255) DEFAULT NULL COMMENT '审批意见',
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_res_conflict` (`device_id`, `status`, `start_time`, `end_time`),
    KEY `idx_res_user_time` (`user_id`, `start_time` DESC),
    CONSTRAINT `chk_res_status`
        CHECK (`status` IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT `chk_res_time`
        CHECK (`end_time` > `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约';

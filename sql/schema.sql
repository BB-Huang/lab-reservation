CREATE TABLE `device_category` (
    `id`   BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序越小越靠前',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分类';

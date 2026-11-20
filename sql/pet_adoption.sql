-- =====================================================================
--  Java1234 微信小程序宠物领养系统 —— 数据库建表脚本 + 测试数据
--  数据库版本：MySQL 8.0
--  服务端口：3308
--  数据库名：pet_adoption
--  说明：所有账号密码均为明文 123456（毕业设计要求，不做加密）
-- =====================================================================

-- 1. 创建数据库（若已存在则先删除）
DROP DATABASE IF EXISTS `pet_adoption`;
CREATE DATABASE `pet_adoption` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `pet_adoption`;

-- =====================================================================
-- 2. 建表语句
-- =====================================================================

-- ----------------------------
-- 2.1 用户表（后台管理员 role=admin，小程序用户 role=user）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '登录账号',
  `password`    VARCHAR(100) NOT NULL COMMENT '登录密码（明文 123456）',
  `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '用户昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `gender`      VARCHAR(10)  DEFAULT '未知' COMMENT '性别：男/女/未知',
  `city`        VARCHAR(50)  DEFAULT NULL COMMENT '所在城市',
  `intro`       VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
  `role`        VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '角色：admin 管理员 / user 普通用户',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常 0 禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2.2 宠物分类表
-- ----------------------------
DROP TABLE IF EXISTS `pet_category`;
CREATE TABLE `pet_category` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`        VARCHAR(30) NOT NULL COMMENT '分类名称，如：狗狗',
  `icon`        VARCHAR(30) DEFAULT '🐶' COMMENT '分类图标（emoji）',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
  `sort`        INT         NOT NULL DEFAULT 0 COMMENT '显示排序，数字越小越靠前',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物分类表';

-- ----------------------------
-- 2.3 宠物信息表
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`        VARCHAR(50)  NOT NULL COMMENT '宠物昵称',
  `category_id` BIGINT       NOT NULL COMMENT '所属分类ID',
  `age`         VARCHAR(20)  DEFAULT NULL COMMENT '年龄，如：3个月 / 2岁',
  `gender`      VARCHAR(10)  DEFAULT '未知' COMMENT '性别：弟弟/妹妹/未知',
  `city`        VARCHAR(50)  DEFAULT NULL COMMENT '所在城市',
  `price`       DECIMAL(10,2) DEFAULT 0.00 COMMENT '领养费用（0 表示免费领养）',
  `health`      VARCHAR(200) DEFAULT NULL COMMENT '健康状况，如：已接种疫苗',
  `description` VARCHAR(1000) DEFAULT NULL COMMENT '宠物详细介绍',
  `cover_img`   VARCHAR(255) DEFAULT NULL COMMENT '封面图片地址',
  `vaccine`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已接种疫苗：1 是 0 否',
  `sterilized`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已绝育：1 是 0 否',
  `deworm`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已驱虫：1 是 0 否',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 待领养 2 已领养 0 下架',
  `view_count`  INT          NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `user_id`     BIGINT       DEFAULT NULL COMMENT '发布人ID（送养人）',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`),
  KEY `idx_pet_category` (`category_id`),
  KEY `idx_pet_status` (`status`),
  KEY `idx_pet_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物信息表';

-- ----------------------------
-- 2.4 宠物相册表
-- ----------------------------
DROP TABLE IF EXISTS `pet_image`;
CREATE TABLE `pet_image` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_id`      BIGINT       NOT NULL COMMENT '宠物ID',
  `url`         VARCHAR(255) NOT NULL COMMENT '图片地址',
  `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序，数字越小越靠前',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`),
  KEY `idx_image_pet` (`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物相册表';

-- ----------------------------
-- 2.5 领养申请表
-- ----------------------------
DROP TABLE IF EXISTS `adopt_apply`;
CREATE TABLE `adopt_apply` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_id`       BIGINT       NOT NULL COMMENT '申请的宠物ID',
  `user_id`      BIGINT       NOT NULL COMMENT '申请人（领养人）ID',
  `reason`       VARCHAR(500) DEFAULT NULL COMMENT '领养理由',
  `address`      VARCHAR(200) DEFAULT NULL COMMENT '居住地址',
  `phone`        VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `experience`   VARCHAR(500) DEFAULT NULL COMMENT '养宠经验',
  `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0 待审核 1 已通过 2 已驳回 3 已取消',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '审核时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`),
  KEY `idx_apply_pet` (`pet_id`),
  KEY `idx_apply_user` (`user_id`),
  KEY `idx_apply_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

-- ----------------------------
-- 2.6 公告 / 领养资讯表
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`       VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content`     TEXT         COMMENT '公告正文',
  `cover`       VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
  `type`        VARCHAR(20)  NOT NULL DEFAULT '通知' COMMENT '类型：通知/活动/攻略',
  `view_count`  INT          NOT NULL DEFAULT 0 COMMENT '阅读量',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 展示 0 隐藏',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告资讯表';

-- ----------------------------
-- 2.7 留言评论表
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_id`      BIGINT       NOT NULL COMMENT '宠物ID',
  `user_id`     BIGINT       NOT NULL COMMENT '留言用户ID',
  `content`     VARCHAR(500) NOT NULL COMMENT '留言内容',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 显示 0 隐藏',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`),
  KEY `idx_comment_pet` (`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言评论表';

-- ----------------------------
-- 2.8 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `pet_id`      BIGINT   NOT NULL COMMENT '收藏的宠物ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `deleted`     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
  PRIMARY KEY (`id`),
  KEY `idx_fav_user` (`user_id`),
  KEY `idx_fav_pet` (`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='我的收藏表';

-- =====================================================================
-- 3. 测试数据
-- =====================================================================

-- ----------------------------
-- 3.1 用户数据（密码统一为 123456）
-- ----------------------------
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `avatar`, `phone`, `gender`, `city`, `intro`, `role`, `status`) VALUES
('admin',    '123456', '系统管理员', '/uploads/avatar/admin.png',    '13800000001', '男', '北京', '平台管理员，负责内容审核与信息维护', 'admin', 1),
('zhangsan', '123456', '张三',     '/uploads/avatar/zhangsan.png', '13800000002', '男', '上海', '喜欢养狗，已经有两只猫咪了',        'user',  1),
('lisi',     '123456', '李四',     '/uploads/avatar/lisi.png',     '13800000003', '女', '广州', '爱猫人士，有过两年养宠经验',        'user',  1),
('wangwu',   '123456', '王五',     '/uploads/avatar/wangwu.png',   '13800000004', '男', '深圳', '希望领养一只金毛，给孩子做个伙伴',  'user',  1),
('zhaoliu',  '123456', '赵六',     '/uploads/avatar/zhaoliu.png',  '13800000005', '女', '杭州', '上班族，喜欢小动物',                'user',  1),
('sunqi',    '123456', '孙七',     '/uploads/avatar/sunqi.png',    '13800000006', '男', '成都', '正在为流浪猫寻找温暖的家',          'user',  1);

-- ----------------------------
-- 3.2 宠物分类数据
-- ----------------------------
INSERT INTO `pet_category` (`name`, `icon`, `description`, `sort`) VALUES
('狗狗', '🐶', '忠诚可爱的狗狗，等待一个温暖的家', 1),
('猫咪', '🐱', '软萌粘人的猫咪，给你无尽的治愈',   2),
('兔子', '🐰', '安静温顺的兔子，适合安静的环境',   3),
('其他', '🐾', '其他可爱的小动物，同样值得被爱',   4);

-- ----------------------------
-- 3.3 宠物信息数据
-- ----------------------------
INSERT INTO `pet` (`name`, `category_id`, `age`, `gender`, `city`, `price`, `health`, `description`, `cover_img`, `vaccine`, `sterilized`, `deworm`, `status`, `view_count`, `user_id`, `create_time`) VALUES
('奶糖',   1, '8个月',  '妹妹', '上海', 0.00,  '健康，已接种三针疫苗',     '漂亮的布偶妹妹，性格温顺粘人，喜欢被抱，适合有陪伴时间的主人。', '/uploads/pet/naitang.jpg',   1, 1, 1, 1, 328, 2, '2026-10-01 10:00:00'),
('旺财',   1, '2岁',    '弟弟', '北京', 200.00,'健康，定期驱虫',         '亲人、体贴的中华田园犬，吃百家饭长大，最会看人脸色，谁都喜欢它。', '/uploads/pet/wangcai.jpg',   1, 1, 1, 1, 512, 3, '2026-10-03 14:20:00'),
('可可',   1, '5个月',  '弟弟', '广州', 0.00,  '健康，幼犬待驱虫',       '小柯基一只，屁股大脸可爱，活泼好动，喜欢和人玩球，最爱胡萝卜。',   '/uploads/pet/keke.jpg',      1, 0, 0, 1, 276, 4, '2026-10-05 09:30:00'),
('阿黄',   1, '4岁',    '弟弟', '深圳', 300.00,'健康，年检齐全',         '一只忠诚的金毛，导盲犬般温顺，遛弯后必回门口等，已经带去公园无数次。', '/uploads/pet/ahuang.jpg',  1, 1, 1, 2, 641, 5, '2026-10-07 16:45:00'),
('煤球',   2, '1岁',    '弟弟', '成都', 0.00,  '健康，已绝育',           '一只纯黑的小煤球，安静高冷但其实很黏人，喜欢晒太阳和纸箱。',       '/uploads/pet/meiqiu.jpg',   1, 1, 1, 1, 405, 6, '2026-10-09 11:10:00'),
('团子',   2, '3个月',  '妹妹', '杭州', 0.00,  '健康，幼猫待接种',       '橘色小团子，圆滚滚的特别能吃，见人就喵喵叫，粘人得很。',           '/uploads/pet/tuanzi.jpg',   1, 0, 0, 1, 389, 5, '2026-10-11 08:50:00'),
('小白',   2, '2岁',    '妹妹', '北京', 150.00,'健康，已接种疫苗',       '异瞳小白猫，安静文静，喜欢晒太阳和纸箱，适合喜欢安静的女孩子。',     '/uploads/pet/xiaobai.jpg',   1, 1, 1, 1, 233, 3, '2026-10-13 19:05:00'),
('闪电',   2, '6个月',  '弟弟', '上海', 0.00,  '健康，幼猫待驱虫',       '狸花猫一只，精力旺盛半夜跑酷，但白天会乖乖趴在你腿上睡觉。',       '/uploads/pet/shandian.jpg', 1, 0, 1, 1, 297, 2, '2026-10-15 13:25:00'),
('跳跳',   3, '1岁',    '弟弟', '南京', 0.00,  '健康，已驱虫',           '一只灰色垂耳兔，跳起来像袋鼠，喜欢胡萝卜和提摩西草，非常安静。',     '/uploads/pet/tiaotiao.jpg', 1, 1, 1, 1, 145, 4, '2026-10-17 10:40:00'),
('糖糖',   3, '5个月',  '妹妹', '苏州', 0.00,  '健康，幼兔待驱虫',       '白色垂耳兔一只，非常干净，会自己用厕所，不咬人，适合室内饲养。',     '/uploads/pet/tangtang.jpg', 1, 0, 0, 1, 98,  5, '2026-10-19 15:55:00'),
('咪咪',   4, '2岁',    '妹妹', '武汉', 100.00,'健康，已绝育',           '小区里捡到的小花咪，胆大包天，一点不怕人，认主后特别黏人。',         '/uploads/pet/mimi.jpg',     1, 1, 1, 1, 176, 6, '2026-10-21 12:00:00'),
('豆豆',   4, '1岁',    '弟弟', '长沙', 0.00,  '健康，已驱虫',           '一只憨憨的小仓鼠，胖乎乎的喜欢囤食，生命力很顽强，好养活。',         '/uploads/pet/doudou.jpg',   1, 1, 0, 1, 87,  2, '2026-10-23 17:35:00');

-- ----------------------------
-- 3.4 宠物相册数据
-- ----------------------------
INSERT INTO `pet_image` (`pet_id`, `url`, `sort`) VALUES
(1, '/uploads/pet/naitang.jpg', 1), (1, '/uploads/pet/naitang2.jpg', 2),
(2, '/uploads/pet/wangcai.jpg', 1), (2, '/uploads/pet/wangcai2.jpg', 2),
(3, '/uploads/pet/keke.jpg', 1),
(4, '/uploads/pet/ahuang.jpg', 1), (4, '/uploads/pet/ahuang2.jpg', 2),
(5, '/uploads/pet/meiqiu.jpg', 1),
(6, '/uploads/pet/tuanzi.jpg', 1), (6, '/uploads/pet/tuanzi2.jpg', 2),
(7, '/uploads/pet/xiaobai.jpg', 1),
(8, '/uploads/pet/shandian.jpg', 1),
(9, '/uploads/pet/tiaotiao.jpg', 1),
(10, '/uploads/pet/tangtang.jpg', 1),
(11, '/uploads/pet/mimi.jpg', 1),
(12, '/uploads/pet/doudou.jpg', 1);

-- ----------------------------
-- 3.5 领养申请数据（0 待审核 1 已通过 2 已驳回 3 已取消）
-- ----------------------------
INSERT INTO `adopt_apply` (`pet_id`, `user_id`, `reason`, `address`, `phone`, `experience`, `status`, `audit_remark`, `create_time`) VALUES
(1,  3, '特别喜欢布偶，性格温柔，我和女友都很喜欢猫，会好好照顾它。', '上海市浦东新区世纪大道100号', '13800000003', '养过一只猫三年直到去世', 1, '资料齐全，符合领养条件', '2026-10-02 10:30:00'),
(2,  4, '想给家里孩子找个伴，孩子正需要一个亲近的伙伴陪伴。',         '北京市朝阳区建国路88号',     '13800000004', '第一次养狗，愿意学习',   1, '同意上门回访',            '2026-10-06 09:20:00'),
(3,  5, '有独立住房，室内面积120平，适合养大型犬。',                   '广州市天河区体育西路20号',   '13800000005', '养过金毛五年',            0, NULL,                      '2026-11-15 14:10:00'),
(5,  3, '喜欢安静的猫，每天加班晚，需要一个安静陪伴的伙伴。',         '上海市徐汇区漕溪北路5号',   '13800000003', '养猫经验三年',            1, '已通过',                  '2026-10-11 16:40:00'),
(6,  6, '刚毕业工作稳定，想养一只橘猫陪伴加班的自己。',               '成都市武侯区天府大道1号',   '13800000006', '无经验，愿意学习',        0, NULL,                      '2026-11-16 20:15:00'),
(7,  4, '想给女儿养一只温柔的小白猫，女孩子和猫最配了。',             '北京市海淀区中关村大街1号', '13800000004', '幼时养过兔子',            2, '暂不建议领养，待完善居住信息', '2026-10-18 11:25:00'),
(8,  5, '独居，房子小但朝南有阳光，猫可以晒太阳。',                   '杭州市西湖区文一西路100号',  '13800000005', '养过狸花猫',              3, '用户主动取消',            '2026-10-20 08:50:00'),
(12, 3, '想给办公室添一只小可爱，缓解加班压力。',                     '上海市静安区南京西路9号',   '13800000003', '喜欢小动物',              0, NULL,                      '2026-11-18 13:05:00');

-- ----------------------------
-- 3.6 公告资讯数据
-- ----------------------------
INSERT INTO `notice` (`title`, `content`, `cover`, `type`, `view_count`, `status`, `create_time`) VALUES
('欢迎来到 Java1234 宠物领养平台', '感谢各位爱宠人士的信任。本平台致力于为流浪动物与待领养宠物搭建桥梁，让每一只小动物都能找到温暖的家。欢迎发布领养信息，参与公益领养活动。', '/uploads/notice/welcome.jpg', '通知', 1024, 1, '2026-09-28 09:00:00'),
('领养流程指南', '第一步：在小程序首页浏览宠物列表并收藏心仪的宠物；第二步：进入详情页填写领养申请，说明领养理由与居住情况；第三步：等待送养人及平台审核；第四步：审核通过后携带身份证到指定地点交接，并签订领养协议；第五步：定期回访上传照片，我们会陪伴你整个养宠周期。', '/uploads/notice/guide.jpg', '攻略', 876, 1, '2026-09-30 10:30:00'),
('秋季公益领养活动启动', '即日起至十一月底，平台联合多家宠物医院提供免费体检、疫苗接种和驱虫服务。所有通过审核的领养人可凭审核通过短信享受专属优惠，名额有限，先到先得。', '/uploads/notice/activity.jpg', '活动', 1533, 1, '2026-10-05 14:00:00'),
('关于规范养宠与科学育宠的倡议', '领养不是一时兴起，而是一份长达十几年的承诺。请做到科学喂养、定期免疫、按时驱虫、不弃养、不虐待。请为您的宠物办证并做好绝育，给它一个稳定而温暖的家。', '/uploads/notice/advise.jpg', '攻略', 692, 1, '2026-10-12 09:45:00'),
('平台新增城市分站', '为方便更多用户，本平台陆续开通城市分站，目前已完成上海、北京、广州、深圳、成都、杭州等城市的领养服务覆盖。用户可在发布宠物时选择所在城市，优先展示同城信息。', NULL, '通知', 418, 1, '2026-10-20 16:20:00');

-- ----------------------------
-- 3.7 留言评论数据
-- ----------------------------
INSERT INTO `comment` (`pet_id`, `user_id`, `content`, `status`, `create_time`) VALUES
(1,  3, '布偶妹妹好可爱，已经提交申请啦，希望能领养成功！', 1, '2026-10-02 15:12:00'),
(1,  4, '好乖的小猫，看着照片都想rua一把。',                 1, '2026-10-03 09:40:00'),
(2,  5, '田园犬亲人又好养，希望我能领到它。',                 1, '2026-10-07 19:22:00'),
(4,  3, '金毛太帅了，忠诚度爆表，孩子一定喜欢。',               1, '2026-10-08 10:05:00'),
(5,  2, '黑猫其实很粘人的，我家那只天天跟着我。',             1, '2026-10-10 12:36:00'),
(6,  6, '橘猫能吃是福，记得少喂点罐头哦。',                     1, '2026-10-12 08:15:00'),
(6,  4, '求求了让我带它回家！',                                1, '2026-10-12 21:48:00'),
(7,  5, '异瞳好美，是我的梦中情猫。',                         1, '2026-10-14 11:30:00'),
(9,  3, '垂耳兔好可爱，安静适合公寓。',                       1, '2026-10-18 17:02:00'),
(11, 2, '小花咪不怕人真好，我小时候也养过一只。',               1, '2026-10-22 13:55:00');

-- ----------------------------
-- 3.8 收藏数据
-- ----------------------------
INSERT INTO `favorite` (`user_id`, `pet_id`, `create_time`) VALUES
(3, 1,  '2026-10-02 14:00:00'),
(3, 9,  '2026-10-18 16:50:00'),
(3, 12, '2026-10-23 18:10:00'),
(4, 2,  '2026-10-06 08:30:00'),
(4, 6,  '2026-10-11 20:15:00'),
(5, 4,  '2026-10-09 10:25:00'),
(5, 7,  '2026-10-15 11:40:00'),
(6, 5,  '2026-10-11 15:35:00'),
(2, 1,  '2026-10-01 20:45:00'),
(2, 4,  '2026-10-08 09:10:00');

-- =====================================================================
-- 4. 验证数据
-- =====================================================================
SELECT '用户数' AS 统计项, COUNT(*) AS 数量 FROM sys_user
UNION ALL SELECT '宠物数', COUNT(*) FROM pet
UNION ALL SELECT '分类数', COUNT(*) FROM pet_category
UNION ALL SELECT '相册图片', COUNT(*) FROM pet_image
UNION ALL SELECT '领养申请', COUNT(*) FROM adopt_apply
UNION ALL SELECT '公告', COUNT(*) FROM notice
UNION ALL SELECT '留言', COUNT(*) FROM comment
UNION ALL SELECT '收藏', COUNT(*) FROM favorite;

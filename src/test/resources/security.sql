/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.99.100
 Source Server Type    : MySQL
 Source Server Version : 50710
 Source Host           : 192.168.99.100
 Source Database       : security

 Target Server Type    : MySQL
 Target Server Version : 50710
 File Encoding         : utf-8

 Date: 06/02/2017 18:37:51 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `sec_resource`
-- ----------------------------
DROP TABLE IF EXISTS `sec_resource`;
CREATE TABLE `sec_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `res_key` varchar(100) DEFAULT NULL,
  `res_desc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `sec_resource`
-- ----------------------------
BEGIN;
INSERT INTO `sec_resource` VALUES ('1', '/test1', '测试1'), ('2', '/test2', '测试2');
COMMIT;

-- ----------------------------
--  Table structure for `sec_role`
-- ----------------------------
DROP TABLE IF EXISTS `sec_role`;
CREATE TABLE `sec_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) DEFAULT NULL,
  `role_desc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `sec_role`
-- ----------------------------
BEGIN;
INSERT INTO `sec_role` VALUES ('1', 'ROLE_ADMIN', '管理员'), ('2', 'ROLE_USER', '一般用户');
COMMIT;

-- ----------------------------
--  Table structure for `sec_role_resource`
-- ----------------------------
DROP TABLE IF EXISTS `sec_role_resource`;
CREATE TABLE `sec_role_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL,
  `resource_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `sec_role_resource`
-- ----------------------------
BEGIN;
INSERT INTO `sec_role_resource` VALUES ('1', '1', '1'), ('2', '1', '2'), ('3', '2', '1');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

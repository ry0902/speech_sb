/*
Navicat MySQL Data Transfer

Source Server         : Ry
Source Server Version : 80012
Source Host           : localhost:3306
Source Database       : speech

Target Server Type    : MYSQL
Target Server Version : 80012
File Encoding         : 65001

Date: 2021-02-09 10:37:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'teacher1', '123456');
INSERT INTO `user` VALUES ('2', 'ry', '123456');

-- ----------------------------
-- Table structure for user_video
-- ----------------------------
DROP TABLE IF EXISTS `user_video`;
CREATE TABLE `user_video` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `upload_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `video_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_video
-- ----------------------------
INSERT INTO `user_video` VALUES ('11', '2', 'D:\\myFile2\\Rycode\\speech_recognition\\manage_sb\\videos\\1612702791182yanjiang10min.mp4', '2021-02-07 20:59:51', '暂无视频说明!', 'yanjiang10min.mp4');
INSERT INTO `user_video` VALUES ('12', '2', 'D:\\myFile2\\Rycode\\speech_recognition\\manage_sb\\videos\\1612770830634sabeining1min.mp4', '2021-02-08 15:53:51', '暂无视频说明!', 'sabeining1min.mp4');

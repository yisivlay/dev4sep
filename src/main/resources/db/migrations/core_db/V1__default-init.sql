CREATE DATABASE /*!32312 IF NOT EXISTS*/`dev4sep-default` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `dev4sep-default`;

DROP TABLE IF EXISTS `offices`;
CREATE TABLE `offices` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `hierarchy` varchar(100) DEFAULT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `opening_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `external_id` (`external_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `parent_id` FOREIGN KEY (`parent_id`) REFERENCES `offices` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

insert  into `offices`(`id`,`parent_id`,`hierarchy`,`external_id`,`name`,`opening_date`) values 
(1,NULL,'.','1','Head Office','2024-02-03');

DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `grouping` varchar(45) DEFAULT NULL,
  `code` varchar(100) NOT NULL,
  `entity_name` varchar(100) DEFAULT NULL,
  `action_name` varchar(100) DEFAULT NULL,
  `can_maker_checker` tinyint(1) NOT NULL DEFAULT '1',
  `is_visible` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

insert  into `permissions`(`id`,`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`,`is_visible`) values 
(1,'special','ALL_FUNCTIONS',NULL,NULL,0,1),
(2,'special','ALL_FUNCTIONS_READ',NULL,NULL,0,1),
(3,'special','CHECKER_SUPER_USER',NULL,NULL,0,1),
(4,'special','REPORTING_SUPER_USER',NULL,NULL,0,1);

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) DEFAULT NULL,
  `username` varchar(100) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `dob` date DEFAULT NULL,
  `gender` tinyint(1) DEFAULT NULL COMMENT '1=M;2=F',
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `activated_date` datetime DEFAULT NULL,
  `activation_key` varchar(50) DEFAULT NULL,
  `lang` varchar(20) DEFAULT 'en-US',
  `account_non_expired` tinyint(1) NOT NULL DEFAULT '1',
  `account_non_locked` tinyint(1) NOT NULL DEFAULT '1',
  `credentials_non_expired` tinyint(1) NOT NULL DEFAULT '1',
  `password_never_expires` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'define if the password, should be check for validity period or not',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `office_id` (`office_id`),
  CONSTRAINT `office_id_ibfk_1` FOREIGN KEY (`office_id`) REFERENCES `offices` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

insert  into `users`(`id`,`office_id`,`username`,`firstname`,`lastname`,`fullname`,`password`,`dob`,`gender`,`email`,`phone`,`address`,`activated_date`,`activation_key`,`lang`,`account_non_expired`,`account_non_locked`,`credentials_non_expired`,`password_never_expires`,`enabled`,`is_deleted`) values
(1,1,'system','System','User','User System','$2a$10$ujZdKtitPoMkd2icuOQCa.C0zJq8ciXa52/pNSikJ5CwkW8tR4y.S','2024-02-03',1,'dev4sep@info.com',NULL,'Phnom Penh',NULL,NULL,'kh-KM',1,1,1,0,1,0),
(2,1,'admin','Admin','User','User Admin','$2a$10$ujZdKtitPoMkd2icuOQCa.C0zJq8ciXa52/pNSikJ5CwkW8tR4y.S','2024-02-03',1,'admin@info.com','855884777754','Phnom Penh',NULL,NULL,'kh-KM',1,1,1,0,1,0);

DROP TABLE IF EXISTS `portfolio_command_sources`;
CREATE TABLE `portfolio_command_sources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_name` varchar(50) NOT NULL,
  `entity_name` varchar(50) NOT NULL,
  `office_id` bigint(20) DEFAULT NULL,
  `api_get_url` varchar(100) NOT NULL,
  `resource_id` bigint(20) DEFAULT NULL,
  `subresource_id` bigint(20) DEFAULT NULL,
  `maker_id` bigint(20) NOT NULL,
  `made_on_date` datetime NOT NULL,
  `checker_id` bigint(20) DEFAULT NULL,
  `checked_on_date` datetime DEFAULT NULL,
  `processing_result_enum` smallint(5) NOT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `command_as_json` longtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `maker_id` (`maker_id`),
  KEY `checker_id` (`checker_id`),
  KEY `action_name` (`action_name`),
  KEY `entity_name` (`entity_name`,`resource_id`),
  KEY `made_on_date` (`made_on_date`),
  KEY `checked_on_date` (`checked_on_date`),
  KEY `processing_result_enum` (`processing_result_enum`),
  KEY `office_id` (`office_id`),
  CONSTRAINT `checker_id_ibfk_1` FOREIGN KEY (`checker_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `maker_id_ibfk_2` FOREIGN KEY (`maker_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) NOT NULL,
  `code` varchar(50) DEFAULT NULL,
  `is_disabled` tinyint(1) NOT NULL DEFAULT '0',
  `is_for_sign_up_process` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

insert  into `roles`(`id`,`name`,`description`,`code`,`is_disabled`,`is_for_sign_up_process`) values
(1,'Super User','This position gives permission for all users','role.code.super-user',0,0);

DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `permission_id` (`permission_id`),
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

insert  into `role_permissions`(`role_id`,`permission_id`) values 
(1,1);

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `role_id_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `user_id_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
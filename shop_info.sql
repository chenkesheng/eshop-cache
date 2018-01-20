/*
SQLyog Professional v12.09 (64 bit)
MySQL - 5.7.20 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

create table `shop_info` (
	`id` bigint (11),
	`name` varchar (765),
	`level` int (11),
	`good_comment_rate` double 
); 
insert into `shop_info` (`id`, `name`, `level`, `good_comment_rate`) values('1','cks的手机店','5','0.99');

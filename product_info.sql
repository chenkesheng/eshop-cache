/*
SQLyog Professional v12.09 (64 bit)
MySQL - 5.7.20 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

create table `product_info` (
	`id` int (11),
	`name` varchar (765),
	`price` double ,
	`picture_list` varchar (765),
	`specification` varchar (765),
	`service` varchar (765),
	`color` varchar (96),
	`size` varchar (96),
	`shop_id` bigint (11),
	`update_time` varchar (192)
); 
insert into `product_info` (`id`, `name`, `price`, `picture_list`, `specification`, `service`, `color`, `size`, `shop_id`, `update_time`) values('1','iphone8手机','8888','a.jpg,b.jpg','iphone8的规格','iphone8的售后服务','红色,黑色,白色','5.5','1','2018-1-20 16:28:13');
insert into `product_info` (`id`, `name`, `price`, `picture_list`, `specification`, `service`, `color`, `size`, `shop_id`, `update_time`) values('2','iphone8手机','8888','a.jpg,b.jpg','iphone8的规格','iphone8的售后服务','红色,黑色,白色','5.5','2','2018-1-20 16:28:01');

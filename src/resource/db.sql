

create database kome default character set utf8;

create user guest identified by 'guest';
grant all privileges on kome@'%' to guest;

create table operator (
  id int not null primary key auto_increment,
  name varchar(255) not null,
  email varchar(255) null
);

create table spider_url(
  id bigint primary key auto_increment,
  url text not null,
  from_doc_url text comment '来源RUL',
  create_time date comment '创建时间',
  update_time date comment '更新时间'
);

create table spider_file(
  id bigint primary key auto_increment,
  type varchar(10) comment '文件类型: image,mp3,vedio',
  from_doc_url text comment '来源URL',
  identifier varchar(255) comment '文件名，文件 md5加后缀名',
  size bigint comment '文件大小',
  create_time date comment '创建时间',
  update_time date comment '更新时间'
);
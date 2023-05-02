drop database if exists tvtracker;
create database tvtracker;
use tvtracker;

create table user(
userid int primary key auto_increment,
username varchar(50) NOT NULL unique,
password varchar(50) NOT NULL,
Role varchar(5) NOT NULL 
);

create table shows(
showid int primary key auto_increment,
name varchar(50) NOT NULL,
episodes int NOT NULL
);

create table user_shows(
userid int,
showid int,
episodes int NOT NULL,
rating int NOT NULL DEFAULT 0,
primary key(userid, showid),
foreign key(userid) references user (userid),
foreign key(showid) references shows (showid)
);


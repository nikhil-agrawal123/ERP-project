create database Auth;
use Auth;
create table StudentAuth(
	id int auto_increment primary key,
    studentId varchar(100) not null,
    studentPass text not null
);

create table facultyAuth(
	id int auto_increment primary key,
    facultyId varchar(50) not null,
    facultyPass text not null
);

create table adminAuth(
	id int auto_increment primary key,
    adminId varchar(50) not null,
    adminName varchar(50) not null,
    adminPass text not null
);

create table parentAuth(
	id int auto_increment primary key,
    studentId varchar(50) not null,
    parentPass text not null
);
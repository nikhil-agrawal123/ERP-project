create database Users;
use Users;
create table Student(
    id int auto_increment key,
    studentId int not null,
    studentName varchar(100) not null,
    studentRollNumber int not null,
    registerCourses json ,
    completedCourses json,
    currentSemester int not null ,
    currentCGPA double not null ,
    currentCredits int not null,
    feeHistory json
);



alter table student
    add column studentId int not null;

alter table student
    add column studentEmail varchar(100) not null;

alter table student
    add column numberCourses int not null ;

alter table Users.student MODIFY COLUMN studentId VARCHAR(100);


drop table Student;

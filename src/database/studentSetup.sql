create database Users;
use Users;
create table Student(
    id int auto_increment primary key,
    studentName varchar(100) not null,
    studentRollNumber int not null,
    registerCourses json ,
    completedCourses json,
    currentSemester int not null ,
    currentCGPA double not null ,
    currentCredits int not null,
    feeHistory json
);

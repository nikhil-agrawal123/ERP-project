insert into auth.studentauth(id, studentId, studentPass)
values (1,"nikhil", "nikhil");

insert into auth.studentauth
values
       (2,"gaurav","gaurav"),
       (3,"aditri", "aditri");

ALTER TABLE auth.studentauth MODIFY COLUMN studentPass VARCHAR(255);

update auth.studentauth
set studentPass = "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje"
where id = 1;

update auth.studentauth
set studentPass = "$2a$10$9QdGmKb4mI4UqSgRB2Eqpud6Ix/eawbo7Oc8WiAcKr/R92A3qXtXW"
where id = 2;

insert into auth.facultyauth(facultyId, facultyPass)
values ("nikhil","nikhil"),
       ("gaurav", "gaurav"),
       ("aditri", "aditri");

update auth.facultyauth
set facultyPass = "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje"
where id = 1;

update auth.facultyauth
set facultyPass = "$2a$10$9QdGmKb4mI4UqSgRB2Eqpud6Ix/eawbo7Oc8WiAcKr/R92A3qXtXW"
where id = 2;

insert into auth.parentauth(studentId, parentPass)
values
    ( "nikhil", "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje"),
    ("gaurav","gaurav"),
    ("aditri" , "aditri");

insert  into auth.adminauth(adminId, adminName, adminPass)
values
    ( "nikhil","nikhil", "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje");

use users;
insert into users.student (studentName, studentRollNumber, numberCourses,registerCourses, completedCourses, currentSemester, currentCGPA, currentCredits, feeHistory, studentId,studentEmail)
values ("nikhil" , "2024380",4, '{"1": {"course_code": "MTH201" , "course_name":"Real Analysis" ,"course_instructor" : "Namambita Ray","course_credits" : 4 }}','{
  "1": {
    "course_code": "MTH201",
    "course_name": "Real Analysis",
    "course_instructor": "Namambita Ray",
    "course_credits": 4
  }
}' , 2 ,8.0 , 40 ,'{"1": {"course_code": "MTH201" , "course_name":"Real Analysis" ,"course_instructor" : "Namambita Ray","course_credits" : 4 }}',2024380,"nikhil24380@iiitd.ac.in");

UPDATE users.student
set studentId = "nikhil"
where id = 1;

update users.student
set studentEmail = "nikhil24380@iiitd.ac.in"
where id = 1;
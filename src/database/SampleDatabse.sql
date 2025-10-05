insert into studentauth(id, studentId, studentPass)
values (1,"nikhil", "nikhil");

insert into studentauth
values
       (2,"gaurav","gaurav"),
       (3,"aditri", "aditri");

ALTER TABLE auth.studentauth MODIFY COLUMN studentPass VARCHAR(255);

update auth.studentauth
set studentPass = "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje"
where id = 1;

insert into facultyauth(facultyId, facultyPass)
values ("nikhil","nikhil"),
       ("gaurav", "gaurav"),
       ("aditri", "aditri");

update auth.facultyauth
set facultyPass = "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje"
where id = 1;

insert into parentauth(studentId, parentPass)
values
    ( "nikhil", "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje"),
    ("gaurav","gaurav"),
    ("aditri" , "aditri");

insert  into adminauth(adminId, adminName, adminPass)
values
    ( "nikhil","nikhil", "$2a$10$Kc/0I11BVSsNDxsuYAsJDekSvfAze0t7auWjff5OF6oC/anfVdVje");


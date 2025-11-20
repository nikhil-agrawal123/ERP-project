SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS auth.studentauth;
DROP TABLE IF EXISTS auth.parentauth;
DROP TABLE IF EXISTS auth.facultyauth;
DROP TABLE IF EXISTS auth.adminauth;

DROP TABLE IF EXISTS users.grades;
DROP TABLE IF EXISTS users.enrollments;
DROP TABLE IF EXISTS users.sections;
DROP TABLE IF EXISTS users.gradingpolicy;
DROP TABLE IF EXISTS users.students;
DROP TABLE IF EXISTS users.instructors;
DROP TABLE IF EXISTS users.courses;

SET FOREIGN_KEY_CHECKS = 1;

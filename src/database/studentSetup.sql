-- -----------------------------------------------------
-- Table `students`
-- `user_id` is now a VARCHAR to match the Auth DB's potential username format.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `students` (
                                          `user_id` VARCHAR(50) NOT NULL,
                                          `student_roll_no` VARCHAR(45) NOT NULL,
                                          `full_name` VARCHAR(100) NOT NULL,
                                          `program` VARCHAR(100) NULL,
                                          `enrollment_year` INT NULL,
                                          PRIMARY KEY (`user_id`),
                                          UNIQUE INDEX `student_roll_no_UNIQUE` (`student_roll_no` ASC)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `instructors`
-- `user_id` is a VARCHAR.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `instructors` (
                                             `user_id` VARCHAR(50) NOT NULL,
                                             `instructor_id` VARCHAR(45) NOT NULL,
                                             `full_name` VARCHAR(100) NOT NULL,
                                             `department` VARCHAR(100) NULL,
                                             `email` VARCHAR(100) NULL,
                                             PRIMARY KEY (`user_id`),
                                             UNIQUE INDEX `instructor_id_UNIQUE` (`instructor_id` ASC)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `courses`
-- `course_code` is now the PRIMARY KEY for simplicity and efficiency.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `courses` (
                                         `course_code` VARCHAR(10) NOT NULL,
                                         `course_title` VARCHAR(100) NOT NULL,
                                         `credits` INT NOT NULL,
                                         PRIMARY KEY (`course_code`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `sections`
-- `instructor_id` is now VARCHAR to match `instructors.user_id`.
-- `course_code` is used as the foreign key to match the new `courses` primary key.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sections` (
                                          `section_id` INT NOT NULL AUTO_INCREMENT,
                                          `course_code` VARCHAR(10) NOT NULL,
                                          `instructor_id` VARCHAR(50) NOT NULL, -- This is the user_id of the instructor
                                          `semester` VARCHAR(45) NOT NULL,
                                          `year` INT NOT NULL,
                                          `capacity` INT NOT NULL,
                                          PRIMARY KEY (`section_id`),
                                          INDEX `fk_sections_courses_idx` (`course_code` ASC),
                                          INDEX `fk_sections_instructors_idx` (`instructor_id` ASC),
                                          CONSTRAINT `fk_sections_courses`
                                              FOREIGN KEY (`course_code`)
                                                  REFERENCES `courses` (`course_code`),
                                          CONSTRAINT `fk_sections_instructors`
                                              FOREIGN KEY (`instructor_id`)
                                                  REFERENCES `instructors` (`user_id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `enrollments`
-- `student_id` is now VARCHAR to match `students.user_id`.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `enrollments` (
                                             `enrollment_id` INT NOT NULL AUTO_INCREMENT,
                                             `student_id` VARCHAR(50) NOT NULL, -- This is the user_id of the student
                                             `section_id` INT NOT NULL,
                                             `enrollment_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             PRIMARY KEY (`enrollment_id`),
                                             UNIQUE INDEX `student_section_UNIQUE` (`student_id`, `section_id`),
                                             INDEX `fk_enrollments_students_idx` (`student_id` ASC),
                                             INDEX `fk_enrollments_sections_idx` (`section_id` ASC),
                                             CONSTRAINT `fk_enrollments_students`
                                                 FOREIGN KEY (`student_id`)
                                                     REFERENCES `students` (`user_id`),
                                             CONSTRAINT `fk_enrollments_sections`
                                                 FOREIGN KEY (`section_id`)
                                                     REFERENCES `sections` (`section_id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `grades`
-- No changes needed here, as it links to `enrollments` which is fixed.
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `grades` (
                                        `student_roll_no` VARCHAR(45) NOT NULL,
                                        `course_code` VARCHAR(10) NOT NULL,
                                        `semester` INT NOT NULL,
                                        `year` INT NOT NULL,
                                        `score` DECIMAL(5, 2) NOT NULL,
                                        PRIMARY KEY (`student_roll_no`),
                                        INDEX `fk_grades_students_idx` (`student_roll_no` ASC),
                                        INDEX `fk_grades_courses_idx` (`course_code` ASC),
                                        CONSTRAINT `fk_grades_students`
                                            FOREIGN KEY (`student_roll_no`)
                                                REFERENCES `students` (`student_roll_no`),
                                        CONSTRAINT `fk_grades_courses`
                                            FOREIGN KEY (`course_code`)
                                                REFERENCES `courses` (`course_code`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Insert Sample Data (Corrected and Consistent)
-- -----------------------------------------------------

-- 0. Create user profiles first
INSERT INTO `students` (`user_id`, `student_roll_no`, `full_name`, `program`, `enrollment_year`) VALUES
                                                                                                     ('nikhil24380', '2024380', 'Nikhil Agrawal', 'Computer Science and Applied Mathematics', 2024),
                                                                                                     ('rohan24390', '2024390', 'Rohan Verma', 'Mechanical Enginering', 2024)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

INSERT INTO `instructors` (`user_id`, `instructor_id`, `full_name`, `department`, `email`) VALUES
    ('alok', 'INST-CS-501', 'Dr. Alok Gupta', 'Computer Science', 'agupta@university.edu')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 1. Create two courses
INSERT INTO `courses` (`course_code`, `course_title`, `credits`) VALUES
                                                                     ('CS101', 'Introduction to Programming', 4),
                                                                     ('PHY101', 'General Physics I', 4)
ON DUPLICATE KEY UPDATE course_title=VALUES(course_title);

-- 2. Create a section for CS101 taught by Dr. Alok Gupta (user_id 'alok_g')
INSERT INTO `sections` (`course_code`, `instructor_id`, `semester`, `year`, `capacity`) VALUES
    ('CS101', 'alok', 'Fall', 2025, 50);

INSERT INTO `enrollments` (`student_id`, `section_id`) VALUES
                                                           ('nikhil24380', (SELECT section_id FROM sections WHERE course_code = 'CS101' AND instructor_id = 'alok')),
                                                           ('rohan24390', (SELECT section_id FROM sections WHERE course_code = 'CS101' AND instructor_id = 'alok'));

INSERT INTO `grades` (`enrollment_id`, `assessment_type`, `score`) VALUES
    ((SELECT enrollment_id FROM enrollments WHERE student_id = 'nikhil24380' LIMIT 1), 'Midterm Exam', 85.50);

alter table students add column student_email varchar(50);
alter table instructors add column instructor_email varchar(50)

use auth;
update studentauth
set studentId = "nikhil24380"
where id = 1;

use users;

update students
set students.student_email = "nikhil24380@iiitd.ac.in"
where user_id = "nikhil24380";

INSERT INTO `grades` (`student_roll_no`, `course_code`, `semester`, `year`, `score`) VALUES
    ('2024380', 'CS101', '1', 2025, 85.50);

alter table grades add column credits int not null
update grades
set grades.credits = 4
where student_roll_no = '2024380' and course_code ='CS101';

alter table courses add column course_type varchar(50);
update courses
set course_type = "mandatory"
where course_code = "CS101";
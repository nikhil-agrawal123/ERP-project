# 🎓 College ERP System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000f?style=for-the-badge&logo=mysql&logoColor=white)

A role-based College Management System built with **Java** and **MySQL**. It streamlines academic operations including course registration, grading, and system maintenance.

---

## 🚀 Features

### 🛡️ Admin
* **User Management:** Add or remove Students and Faculty members.
* **Course Control:** Create/Delete courses and assign Faculty to them.
* **Registration Window:** Set specific start/end dates for when students can add/drop courses.
* **Maintenance:**
    * **Backup:** One-click SQL dump of the database.
    * **Restore:** Restore database from previous backup files.

### 👨‍🏫 Faculty
* **Grading:** Enter marks and calculate GPA for students in their assigned courses.
* **Policies:** Define and upload grading policies/syllabus for their subjects.
* **Rosters:** View enrolled students per semester.

### 👨‍🎓 Student
* **Registration:** Register or Drop courses (active only during the Admin-defined time window).
* **Results:** View finalized grades and semester GPA.
* **Profile:** View personal details and enrolled course schedule.

---

## 🛠 Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Java (Swing/JavaFX) |
| **Database** | MySQL 8.0 |
| **Connectivity** | JDBC |
| **Backup Tool** | `mysqldump` (accessed via Java ProcessBuilder) |

---

## ⚙️ Setup & Installation

1.  **Database Setup**
    Create the database and run the schema script:
    ```sql
    CREATE DATABASE college_erp;
    USE college_erp;
    
    -- Users Table (Role: 'admin', 'faculty', 'student')
    CREATE TABLE users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) UNIQUE,
        password VARCHAR(50),
        role VARCHAR(20)
    );

    -- Courses Table
    CREATE TABLE courses (
        course_id VARCHAR(10) PRIMARY KEY,
        name VARCHAR(100),
        faculty_id INT,
        credits INT
    );
    ```

2.  **Configuration**
    Update `src/util/DBConnection.java`:
    ```java
    String URL = "jdbc:mysql://localhost:3306/college_erp";
    String USER = "root";
    String PASS = "your_password";
    ```

3.  **System Requirements**
    * Ensure **MySQL Server** is running.
    * For the **Backup** feature to work, ensure `mysqldump` is in your system's Environment Variables (PATH).

4.  **Run**
    * Execute `Main.java` to launch the login screen.
    * Default Admin Credentials: `admin` / `admin123` (Insert this into DB manually first).

---

## 📂 Project Structure

```text
college-erp/
├── src/
│   ├── admin/       # BackupLogic.java, UserManage.java
│   ├── faculty/     # Grading.java, PolicyUpload.java
│   ├── student/     # CourseReg.java, ViewGrades.java
│   └── util/        # DBConnection.java
├── lib/
│   └── mysql-connector-java.jar
├── backups/         # Folder where SQL backups are saved
└── README.md

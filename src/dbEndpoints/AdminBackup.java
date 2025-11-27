package dbEndpoints;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdminBackup {
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";
    private static final String DB_NAME = "users";
    private static final String DB_NAME_AUTH = "auth"; // Backup both if needed
    private static final String MYSQL_PATH = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\";

    /**
     * Creates a SQL dump of the database to the specified file.
     */
    public boolean createBackup(File destinationFile) {
        // Command: mysqldump -u [user] -p[pass] --databases [db1] [db2] -r [file]
        String dumpCommand = MYSQL_PATH + "mysqldump";

        List<String> command = List.of(
                dumpCommand,
                "-u" + DB_USER,
                "-p" + DB_PASS,
                "--databases",
                DB_NAME,
                DB_NAME_AUTH,
                "-r",
                destinationFile.getAbsolutePath()
        );

        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process process = pb.start();
            int processComplete = process.waitFor();
            return processComplete == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restores the database from a SQL file.
     * WARNING: This overwrites existing data.
     */
    public boolean restoreBackup(File sourceFile) {
        // Command: mysql -u [user] -p[pass] < [file]
        String importCommand = MYSQL_PATH + "mysql";

        List<String> command = List.of(
                importCommand,
                "-u" + DB_USER,
                "-p" + DB_PASS
        );

        ProcessBuilder pb = new ProcessBuilder(command);
        // Redirect the process input to come from the file
        pb.redirectInput(sourceFile);

        try {
            Process process = pb.start();
            int processComplete = process.waitFor();
            return processComplete == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package ui.service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import dbClasses.StudentRegisteredCourse;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A service class responsible for generating PDF reports.
 * It contains all the iTextPDF logic, keeping the UI clean.
 */
public class PdfExportService {

    // Define colors for the PDF (matching your UI theme)
    private static final Color COLOR_PRIMARY = new DeviceRgb(52, 159, 148);
    private static final Color COLOR_HEADER_BG = new DeviceRgb(54, 59, 74);
    private static final Color COLOR_TEXT_LIGHT = new DeviceRgb(255, 255, 255);
    private static final Color COLOR_TEXT_MUTED = new DeviceRgb(179, 179, 179);

    /**
     * Generates a PDF report of a student's courses.
     * @param semesterData The map of data to export.
     * @param username The student's username for the report title.
     * @param destinationFile The File object where the PDF will be saved.
     * @return true on success, false on failure.
     */
    public boolean exportStudentReport(Map<Integer, List<StudentRegisteredCourse>> semesterData, String username, File destinationFile) {

        try (PdfWriter writer = new PdfWriter(destinationFile);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // --- 1. Add Report Title ---
            Paragraph title = new Paragraph("Student Course Report")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(COLOR_PRIMARY);
            document.add(title);

            // --- 2. Add Student Name Subtitle ---
            Paragraph subtitle = new Paragraph("Student: " + username)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(COLOR_TEXT_MUTED);
            document.add(subtitle);

            document.add(new Paragraph("\n")); // Add some space

            // --- 3. Loop through each semester and create a table ---
            for (int i = 1; i < 9; i++) {
                if (semesterData.containsKey(i)) {
                    List<StudentRegisteredCourse> courses = semesterData.get(i);

                    // Add Semester Heading
                    Paragraph semTitle = new Paragraph("Semester " + i)
                            .setFontSize(16)
                            .setBold()
                            .setFontColor(COLOR_PRIMARY)
                            .setMarginTop(15);
                    document.add(semTitle);

                    // Create Table
                    float[] columnWidths = {2, 4, 1.5f, 3, 2}; // Relative widths
                    Table table = new Table(UnitValue.createPercentArray(columnWidths));
                    table.setWidth(UnitValue.createPercentValue(100));

                    // --- 4. Add Table Headers ---
                    table.addHeaderCell(createHeaderCell("Code"));
                    table.addHeaderCell(createHeaderCell("Course Name"));
                    table.addHeaderCell(createHeaderCell("Credits"));
                    table.addHeaderCell(createHeaderCell("Offered By"));
                    table.addHeaderCell(createHeaderCell("Grade"));
                    table.addHeaderCell(createHeaderCell("Letter Grade"));

                    // --- 5. Add Table Rows (Data) ---
                    for (StudentRegisteredCourse course : courses) {
                        table.addCell(new Cell().add(new Paragraph(course.getCourseCode())));
                        table.addCell(new Cell().add(new Paragraph(course.getCourseName())));
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(course.getCourseCredits()))
                                .setTextAlignment(TextAlignment.CENTER)));
                        table.addCell(new Cell().add(new Paragraph(course.getOfferedBy())));

                        String grade = (course.getGradePoint() == 0.0) ? "In Progress" : String.valueOf(course.getGradePoint());
                        table.addCell(new Cell().add(new Paragraph(grade)
                                .setTextAlignment(TextAlignment.CENTER)));
                        String letterGrade = course.getGradeLetter();
                        table.addCell(new Cell().add(new Paragraph(letterGrade).setTextAlignment(TextAlignment.CENTER)));
                    }

                    document.add(table); // Add the completed table to the document
                }
            }

            // 6. Close the document - this saves the file
            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to create a styled header cell for the table.
     */
    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBold()
                .setFontColor(COLOR_TEXT_LIGHT)
                .setBackgroundColor(COLOR_HEADER_BG)
                .setTextAlignment(TextAlignment.CENTER);
    }
}
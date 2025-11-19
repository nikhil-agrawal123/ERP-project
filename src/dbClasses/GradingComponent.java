package dbClasses;

/**
 * A simple POJO to represent one row in the grading policy list.
 * e.g., "Midterm Exam", 30
 */
public class GradingComponent {
    private String name;
    private int percentage;

    public GradingComponent(String name, int percentage) {
        this.name = name;
        this.percentage = percentage;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    @Override
    public String toString() {
        return name + ": " + percentage + "%";
    }
}
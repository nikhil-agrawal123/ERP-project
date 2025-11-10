package ui.dashboard.FacultyFrame;

/**
 * A simple model class to hold one component of a grading policy.
 */
public class GradingComponent {
    private String name;
    private int percentage;

    public GradingComponent(String name, int percentage) {
        this.name = name;
        this.percentage = percentage;
    }

    // --- Getters and Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    @Override
    public String toString() {
        return name + ": " + percentage + "%";
    }
}
package ui.FacultyFrame;

/**
 * Model class to hold information about a single grading component.
 */
public class GradingComponent {
    private String name;
    private int percentage;

    public GradingComponent(String name, int percentage) {
        this.name = name;
        this.percentage = percentage;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public int getPercentage() {
        return percentage;
    }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    // Used by JList if no renderer is set
    @Override
    public String toString() {
        return name + " (" + percentage + "%)";
    }
}
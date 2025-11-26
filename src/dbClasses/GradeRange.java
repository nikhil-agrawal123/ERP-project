package dbClasses;

public class GradeRange {
    private String gradeLetter; // e.g. "A", "B+"
    private int minScore;       // e.g. 90, 85

    public GradeRange(String gradeLetter, int minScore) {
        this.gradeLetter = gradeLetter;
        this.minScore = minScore;
    }

    public String getGradeLetter() { return gradeLetter; }

    public int getMinScore() { return minScore; }
}
package dbClasses;

public class StudentCgCredits {
    private int credits;
    private double cg;

    public StudentCgCredits(int credits, double cg) {
        this.credits = credits;
        this.cg = cg;
    }

    public int getCredits() {
        return credits;
    }
    public void setCredits(int credits) {
        this.credits = credits;
    }
    public double getCg() {
        return cg;
    }
    public void setCg(double cg) {
        this.cg = cg;
    }
}


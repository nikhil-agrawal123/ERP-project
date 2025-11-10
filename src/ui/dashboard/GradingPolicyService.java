package ui.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mock service to simulate saving and retrieving grading policies.
 * Uses a static map to persist data for the app's lifetime.
 */
public class GradingPolicyService {

    // Our mock in-memory database
    private static final Map<String, List<GradingComponent>> policyDatabase = new HashMap<>();

    /**
     * Gets the grading policy for a course.
     * If one isn't "saved" in our map, it creates a default one.
     */
    public static List<GradingComponent> getPolicy(String courseCode) {
        // Check if we have a saved policy
        if (policyDatabase.containsKey(courseCode)) {
            return new ArrayList<>(policyDatabase.get(courseCode)); // Return a copy
        }

        // If not, create a default hardcoded policy
        List<GradingComponent> defaultPolicy = new ArrayList<>();
        defaultPolicy.add(new GradingComponent("Mid-sem", 20));
        defaultPolicy.add(new GradingComponent("End-sem", 40));
        defaultPolicy.add(new GradingComponent("Quiz", 30));
        defaultPolicy.add(new GradingComponent("Assignment", 10));

        // "Save" this default policy for next time
        policyDatabase.put(courseCode, defaultPolicy);
        return new ArrayList<>(defaultPolicy); // Return a copy
    }

    /**
     * "Saves" the policy to our mock database.
     */
    public static void savePolicy(String courseCode, List<GradingComponent> policy) {
        // We save a copy of the list
        policyDatabase.put(courseCode, new ArrayList<>(policy));
    }
}
package middleware;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dbEndpoints.gradingPoints;
import ui.FacultyFrame.GradingComponent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle Grading Policy logic and JSON conversion.
 */
public class gradingService {

    private gradingPoints repository;
    private Gson gson;

    public gradingService() {
        this.repository = new gradingPoints();
        this.gson = new Gson();
    }

    public List<GradingComponent> getPolicy(String courseCode, String instructorId, String semester) {
        String json = repository.getPolicyJson(courseCode, instructorId, semester);

        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        Type listType = new TypeToken<ArrayList<GradingComponent>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public boolean savePolicy(String courseCode, String courseName, String instructorId, String semester, List<GradingComponent> components) {
        String json = gson.toJson(components);
        return repository.savePolicyJson(courseCode, courseName, instructorId, semester, json);
    }
}
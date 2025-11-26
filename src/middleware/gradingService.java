package middleware;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dbEndpoints.gradingPoints;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dbClasses.GradingComponent;
import dbClasses.GradeRange;

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

    public List<GradeRange> getGradeCutoffs(String courseCode, String instructorId, String semester) {
        String json = repository.getCutoffsJson(courseCode, instructorId, semester);

        if (json == null || json.isEmpty()) {
            return null; // Return null so UI knows to load defaults
        }

        Type listType = new TypeToken<ArrayList<GradeRange>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public boolean saveGradeCutoffs(String courseCode, String courseName, String instructorId, String semester, List<GradeRange> ranges) {
        String json = gson.toJson(ranges);
        return repository.saveCutoffsJson(courseCode, courseName, instructorId, semester, json);
    }
}
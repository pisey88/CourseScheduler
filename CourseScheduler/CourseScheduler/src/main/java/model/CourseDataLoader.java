package model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CourseDataLoader {
    public static List<Course> loadCourses(String filePath) {
        List<Course> courses = new ArrayList<>();

        try (InputStream is = CourseDataLoader.class.getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            br.readLine(); // Skip header

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // Keep empty values
                if (parts.length >= 11) {
                    courses.add(new Course(
                            Integer.parseInt(parts[0]),
                            parts[1], parts[2], parts[3], parts[4],
                            parts[5], parts[6], parts[7], parts[8],
                            parts[9], parts[10], parts.length > 11 ? parts[11] : ""
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            courses = getSampleCourses();
        }
        return courses;
    }

    private static List<Course> getSampleCourses() {
        List<Course> sampleCourses = new ArrayList<>();
        sampleCourses.add(new Course(1, "001", "CS101", "Intro to Programming", "Core",
                "Dr. Smith", "Mon/Wed 10:00-11:30", "2025-01-15", "2025-05-15",
                "30/30", "Closed", ""));
        sampleCourses.add(new Course(2, "001", "CS102", "Data Structures", "Core",
                "Dr. Johnson", "Tue/Thu 13:00-14:30", "2025-01-15", "2025-05-15",
                "25/30", "Open", "CS101"));
        sampleCourses.add(new Course(3, "001", "CS201", "Algorithms", "Core",
                "Dr. Williams", "Mon/Wed 13:00-14:30", "2025-01-15", "2025-05-15",
                "20/25", "Open", "CS102"));
        return sampleCourses;
    }
}
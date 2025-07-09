package model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PrerequisiteManager {
    private final Map<String, List<String>> adjList = new HashMap<>();
    private final Map<String, Integer> inDegree = new HashMap<>();
    private final Set<String> allCourses = new HashSet<>();

    public void loadPrerequisites(String filePath) {
        try (InputStream is = getClass().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            br.readLine(); // Skip header

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    addPrerequisite(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addPrerequisite("CS102", "CS101");
            addPrerequisite("CS201", "CS102");
        }
    }

    public void addPrerequisite(String course, String prerequisite) {
        adjList.putIfAbsent(prerequisite, new ArrayList<>());
        adjList.putIfAbsent(course, new ArrayList<>());

        adjList.get(prerequisite).add(course);
        inDegree.put(course, inDegree.getOrDefault(course, 0) + 1);
        inDegree.putIfAbsent(prerequisite, 0);

        allCourses.add(course);
        allCourses.add(prerequisite);
    }

    public List<String> getTopologicalOrder() throws IllegalStateException {
        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> tempInDegree = new HashMap<>(inDegree);

        for (Map.Entry<String, Integer> entry : tempInDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            for (String neighbor : adjList.getOrDefault(current, new ArrayList<>())) {
                tempInDegree.put(neighbor, tempInDegree.get(neighbor) - 1);
                if (tempInDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        if (result.size() != tempInDegree.size()) {
            throw new IllegalStateException("Circular dependency detected in prerequisites");
        }

        return result;
    }

    public boolean courseExists(String csn) {
        return allCourses.contains(csn);
    }

    public boolean isPrerequisiteSatisfied(String prereq, List<Course> selectedCourses) {
        return selectedCourses.stream()
                .anyMatch(course -> course.getCsn().equals(prereq));
    }

    public Set<String> getAllCourses() {
        return Collections.unmodifiableSet(allCourses);
    }
}
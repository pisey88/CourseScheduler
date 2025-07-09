package gui;

import model.Course;
import model.CourseDataLoader;
import model.PrerequisiteManager;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class CourseSchedulerGUI {
    private static PrerequisiteManager prerequisiteManager;
    private static TableRowSorter<CourseTableModel> sorter;
    private static JButton availableButton;
    private static JButton checkSelectionButton;
    private static JButton generateScheduleButton;
    private static JButton resetButton;
    private static JButton unselectAllButton;
    private static JTable table;

    public static void launch() {
        prerequisiteManager = new PrerequisiteManager();
        prerequisiteManager.loadPrerequisites("/prerequisites.csv");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AUPP COURSE SCHEDULER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 700);
            frame.setLayout(new BorderLayout());

            table = createCourseTable();
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            frame.add(createTopPanel(), BorderLayout.NORTH);
            frame.add(createBottomPanel(), BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }

    private static JTable createCourseTable() {
        String[] columns = {"Select", "ID", "Section", "CSN", "Course Name", "Category",
                "Faculty", "Schedule", "Start Date", "End Date", "Capacity",
                "Status", "Prerequisites"};

        List<Course> courses = CourseDataLoader.loadCourses("/courses.csv");
        CourseTableModel model = new CourseTableModel(courses, columns);

        JTable table = new JTable(model) {
            @Override
            public void changeSelection(int row, int col, boolean toggle, boolean extend) {
                super.changeSelection(row, col, toggle, extend);
                if (col == 0) {
                    boolean value = (Boolean) getModel().getValueAt(row, col);
                    getModel().setValueAt(!value, row, col);
                }
            }
        };

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);
        table.getColumnModel().getColumn(9).setPreferredWidth(80);
        table.getColumnModel().getColumn(10).setPreferredWidth(80);
        table.getColumnModel().getColumn(11).setPreferredWidth(80);
        table.getColumnModel().getColumn(12).setPreferredWidth(150);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        return table;
    }

    private static JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("AUPP COURSE SCHEDULER", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(20);
        searchField.addActionListener(e -> {
            String text = searchField.getText();
            if (text.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(CourseFilter.createSearchFilter(text));
            }
        });

        JButton clearSearchButton = new JButton("Clear");
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        searchPanel.add(searchField);
        searchPanel.add(clearSearchButton);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private static JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        unselectAllButton = new JButton("Unselect All");
        unselectAllButton.setPreferredSize(new Dimension(120, 30));
        unselectAllButton.addActionListener(e -> unselectAllCourses());
        leftButtonPanel.add(unselectAllButton);

        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        availableButton = new JButton("Available");
        checkSelectionButton = new JButton("Check Selection");
        generateScheduleButton = new JButton("Generate Schedule");
        resetButton = new JButton("Reset Filters");

        Dimension buttonSize = new Dimension(150, 30);
        availableButton.setPreferredSize(buttonSize);
        checkSelectionButton.setPreferredSize(buttonSize);
        generateScheduleButton.setPreferredSize(buttonSize);
        resetButton.setPreferredSize(buttonSize);

        availableButton.addActionListener(e -> {
            sorter.setRowFilter(CourseFilter.createStatusFilter("Open"));
            availableButton.setEnabled(false);
            resetButton.setEnabled(true);
        });

        checkSelectionButton.addActionListener(e -> validateSelection());
        generateScheduleButton.addActionListener(e -> generateTopologicalSchedule());
        resetButton.addActionListener(e -> {
            sorter.setRowFilter(null);
            availableButton.setEnabled(true);
            resetButton.setEnabled(false);
        });

        resetButton.setEnabled(false);

        rightButtonPanel.add(availableButton);
        rightButtonPanel.add(checkSelectionButton);
        rightButtonPanel.add(generateScheduleButton);
        rightButtonPanel.add(resetButton);

        panel.add(leftButtonPanel, BorderLayout.WEST);
        panel.add(rightButtonPanel, BorderLayout.EAST);

        return panel;
    }

    private static void unselectAllCourses() {
        CourseTableModel model = (CourseTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(false, i, 0);
        }
    }

    private static void validateSelection() {
        CourseTableModel model = (CourseTableModel) table.getModel();
        List<Course> selectedCourses = model.getSelectedCourses();

        if (selectedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Please select courses by checking the boxes in the first column",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<String> validOrder = prerequisiteManager.getTopologicalOrder();
            StringBuilder missingPrereqs = new StringBuilder();

            for (Course course : selectedCourses) {
                if (!validOrder.contains(course.getCsn())) {
                    missingPrereqs.append("- ").append(course.getCsn())
                            .append(" requires unselected prerequisites\n");
                }
            }

            if (missingPrereqs.length() > 0) {
                throw new IllegalStateException(missingPrereqs.toString());
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(null,
                    "Prerequisite violations:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder timeConflicts = new StringBuilder();
        for (int i = 0; i < selectedCourses.size(); i++) {
            for (int j = i + 1; j < selectedCourses.size(); j++) {
                Course a = selectedCourses.get(i);
                Course b = selectedCourses.get(j);
                if (hasTimeConflict(a, b)) {
                    timeConflicts.append("- ").append(a.getCsn())
                            .append(" (").append(a.getSchedule()).append(")")
                            .append(" conflicts with ")
                            .append(b.getCsn()).append(" (").append(b.getSchedule()).append(")\n");
                }
            }
        }

        if (timeConflicts.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "Schedule conflicts:\n" + timeConflicts,
                    "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Valid selection! All prerequisites are met and no schedule conflicts.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static boolean hasTimeConflict(Course a, Course b) {
        return a.getSchedule().equals(b.getSchedule());
    }

    private static void generateTopologicalSchedule() {
        CourseTableModel model = (CourseTableModel) table.getModel();
        List<Course> selectedCourses = model.getSelectedCourses();

        if (selectedCourses.isEmpty()) {
            try {
                List<String> fullSchedule = prerequisiteManager.getTopologicalOrder();
                List<Course> allCourses = new ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    allCourses.add(model.getCourseAt(i));
                }

                StringBuilder sb = new StringBuilder("Complete Recommended Course Order:\n\n");
                for (int i = 0; i < fullSchedule.size(); i++) {
                    String courseCode = fullSchedule.get(i);
                    Course course = allCourses.stream()
                            .filter(c -> c.getCsn().equals(courseCode))
                            .findFirst()
                            .orElse(null);

                    if (course != null) {
                        sb.append(i + 1).append(". ").append(course.getCsn())
                                .append(" - ").append(course.getName())
                                .append("\n   Schedule: ").append(course.getSchedule())
                                .append("\n   Prerequisites: ").append(course.getPrerequisites().isEmpty() ? "None" : course.getPrerequisites())
                                .append("\n\n");
                    }
                }

                JTextArea textArea = new JTextArea(sb.toString(), 20, 60);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(null, scrollPane,
                        "Complete Recommended Course Order", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(null,
                        "Error: Cannot generate schedule - circular dependencies detected:\n" +
                                e.getMessage() + "\n\nPlease consult your advisor.",
                        "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        try {
            PrerequisiteManager tempManager = new PrerequisiteManager();

            for (Course course : selectedCourses) {
                String prereqs = course.getPrerequisites();
                if (prereqs != null && !prereqs.isEmpty()) {
                    for (String prereq : prereqs.split(",")) {
                        if (selectedCourses.stream().anyMatch(c -> c.getCsn().equals(prereq.trim()))) {
                            tempManager.addPrerequisite(course.getCsn(), prereq.trim());
                        }
                    }
                }
            }

            List<String> schedule = tempManager.getTopologicalOrder();

            List<String> filteredSchedule = new ArrayList<>();
            for (String courseCode : schedule) {
                if (selectedCourses.stream().anyMatch(c -> c.getCsn().equals(courseCode))) {
                    filteredSchedule.add(courseCode);
                }
            }

            for (Course course : selectedCourses) {
                if (!filteredSchedule.contains(course.getCsn())) {
                    filteredSchedule.add(course.getCsn());
                }
            }

            StringBuilder sb = new StringBuilder("Recommended Course Order for Your Selection:\n\n");
            for (int i = 0; i < filteredSchedule.size(); i++) {
                String courseCode = filteredSchedule.get(i);
                Course course = selectedCourses.stream()
                        .filter(c -> c.getCsn().equals(courseCode))
                        .findFirst()
                        .orElse(null);

                if (course != null) {
                    sb.append(i + 1).append(". ").append(course.getCsn())
                            .append(" - ").append(course.getName())
                            .append("\n   Schedule: ").append(course.getSchedule())
                            .append("\n   Prerequisites: ").append(course.getPrerequisites().isEmpty() ? "None" : course.getPrerequisites())
                            .append("\n\n");
                }
            }

            JTextArea textArea = new JTextArea(sb.toString(), 20, 60);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(null, scrollPane,
                    "Recommended Schedule for Selected Courses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(null,
                    "Error: Cannot generate schedule - circular dependencies detected in:\n" +
                            e.getMessage() + "\n\nPlease adjust your course selection.",
                    "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
        }
    }
}
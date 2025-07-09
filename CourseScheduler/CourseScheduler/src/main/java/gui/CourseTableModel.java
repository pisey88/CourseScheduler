package gui;

import model.Course;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CourseTableModel extends AbstractTableModel {
    private final List<Course> courses;
    private final String[] columns;
    private final List<Boolean> selectedRows;

    public CourseTableModel(List<Course> courses, String[] columns) {
        this.courses = courses;
        this.columns = columns;
        this.selectedRows = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            selectedRows.add(false);
        }
    }

    @Override public int getRowCount() { return courses.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

    @Override
    public Class<?> getColumnClass(int col) {
        return col == 0 ? Boolean.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) return selectedRows.get(row);

        Course course = courses.get(row);
        switch (col) {
            case 1: return course.getId();
            case 2: return course.getSectionNo();
            case 3: return course.getCsn();
            case 4: return course.getName();
            case 5: return course.getCategory();
            case 6: return course.getFaculty();
            case 7: return course.getSchedule();
            case 8: return course.getStartDate();
            case 9: return course.getEndDate();
            case 10: return course.getCapacity();
            case 11: return course.getStatus();
            case 12: return course.getPrerequisites();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            selectedRows.set(row, (Boolean) value);
            fireTableCellUpdated(row, col);
        }
    }

    public Course getCourseAt(int row) {
        return courses.get(row);
    }

    public List<Course> getSelectedCourses() {
        List<Course> selected = new ArrayList<>();
        for (int i = 0; i < selectedRows.size(); i++) {
            if (selectedRows.get(i)) {
                selected.add(courses.get(i));
            }
        }
        return selected;
    }
}
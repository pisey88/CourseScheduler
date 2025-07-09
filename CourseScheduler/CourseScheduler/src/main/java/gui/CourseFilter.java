package gui;
import javax.swing.RowFilter;

public class CourseFilter {
    public static RowFilter<Object, Object> createStatusFilter(String status) {
        return RowFilter.regexFilter(status, 11); // Status is column 11
    }

    public static RowFilter<Object, Object> createSearchFilter(String searchText) {
        return RowFilter.regexFilter("(?i)" + searchText);
    }
}
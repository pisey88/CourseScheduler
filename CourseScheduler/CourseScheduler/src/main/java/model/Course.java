package model;

public class Course {
    private final int id;
    private final String sectionNo;
    private final String csn;
    private final String name;
    private final String category;
    private final String faculty;
    private final String schedule;
    private final String startDate;
    private final String endDate;
    private final String capacity;
    private final String status;
    private final String prerequisites;

    public Course(int id, String sectionNo, String csn, String name, String category,
                  String faculty, String schedule, String startDate, String endDate,
                  String capacity, String status, String prerequisites) {
        this.id = id;
        this.sectionNo = sectionNo;
        this.csn = csn;
        this.name = name;
        this.category = category;
        this.faculty = faculty;
        this.schedule = schedule;
        this.startDate = startDate;
        this.endDate = endDate;
        this.capacity = capacity;
        this.status = status;
        this.prerequisites = prerequisites != null ? prerequisites : "";
    }

    // Getters
    public int getId() { return id; }
    public String getSectionNo() { return sectionNo; }
    public String getCsn() { return csn; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getFaculty() { return faculty; }
    public String getSchedule() { return schedule; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getCapacity() { return capacity; }
    public String getStatus() { return status; }
    public String getPrerequisites() { return prerequisites; }
}
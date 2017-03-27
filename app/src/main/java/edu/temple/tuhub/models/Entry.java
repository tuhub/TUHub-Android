package edu.temple.tuhub.models;

public  class Entry {
    public final String course;
    public final String crseId;
    public final String description;
    public final String creditHr;
    public final String college;
    public final String division;
    public final String department;
    public final String schedule;

    public Entry(String course, String crseId, String description, String creditHr, String college, String division, String department, String schedule) {
        this.course = course;
        this.crseId = crseId;
        this.description = description;
        this.creditHr = creditHr;
        this.college = college;
        this.division = division;
        this.department = department;
        this.schedule = schedule;
    }

    public String getEntries() {
        return course+" "+crseId + " " + description+ " "+creditHr+" "+college+" "+division+" "+department+" "+schedule;
    }

    public String getCourse() {
        return course;
    }

    public String getCrseId() {
        return crseId;
    }

    public String getDescription() {
        return description;
    }

    public String getCreditHr() {
        return creditHr;
    }

    public String getCollege() {
        return college;
    }

    public String getDivision() {
        return division;
    }

    public String getDepartment() {
        return department;
    }

    public String getSchedule() {
        return schedule;
    }
}
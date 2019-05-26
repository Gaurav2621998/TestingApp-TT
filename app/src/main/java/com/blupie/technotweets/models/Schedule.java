package com.blupie.technotweets.models;

public class Schedule {

    public Schedule() {
    }

    public Schedule(String time, String faculty, String subject) {
        this.time = time;
        this.faculty = faculty;
        this.subject = subject;
    }

    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    String faculty;
    String subject;
}

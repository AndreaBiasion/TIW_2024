package com.example.tiw_2024.beans;

import java.util.Date;

public class Group {

    private int id;

    private int activity_duration;
    private int min_parts;
    private int max_parts;

    private String title;
    private Date date_creation;

    // temporary
    public Group(int id, int activity_duration, int min_parts, int max_parts, String title, Date date_creation) {
        this.id = id;
        this.activity_duration = activity_duration;
        this.min_parts = min_parts;
        this.max_parts = max_parts;
        this.title = title;
        this.date_creation = date_creation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActivity_duration() {
        return activity_duration;
    }

    public void setActivity_duration(int activity_duration) {
        this.activity_duration = activity_duration;
    }

    public int getMin_parts() {
        return min_parts;
    }

    public void setMin_parts(int min_parts) {
        this.min_parts = min_parts;
    }

    public int getMax_parts() {
        return max_parts;
    }

    public void setMax_parts(int max_parts) {
        this.max_parts = max_parts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(Date date_creation) {
        this.date_creation = date_creation;
    }
}

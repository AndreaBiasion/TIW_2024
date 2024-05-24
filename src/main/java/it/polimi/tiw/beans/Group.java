package it.polimi.tiw.beans;

import java.sql.Date;

public class Group {

    private int id;
    private int activity_duration;
    private int min_parts;
    private int max_parts;
    private String title;
    private Date date_creation;
    /**
     *
     * @return the id of the group
     */
    public int getId() {
        return id;
    }
    /**
     * Set the group id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     *
     * @return the duration of the group activity
     */
    public int getActivity_duration() {
        return activity_duration;
    }
    /**
     * Set the duration of the group activity
     * @param activity_duration
     */
    public void setActivity_duration(int activity_duration) {
        this.activity_duration = activity_duration;
    }
    /**
     *
     * @return the minimum number of participants of the group
     */
    public int getMin_parts() {
        return min_parts;
    }
    /**
     * Set the minimum number of participants of the group
     * @param min_parts
     */
    public void setMin_parts(int min_parts) {
        this.min_parts = min_parts;
    }
    /**
     *
     * @return the maximum number of participants of the group
     */
    public int getMax_parts() {
        return max_parts;
    }
    /**
     * Set the maximum number of participants of the group
     * @param max_parts
     */
    public void setMax_parts(int max_parts) {
        this.max_parts = max_parts;
    }
    /**
     *
     * @return the tile of the group
     */
    public String getTitle() {
        return title;
    }
    /**
     * Set the title of the group
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     *
     * @return the date of creation of the group
     */
    public Date getDate_creation() {
        return date_creation;
    }
    /**
     * Set the date of creation of the group
     * @param date_creation
     */
    public void setDate_creation(Date date_creation) {
        this.date_creation = date_creation;
    }
}

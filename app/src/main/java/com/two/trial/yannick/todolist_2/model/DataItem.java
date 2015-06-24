package com.two.trial.yannick.todolist_2.model;

import java.io.Serializable;

/*
 *  this must be made serialisable to be passed together with an intent 
 */
public class DataItem implements Serializable {

    public DataItem() {}

    /*
	 * holds all information stored for an item
	 */
    private long id;
	private String name;
    private String description;
	private long expiry;
    private boolean done;
    private boolean favourite;

    public DataItem(String name, long expiry, String description) {
		this.name = name;
        this.description = description;
		this.expiry = expiry;
        this.done = done;
//        this.favourite = favourite;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getExpiry() {
        return expiry;
	}

	public void setExpiry(long expiry) {
        this.expiry = expiry;
	}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    /* DO NOT DO THIS !!! DataItem should not know how it is displayed - recording 13.May2015, 20min */
//  public String toString() {
//        return "item " + this.name + " " + this.expiry + " " + this.description;
//        return "item " + this.name + " " + this.expiry + " " + this.favourite + " " + this.done + " " + this.description;
//    }
    /**/
}

package com.two.trial.yannick.todolist_2.model;

import java.io.Serializable;

/*
 *  this must be made serialisable to be passed together with an intent 
 */
public class ToDoData implements Serializable {

    public ToDoData() {}

    /*
	 * holds all information stored for an item
	 */
    private long id;
	private String name;
    private String description;
	private long epired;
    private boolean done;
    private boolean favourite;

    public ToDoData(String name, long epired, String description) {
		this.name = name;
        this.description = description;
		this.epired = epired;
//        this.done = done;
//        this.favourite = favourite;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getEpired() {
        return epired;
	}

	public void setEpired(long epired) {
        this.epired = epired;
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
//        return "item " + this.name + " " + this.epired + " " + this.description;
//        return "item " + this.name + " " + this.epired + " " + this.favourite + " " + this.done + " " + this.description;
//    }
    /**/
}

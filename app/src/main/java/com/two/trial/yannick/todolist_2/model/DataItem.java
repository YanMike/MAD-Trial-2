package com.two.trial.yannick.todolist_2.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;

/*
 *  this must be made serialisable to be passed together with an intent 
 */
public class DataItem implements Serializable {
    /*
	 * holds all information stored for an item
	 */
    private long id;
	private String name;
    private String description;
	private long expiry;
    private boolean done;
    private boolean favourite;

    public DataItem() {}

    public DataItem(String name, long expiry, String description, boolean done, boolean favourite) {
		this.name = name;
        this.description = description;
		this.expiry = expiry;
        this.done = done;
        this.favourite = favourite;
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
}

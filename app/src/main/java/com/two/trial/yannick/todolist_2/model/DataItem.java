package com.two.trial.yannick.todolist_2.model;

import java.io.Serializable;

/*
 *  this must be made serialisable to be passed together with an intent 
 */
public class DataItem implements Serializable {
	
	/*
	 * this holds the name of the item
	 */
	private String name;

	/*
	 * here we will store how long it had taken for the activity to start after being called
	 */
	private long latency;

	public DataItem(String name,long latency) {
		this.name = name;
		this.latency = latency;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLatency() {
		return latency;
	}

	public void setLatency(long latency) {
		this.latency = latency;
	}

}

package com.mycompany.portfoliosimulator.utils;

import java.util.Date;

public class Interval {

	private Date start;
	private Date end;

	public Interval(Date start, Date end){
		this.start = start;
		this.end = end;
	}

	public void setStart(Date start){
		this.start = start;
	}

	public void setStart(long start){
		this.start.setTime(start);
	}

	public Date getStart(){
		return start;
	}

	public void setEnd(Date end){
		this.end = end;
	}

	public void setEnd(long end){
		this.end.setTime(end);
	}

	public Date getEnd(){
		return end;
	}

	public Boolean contains(Date date, Boolean inclusive) {
		return (start.before(date) || start.getTime() == date.getTime()) && (end.after(date) || end.getTime() == date.getTime());
	}

	public Boolean after(Date date) {
		return end.before(date);
	}

	public Boolean before(Date date) {
		return start.after(date);
	}


}

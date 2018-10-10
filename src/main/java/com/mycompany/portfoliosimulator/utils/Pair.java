package com.mycompany.portfoliosimulator.utils;

public class Pair implements Comparable<Pair> {

	public String val0;
	public Float val1;

	public Pair(String val0, Float val1) {
		this.val0 = val0;
		this.val1 = val1;
	}

	@Override
	public String toString() {
		return "("+val0+", "+val1+")";
	}

	@Override
	public int compareTo(Pair p) {
		if(this.val1 == null) return 1;
		if(p.val1 == null) return -1;
		return (this.val1<p.val1 ? 1 : (this.val1.equals(p.val1) ? 0 : -1));
	}
}
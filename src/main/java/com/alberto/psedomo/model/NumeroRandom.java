package com.alberto.psedomo.model;

public class NumeroRandom {
	
	private String id;
	private long TimeToProcess;
	private long TimeArrived;
	private Integer number;
	private long TotalWaitTime;
	
	
	
	public long getTotalWaitTime() {
		return TotalWaitTime;
	}
	
	public void setTotalWaitTime(long totalWaitTime) {
		TotalWaitTime = totalWaitTime;
	}
	
	public void setTimeToProcess(long timeToProcess) {
		TimeToProcess = timeToProcess;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public long getTimeToProcess() {
		return TimeToProcess;
	}
	
	public void SetTimeToProcess(long TimeToProcess) {
		this.TimeToProcess = TimeToProcess;
	}
	
	public long getTimeArrived() {
		return TimeArrived;
	}
	public void setTimeArrived(long timeArrived) {
		TimeArrived = timeArrived;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	
}

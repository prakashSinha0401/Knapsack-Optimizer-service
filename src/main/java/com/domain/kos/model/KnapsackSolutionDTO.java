package com.domain.kos.model;

import java.util.Map;

public class KnapsackSolutionDTO {

	private String task;
	private String status;
	private Map<String, Long> timestamps;
	private KnapsackProblemSpecification problem;
	private Map<String, Object> solution;
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Map<String, Long> getTimestamps() {
		return timestamps;
	}
	public void setTimestamps(Map<String, Long> timestamps) {
		this.timestamps = timestamps;
	}
	public KnapsackProblemSpecification getProblem() {
		return problem;
	}
	public void setProblem(KnapsackProblemSpecification problem) {
		this.problem = problem;
	}
	public Map<String, Object> getSolution() {
		return solution;
	}
	public void setSolution(Map<String, Object> solution) {
		this.solution = solution;
	}
	
	
}

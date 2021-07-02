package com.domain.kos.entity;

import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KOS")
public class Kos {
  
	@Id
	@Column(name = "KOS_TASK_ID", length = 20)
	private String task;
	
	@Column(name = "KOS_STATUS", length = 20)
	private String status;
	
	@Column(name = "KOS_SOLUTION")
	private Clob solution;

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

	public Clob getSolution() {
		return solution;
	}

	public void setSolution(Clob solution) {
		this.solution = solution;
	}
	
	
}

package fr.vergne.taskmanager;

import java.util.Date;

public class Task {
	private String title;
	private String description;
	private Date creationDate;
	private Date deadline;
	private TaskStatus status;

	public static enum TaskStatus {
		SLEEPING, RUNNING, FINISHED
	}

	public Task(String title, String description, boolean started, Date deadline) {
		setTitle(title);
		setDescription(description);
		setDeadline(deadline);
		setStatus(TaskStatus.SLEEPING);
		setCreationDate(new Date());
	}

	public Task(String title, boolean start, Date deadline) {
		this(title, null, start, deadline);
	}

	public Task(String title, String description, Date deadline) {
		this(title, description, false, deadline);
	}

	public Task(String title, Date deadline) {
		this(title, false, deadline);
	}

	public Task(String title, String description) {
		this(title, description, null);
	}

	public Task(String title) {
		this(title, (Date) null);
	}

	public Task() {
		this("New task");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String decription) {
		this.description = decription;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public boolean isStarted() {
		return status != TaskStatus.SLEEPING;
	}

	public boolean isRunning() {
		return status == TaskStatus.RUNNING;
	}

	public boolean isDone() {
		return status == TaskStatus.FINISHED;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public boolean hasDeadline() {
		return deadline != null;
	}

}

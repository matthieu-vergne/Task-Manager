package fr.vergne.taskmanager;

import java.util.Date;

import fr.vergne.taskmanager.history.AbstractHistorizable;

public class Task {
	private AbstractHistorizable<String> title;
	private AbstractHistorizable<String> description;
	private Date creationDate;
	private AbstractHistorizable<Date> deadline;
	private AbstractHistorizable<TaskStatus> status;

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
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String decription) {
		this.description.set(decription);
	}

	public Date getDeadline() {
		return deadline.get();
	}

	public void setDeadline(Date deadline) {
		this.deadline.set(deadline);
	}

	public boolean isStarted() {
		return getStatus() != TaskStatus.SLEEPING;
	}

	public boolean isRunning() {
		return getStatus() == TaskStatus.RUNNING;
	}

	public boolean isDone() {
		return getStatus() == TaskStatus.FINISHED;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public TaskStatus getStatus() {
		return status.get();
	}

	public void setStatus(TaskStatus status) {
		this.status.set(status);
	}

	public boolean hasDeadline() {
		return deadline != null;
	}

}

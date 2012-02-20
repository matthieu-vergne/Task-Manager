package fr.vergne.taskmanager.task;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import fr.vergne.taskmanager.history.DefaultHistorizable;

public class Task {
	private final DefaultHistorizable<String> title = new DefaultHistorizable<String>();
	private final DefaultHistorizable<String> description = new DefaultHistorizable<String>();
	private Date creationDate;
	private final DefaultHistorizable<Date> deadline = new DefaultHistorizable<Date>(
			null);
	private final DefaultHistorizable<TaskStatus> status = new DefaultHistorizable<TaskStatus>(
			TaskStatus.SLEEPING);

	public Task(String title, String description) {
		setTitle(title);
		setDescription(description);
	}

	public Task(String title) {
		this(title, null);
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
		setCreationDate(creationDate, false);
	}

	public void setCreationDate(Date creationDate, boolean force) {
		Date min = new Date(Long.MAX_VALUE);
		if (!force) {
			Collection<Date> firsts = new LinkedList<Date>();
			firsts.add(title.getHistory().iterator().next().getDate());
			firsts.add(description.getHistory().iterator().next().getDate());
			firsts.add(deadline.getHistory().iterator().next().getDate());
			firsts.add(status.getHistory().iterator().next().getDate());

			for (Date date : firsts) {
				if (min.after(date)) {
					min = date;
				}
			}
		}
		if (creationDate.after(min) && !force) {
			throw new IllegalArgumentException(
					"A part of the history is before " + creationDate
							+ ", you cannot set the creation date after " + min
							+ ".");
		} else {
			this.creationDate = creationDate;
		}
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

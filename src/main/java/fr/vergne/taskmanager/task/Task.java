package fr.vergne.taskmanager.task;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import fr.vergne.taskmanager.export.Exportable;
import fr.vergne.taskmanager.gui.gantt.UpdateListener;
import fr.vergne.taskmanager.history.AbstractHistorizable;
import fr.vergne.taskmanager.history.Historizable;
import fr.vergne.taskmanager.history.HistorizableDate;
import fr.vergne.taskmanager.history.HistorizableEnum;
import fr.vergne.taskmanager.history.HistorizableString;
import fr.vergne.taskmanager.history.History;

public class Task implements Exportable {
	private final HistorizableString title = new HistorizableString("New Task",
			true);
	private final HistorizableString description = new HistorizableString();
	private Date creationDate;
	private final HistorizableDate start = new HistorizableDate();
	private final HistorizableDate deadline = new HistorizableDate();
	private final AbstractHistorizable<TaskStatus> status = new HistorizableEnum<TaskStatus>() {
		@Override
		public Class<TaskStatus> getEnumClass() {
			return TaskStatus.class;
		}
	};

	public Task(String title, String description) {
		this();
		setTitle(title);
		setDescription(description);
	}

	public Task(String title) {
		this(title, null);
	}

	public Task() {
		setCreationDate(new Date(), true);
		setStart(null);
		setDeadline(null);
		setStatus(TaskStatus.SLEEPING);
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
		fireUpdateEvent();
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String decription) {
		this.description.set(decription);
		fireUpdateEvent();
	}

	public Date getStart() {
		return start.get();
	}

	public void setStart(Date start) {
		this.start.set(start);
		fireUpdateEvent();
	}

	public Date getDeadline() {
		return deadline.get();
	}

	public void setDeadline(Date deadline) {
		this.deadline.set(deadline);
		fireUpdateEvent();
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
		Date max = lookForMaximalCreationDate(!force);
		if (creationDate.after(max) && !force) {
			throw new IllegalArgumentException(
					"A part of the history is before " + creationDate.getTime()
							+ ", you cannot set the creation date after "
							+ max.getTime());
		} else {
			this.creationDate = creationDate;

			title.getHistory().clearBefore(creationDate);
			description.getHistory().clearBefore(creationDate);
			deadline.getHistory().clearBefore(creationDate);
			status.getHistory().clearBefore(creationDate);
		}
		fireUpdateEvent();
	}

	private Date lookForMaximalCreationDate(boolean considerHistory) {
		Date min = new Date(Long.MAX_VALUE);
		if (considerHistory) {
			Collection<Date> firsts = new LinkedList<Date>();
			for (Historizable<?> element : new Historizable[] { title,
					description, deadline, status }) {
				History<?> history = element.getHistory();
				if (!history.isEmpty()) {
					firsts.add(history.datesIterator().next());
				}
			}
			for (Date date : firsts) {
				if (date.before(min)) {
					min = date;
				}
			}
		}
		return min;
	}

	public TaskStatus getStatus() {
		return status.get();
	}

	public void setStatus(TaskStatus status) {
		this.status.set(status);
		fireUpdateEvent();
	}

	public boolean hasStart() {
		return getStart() != null;
	}

	public boolean hasDeadline() {
		return getDeadline() != null;
	}

	@Override
	public void write(TransformerHandler handler) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute("", "", "creation", "CDATA", ""
				+ getCreationDate().getTime());
		handler.startElement("", "", "task", attributes);

		handler.startElement("", "", "title", null);
		title.getHistory().write(handler);
		handler.endElement("", "", "title");

		handler.startElement("", "", "description", null);
		description.getHistory().write(handler);
		handler.endElement("", "", "description");

		handler.startElement("", "", "start", null);
		start.getHistory().write(handler);
		handler.endElement("", "", "start");

		handler.startElement("", "", "deadline", null);
		deadline.getHistory().write(handler);
		handler.endElement("", "", "deadline");

		handler.startElement("", "", "status", null);
		status.getHistory().write(handler);
		handler.endElement("", "", "status");

		handler.endElement("", "", "task");
	}

	@Override
	public void read(Node node) throws SAXException {
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String tag = child.getNodeName();
			Node historyNode = child.getFirstChild();
			if (tag.equals("title")) {
				title.getHistory().read(historyNode);
			} else if (tag.equals("description")) {
				description.getHistory().read(historyNode);
			} else if (tag.equals("start")) {
				start.getHistory().read(historyNode);
			} else if (tag.equals("deadline")) {
				deadline.getHistory().read(historyNode);
			} else if (tag.equals("status")) {
				status.getHistory().read(historyNode);
			} else {
				throw new IllegalStateException(tag + " is not a managed tag");
			}
		}

		String time = node.getAttributes().getNamedItem("creation")
				.getNodeValue();
		Date date = new Date(Long.parseLong(time));
		setCreationDate(date);
	}

	private final Collection<UpdateListener> updateListeners = new LinkedList<UpdateListener>();

	public void addUpdateListener(UpdateListener listener) {
		synchronized (updateListeners) {
			updateListeners.add(listener);
		}
	}

	public void removeUpdateListener(UpdateListener listener) {
		synchronized (updateListeners) {
			updateListeners.remove(listener);
		}
	}

	protected void fireUpdateEvent() {
		synchronized (updateListeners) {
			for (UpdateListener listener : updateListeners) {
				listener.update();
			}
		}
	}
}

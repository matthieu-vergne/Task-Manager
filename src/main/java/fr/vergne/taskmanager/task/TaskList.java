package fr.vergne.taskmanager.task;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import fr.vergne.taskmanager.export.Exportable;
import fr.vergne.taskmanager.gui.gantt.UpdateListener;

// TODO manage deleted tasks (including in XML)
// TODO add different methods to get all or a given kind of tasks (deleted, finished, running, ...)
public class TaskList implements Exportable {

	private final LinkedList<Task> tasks = new LinkedList<Task>();
	private final Map<Task, Date> deletedTasks = new HashMap<Task, Date>();

	public Collection<Task> getAllTasks(boolean getDeleted) {
		LinkedList<Task> list = new LinkedList<Task>(tasks);
		if (!getDeleted) {
			list.removeAll(getDeletedTasks());
		}
		return list;
	}

	public Collection<Task> getDeletedTasks() {
		return new LinkedList<Task>(deletedTasks.keySet());
	}

	public List<Task> getDoneTasks(boolean getDeleted) {
		List<Task> list = new LinkedList<Task>();
		for (Task task : getAllTasks(getDeleted)) {
			if (task.isDone()) {
				list.add(task);
			}
		}
		return list;
	}

	public List<Task> getNotDoneTasks(boolean getDeleted) {
		List<Task> list = new LinkedList<Task>(getAllTasks(getDeleted));
		list.removeAll(getDoneTasks(getDeleted));
		return list;
	}

	public List<Task> getStartedTasks(boolean getDeleted) {
		List<Task> list = new LinkedList<Task>();
		for (Task task : getAllTasks(getDeleted)) {
			if (task.isStarted()) {
				list.add(task);
			}
		}
		return list;
	}

	public List<Task> getNotStartedTasks(boolean getDeleted) {
		List<Task> list = new LinkedList<Task>(getAllTasks(getDeleted));
		list.removeAll(getStartedTasks(getDeleted));
		return list;
	}

	public List<Task> getRunningTasks(boolean getDeleted) {
		List<Task> list = new LinkedList<Task>();
		for (Task task : getAllTasks(getDeleted)) {
			if (task.isRunning()) {
				list.add(task);
			}
		}
		return list;
	}

	public List<Task> getNotRunningTasks(boolean getDeleted) {
		List<Task> list = new LinkedList<Task>(getAllTasks(getDeleted));
		list.removeAll(getRunningTasks(getDeleted));
		return list;
	}

	public static Comparator<Task> getStartDateComparator() {
		return new Comparator<Task>() {

			@Override
			public int compare(Task t1, Task t2) {
				return t1.getStart().compareTo(t2.getStart());
			}
		};
	}

	public static Comparator<Task> getDeadlineComparator() {
		return new Comparator<Task>() {

			@Override
			public int compare(Task t1, Task t2) {
				return t1.getDeadline().compareTo(t2.getDeadline());
			}
		};
	}

	public static Comparator<Task> getTitleComparator() {
		return new Comparator<Task>() {

			@Override
			public int compare(Task t1, Task t2) {
				return t1.getTitle().compareTo(t2.getTitle());
			}
		};
	}

	public boolean isDeleted(Task task) {
		return deletedTasks.containsKey(task);
	}

	public Date getDeletiontDate(Task task) {
		return deletedTasks.get(task);
	}

	@Override
	public void write(TransformerHandler handler) throws SAXException {
		handler.startElement("", "", "tasks", null);
		for (Task task : tasks) {
			if (isDeleted(task)) {
				AttributesImpl attributes = new AttributesImpl();
				attributes.addAttribute("", "", "date", "CDATA", ""
						+ getDeletiontDate(task).getTime());
				handler.startElement("", "", "deleted", attributes);
				task.write(handler);
				handler.endElement("", "", "deleted");
			} else {
				task.write(handler);
			}
		}
		handler.endElement("", "", "tasks");
	}

	private void clear() {
		tasks.clear();
		deletedTasks.clear();
	}

	@Override
	public void read(Node node) throws SAXException {
		clear();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals("deleted")) {
				Task task = new Task();
				task.read(child.getFirstChild());
				add(task);
				
				String time = child.getAttributes().getNamedItem("date")
						.getNodeValue();
				Date date = new Date(Long.parseLong(time));
				deletedTasks.put(task, date);
			} else {
				Task task = new Task();
				task.read(child);
				add(task);
			}
		}
		fireUpdateEvent();
	}

	private final UpdateListener taskListener = new UpdateListener() {

		@Override
		public void update() {
			fireUpdateEvent();
		}
	};

	public void add(Task task) {
		tasks.add(task);
		task.addUpdateListener(taskListener);
		fireUpdateEvent();
	}

	public void delete(Task task) {
		if (tasks.contains(task)) {
			deletedTasks.put(task, new Date());
			fireUpdateEvent();
		} else {
			throw new IllegalArgumentException(task + " is not a known task.");
		}
	}

	public Task get(int index) {
		return tasks.get(index);
	}

	public int size() {
		return tasks.size();
	}

	private final Collection<UpdateListener> updateListeners = new LinkedList<UpdateListener>();

	public void addUpdateListener(UpdateListener listener) {
		updateListeners.add(listener);
	}

	public void removeUpdateListener(UpdateListener listener) {
		updateListeners.remove(listener);
	}

	protected void fireUpdateEvent() {
		for (UpdateListener listener : updateListeners) {
			listener.update();
		}
	}
}

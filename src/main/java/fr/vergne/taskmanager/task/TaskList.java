package fr.vergne.taskmanager.task;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.vergne.taskmanager.export.Exportable;
import fr.vergne.taskmanager.gui.gantt.UpdateListener;

public class TaskList implements Exportable, Iterable<Task> {

	private final LinkedList<Task> tasks = new LinkedList<Task>();

	public List<Task> getDoneTasks() {
		List<Task> list = new LinkedList<Task>();
		for (Task task : this) {
			if (task.isDone()) {
				list.add(task);
			}
		}
		return list;
	}

	public List<Task> getNotDoneTasks() {
		List<Task> list = new LinkedList<Task>(tasks);
		list.removeAll(getDoneTasks());
		return list;
	}

	public List<Task> getStartedTasks() {
		List<Task> list = new LinkedList<Task>();
		for (Task task : this) {
			if (task.isStarted()) {
				list.add(task);
			}
		}
		return list;
	}

	public List<Task> getNotStartedTasks() {
		List<Task> list = new LinkedList<Task>(tasks);
		list.removeAll(getStartedTasks());
		return list;
	}

	public List<Task> getRunningTasks() {
		List<Task> list = new LinkedList<Task>();
		for (Task task : this) {
			if (task.isRunning()) {
				list.add(task);
			}
		}
		return list;
	}

	public List<Task> getNotRunningTasks() {
		List<Task> list = new LinkedList<Task>(tasks);
		list.removeAll(getRunningTasks());
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

	@Override
	public void write(TransformerHandler handler) throws SAXException {
		handler.startElement("", "", "tasks", null);
		for (Task task : this) {
			task.write(handler);
		}
		handler.endElement("", "", "tasks");
	}

	public void clear() {
		tasks.clear();
	}

	@Override
	public void read(Node node) throws SAXException {
		clear();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Task task = new Task();
			task.read(child);
			add(task);
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

	public void remove(Task task) {
		tasks.remove(task);
		task.removeUpdateListener(taskListener);
		fireUpdateEvent();
	}

	public Task get(int index) {
		return tasks.get(index);
	}

	public int size() {
		return tasks.size();
	}

	@Override
	public Iterator<Task> iterator() {
		return tasks.iterator();
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

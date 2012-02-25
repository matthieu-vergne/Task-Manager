package fr.vergne.taskmanager.task;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.vergne.taskmanager.export.Exportable;

@SuppressWarnings("serial")
public class TaskList extends LinkedList<Task> implements Exportable {

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
		List<Task> list = new LinkedList<Task>(this);
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
		List<Task> list = new LinkedList<Task>(this);
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
		List<Task> list = new LinkedList<Task>(this);
		list.removeAll(getRunningTasks());
		return list;
	}

	public static Comparator<Task> getCreationDateComparator() {
		return new Comparator<Task>() {

			@Override
			public int compare(Task t1, Task t2) {
				return t1.getCreationDate().compareTo(t2.getCreationDate());
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
	}
}

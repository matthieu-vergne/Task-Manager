package fr.vergne.taskmanager.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import fr.vergne.taskmanager.gui.gantt.Gantt;
import fr.vergne.taskmanager.gui.todo.Todo;
import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.TaskList;

// TODO add persistency (save+load)
@SuppressWarnings("serial")
public class Gui extends JFrame {

	private JTabbedPane views;

	public static void main(String[] args) {
		final Gui gui = new Gui();
		new Runnable() {
			@Override
			public void run() {
				gui.setVisible(true);
			}
		}.run();
	}

	public Gui() {
		initFrameProperties();
		initComponents();
		pack();
	}

	private void initComponents() {
		setLayout(new GridLayout(1, 1));
		views = new JTabbedPane();
		add(views);

		Todo todo = new Todo();
		views.addTab("Todo", todo);

		final Gantt gantt = new Gantt();
		views.addTab("Gantt", gantt);

		final TaskList taskList = initTaskList();

		gantt.applyTaskList(taskList);
		gantt.resetDisplay();

		todo.applyTaskList(taskList);

		views.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				// do nothing
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				Component component = views.getComponent(views
						.getSelectedIndex());
				component.transferFocus();
			}
		});
	}

	private TaskList initTaskList() {
		Calendar calendar = Calendar.getInstance();

		TaskList todoList = new TaskList();
		Task task = new Task();
		task.setCreationDate(calendar.getTime(), true);
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		task.setTitle("Task 1");
		todoList.add(task);

		task = new Task();
		task.setCreationDate(calendar.getTime(), true);
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		task.setTitle("Task 2");
		todoList.add(task);

		task = new Task();
		task.setCreationDate(calendar.getTime(), true);
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		task.setTitle("Task 3");
		todoList.add(task);

		task = new Task();
		task.setCreationDate(calendar.getTime(), true);
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		task.setTitle("Task 4");
		todoList.add(task);

		task = new Task();
		task.setCreationDate(calendar.getTime(), true);
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		todoList.add(task);
		task.setTitle("Task 5");
		return todoList;
	}

	private void initFrameProperties() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Task Manager");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.width /= 2;
		screenSize.height /= 2;
		setPreferredSize(screenSize);
	}
}

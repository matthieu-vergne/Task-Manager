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

import fr.vergne.taskmanager.Task;
import fr.vergne.taskmanager.TaskList;
import fr.vergne.taskmanager.gui.gantt.Gantt;
import fr.vergne.taskmanager.gui.todo.Todo;

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

		Gantt gantt = new Gantt();
		views.addTab("Gantt", gantt);
		Calendar calendar = Calendar.getInstance();

		TaskList taskList = initTaskList(calendar);

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
				Component component = views.getComponent(views.getSelectedIndex());
				component.transferFocus();
			}
		});
	}

	private TaskList initTaskList(Calendar calendar) {
		TaskList todoList = new TaskList();
		Task task = new Task("Task 1");
		task.setCreationDate(calendar.getTime());
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		todoList.add(task);

		task = new Task("Task 2");
		task.setCreationDate(calendar.getTime());
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		todoList.add(task);

		task = new Task("Task 3");
		task.setCreationDate(calendar.getTime());
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		todoList.add(task);

		task = new Task("Task 4");
		task.setCreationDate(calendar.getTime());
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		todoList.add(task);

		task = new Task("Task 5");
		task.setCreationDate(calendar.getTime());
		calendar.add(Calendar.MINUTE, 5);
		task.setDeadline(calendar.getTime());
		calendar.add(Calendar.MINUTE, -4);
		todoList.add(task);
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

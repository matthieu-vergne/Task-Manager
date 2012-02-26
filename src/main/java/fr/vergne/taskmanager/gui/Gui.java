package fr.vergne.taskmanager.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import fr.vergne.taskmanager.gui.gantt.Gantt;
import fr.vergne.taskmanager.gui.gantt.UpdateListener;
import fr.vergne.taskmanager.gui.todo.Todo;
import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.TaskList;
import fr.vergne.taskmanager.xml.Exporter;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	private final File saveFile = new File("save.xml");
	private final JTabbedPane views = new JTabbedPane();
	private boolean isListModified = false;
	private final TaskList tasks = initTaskList();

	public static void main(String[] args) {
		final Gui gui = Gui.getInstance();
		new Runnable() {
			@Override
			public void run() {
				gui.setVisible(true);
			}
		}.run();
	}

	private static final Gui instance = new Gui();

	public static Gui getInstance() {
		return instance;
	}

	private Gui() {
		initFrameProperties();
		initComponents();
		initListeners();
		pack();
	}

	private void initComponents() {
		setLayout(new GridLayout(1, 1));
		add(views);

		Todo todo = new Todo();
		views.addTab("Todo", todo);

		final Gantt gantt = new Gantt();
		views.addTab("Gantt", gantt);

		gantt.applyTaskList(tasks);
		gantt.resetDisplay();

		todo.applyTaskList(tasks);
	}

	private void initListeners() {
		tasks.addUpdateListener(new UpdateListener() {

			@Override
			public void update() {
				isListModified = true;
				updateTitle();
			}
		});

		for (final Component tab : views.getComponents()) {
			tab.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent arg0) {
					// do nothing
				}

				@Override
				public void keyReleased(KeyEvent event) {
					if (event.getKeyCode() == KeyEvent.VK_INSERT) {
						Task task = new Task();
						TaskUpdateDialog dialog = new TaskUpdateDialog(task);
						dialog.setVisible(true);
						if (dialog.validated) {
							tasks.add(task);
						}
					} else if (event.getKeyCode() == KeyEvent.VK_S
							&& event.isControlDown()) {
						saveTasks(saveFile);
					} else {
						// do nothing
					}
				}

				@Override
				public void keyPressed(KeyEvent arg0) {
					// do nothing
				}
			});
		}

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

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				// nothing to do
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// nothing to do
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// nothing to do
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// nothing to do
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				if (isListModified) {
					int answer = JOptionPane
							.showConfirmDialog(
									null,
									"The tasks has been changed, save the current list?",
									"Save modification", JOptionPane.OK_OPTION);
					if (answer == 0) {
						saveTasks(saveFile);
					} else {
						// do not save
					}
				}
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// nothing to do
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// nothing to do
			}
		});
	}

	public void saveTasks(File file) {
		Logger.getAnonymousLogger().info("Save tasks in " + file);
		Exporter.write(tasks, file);
		isListModified = false;
		updateTitle();
	}

	public void loadTasks(File file) {
		Logger.getAnonymousLogger().info("Load tasks from " + file);
		Exporter.read(tasks, file);
	}

	private TaskList initTaskList() {
		TaskList tasks = new TaskList();
		if (saveFile.exists()) {
			Exporter.read(tasks, saveFile);
		} else {
			Calendar calendar = Calendar.getInstance();

			Task task = new Task();
			task.setCreationDate(calendar.getTime(), true);
			calendar.add(Calendar.MINUTE, 5);
			task.setDeadline(calendar.getTime());
			calendar.add(Calendar.MINUTE, -4);
			task.setTitle("Task 1");
			tasks.add(task);

			task = new Task();
			task.setCreationDate(calendar.getTime(), true);
			calendar.add(Calendar.MINUTE, 5);
			task.setDeadline(calendar.getTime());
			calendar.add(Calendar.MINUTE, -4);
			task.setTitle("Task 2");
			tasks.add(task);

			task = new Task();
			task.setCreationDate(calendar.getTime(), true);
			calendar.add(Calendar.MINUTE, 5);
			task.setDeadline(calendar.getTime());
			calendar.add(Calendar.MINUTE, -4);
			task.setTitle("Task 3");
			tasks.add(task);

			task = new Task();
			task.setCreationDate(calendar.getTime(), true);
			calendar.add(Calendar.MINUTE, 5);
			task.setDeadline(calendar.getTime());
			calendar.add(Calendar.MINUTE, -4);
			task.setTitle("Task 4");
			tasks.add(task);

			task = new Task();
			task.setCreationDate(calendar.getTime(), true);
			calendar.add(Calendar.MINUTE, 5);
			task.setDeadline(calendar.getTime());
			calendar.add(Calendar.MINUTE, -4);
			tasks.add(task);
			task.setTitle("Task 5");
			
			isListModified = true;
		}
		return tasks;
	}

	private void initFrameProperties() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.width /= 2;
		screenSize.height /= 2;
		setPreferredSize(screenSize);
		updateTitle();
	}

	private void updateTitle() {
		String title = "Task Manager";
		if (isListModified) {
			title += " *";
		}
		setTitle(title);
	}
}

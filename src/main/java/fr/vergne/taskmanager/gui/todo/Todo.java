package fr.vergne.taskmanager.gui.todo;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.TaskList;

@SuppressWarnings("serial")
public class Todo extends JPanel {
	private final JTable table = new JTable();

	public Todo() {
		setLayout(new GridLayout(1, 1));
		add(table);
	}

	public void applyTaskList(TaskList list) {
		Object[] titles = new String[] { "Title", "Status", "Creation",
				"Deadline", "Description" };
		Object[][] data = new Object[list.size()][];
		for (int i = 0; i < data.length; i++) {
			Task task = list.get(i);
			data[i] = new Object[] { task.getTitle(), task.getStatus(),
					task.getCreationDate(), task.getDeadline(),
					task.getDescription() };
		}
		DefaultTableModel model = new DefaultTableModel(data, titles);
		table.setModel(model);
	}
}

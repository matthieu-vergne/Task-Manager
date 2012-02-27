package fr.vergne.taskmanager.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import fr.vergne.taskmanager.gui.TaskUpdateDialog.JDateField;
import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.Task.TaskRun;

@SuppressWarnings("serial")
public class CommentUpdateDialog extends JDialog {

	public boolean validated = false;
	private final Task task;
	private final TaskRun run;
	private final JTextField commentEntry = new JTextField();

	public CommentUpdateDialog(Task task, TaskRun run) {
		this.task = task;
		this.run = run;
		initDialog();
		pack();
	}

	private void initDialog() {
		initComponents();
		initListeners();

		setTitle(task.getTitle());
		setModal(true);
	}

	private void initComponents() {
		setLayout(new GridLayout(5, 2, 5, 5));
		{
			add(new JLabel("Task:"));
			add(new JLabel(task.getTitle()));
		}

		{
			add(new JLabel("Subtask:"));
			String start = JDateField.DATE_FORMAT.format(run.getStart());
			String stop = JDateField.DATE_FORMAT.format(run.getStop());
			add(new JLabel(start + " to " + stop));
		}

		{
			JLabel label = new JLabel("Comment:");
			label.setLabelFor(commentEntry);
			add(label);
			commentEntry.setText(run.getComment());
			add(commentEntry);
		}

		{
			add(new JButton(new AbstractAction("OK") {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					validateEntriesAndDispose();
				}
			}));
		}

		{
			final JDialog dialog = this;
			add(new JButton(new AbstractAction("Cancel") {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					dialog.dispose();
				}
			}));
		}
	}

	private void initListeners() {
		KeyListener entryListener = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// do nothing
			}

			@Override
			public void keyReleased(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					validateEntriesAndDispose();
				} else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				} else {
					// do nothing
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// do nothing
			}
		};
		commentEntry.addKeyListener(entryListener);
	}

	private void validateEntriesAndDispose() {
		run.setComment(commentEntry.getText());
		validated = true;
		dispose();
	}

}

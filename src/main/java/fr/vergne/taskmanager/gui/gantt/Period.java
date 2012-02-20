package fr.vergne.taskmanager.gui.gantt;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import fr.vergne.taskmanager.task.Task;

@SuppressWarnings("serial")
public class Period extends JPanel {

	private final JLabel text = new JLabel();
	private final Task task;
	private final Date start;
	private final Date stop;

	public Period(final Task task) {
		this.task = task;

		setLayout(new GridBagLayout());
		setBorder(new LineBorder(Color.BLACK));
		setBackground(null);
		GridBagConstraints constraints = new GridBagConstraints();
		add(this.text, constraints);

		text.setText(task.getTitle());
		start = task.getCreationDate();
		stop = task.getDeadline();
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	public String getText() {
		return text.getText();
	}

	public Date getStart() {
		return start;
	}

	public Date getStop() {
		return stop;
	}

	public Task getTask() {
		return task;
	}
}

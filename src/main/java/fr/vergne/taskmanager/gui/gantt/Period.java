package fr.vergne.taskmanager.gui.gantt;

import java.awt.Color;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import fr.vergne.taskmanager.task.Task;

@SuppressWarnings("serial")
public class Period extends JLabel {

	private final Task task;

	public Period(final Task task) {
		this.task = task;

		setBorder(new LineBorder(Color.BLACK));
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		setBackground(null);
	}

	@Override
	public void setText(String text) {
		if (task != null) {
			task.setTitle(text);
		} else {
			super.setText(text);
		}
	}

	@Override
	public String getText() {
		if (task != null) {
			return task.getTitle();
		} else {
			return super.getText();
		}
	}

	public Date getStart() {
		return task.getCreationDate();
	}

	public Date getStop() {
		return task.getDeadline();
	}

	public Task getTask() {
		return task;
	}
}

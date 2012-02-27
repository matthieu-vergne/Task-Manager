package fr.vergne.taskmanager.gui.gantt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import fr.vergne.taskmanager.gui.CommentUpdateDialog;
import fr.vergne.taskmanager.gui.TaskUpdateDialog;
import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.Task.TaskRun;

@SuppressWarnings("serial")
public class Period extends JPanel {

	private final Task task;
	private final JLabel label;

	public Period(final Task task) {
		this.task = task;

		setBorder(new LineBorder(Color.BLACK));
		setBackground(Color.WHITE);
		GridLayout gridLayout = new GridLayout(1, 1);
		setLayout(gridLayout);
		// reduce as much as possible the size of the component
		setMaximumSize(new Dimension(0, 0));

		label = new JLabel() {
			@Override
			public String getText() {
				return task.getTitle();
			}
		};
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		add(label);

		if (!task.getRunningHistory().isEmpty()) {
			gridLayout.setRows(2);
			JPanel subtasks = new JPanel();
			add(subtasks);
			SpringLayout layout = new SpringLayout();
			subtasks.setLayout(layout);

			final long min = getStart().getTime();
			final long max = getStop().getTime();
			final long delta = max - min;
			SpringWidth ref = new SpringWidth(subtasks);
			for (final TaskRun run : task.getRunningHistory()) {
				final long start = run.getStart().getTime();
				final long stop = run.getStop().getTime();
				JLabel runLabel = new JLabel(run.getComment());
				runLabel.setBorder(new LineBorder(Color.BLACK));
				runLabel.setHorizontalAlignment(JLabel.CENTER);
				runLabel.setVerticalAlignment(JLabel.CENTER);
				subtasks.add(runLabel);
				Spring scale1 = Spring
						.scale(ref, (float) (start - min) / delta);
				Spring scale2 = Spring.scale(ref, (float) (stop - min) / delta);
				layout.putConstraint(SpringLayout.WEST, runLabel, scale1,
						SpringLayout.WEST, subtasks);
				layout.putConstraint(SpringLayout.EAST, runLabel, scale2,
						SpringLayout.WEST, subtasks);
				layout.putConstraint(SpringLayout.NORTH, runLabel, 0,
						SpringLayout.NORTH, subtasks);
				layout.putConstraint(SpringLayout.SOUTH, runLabel, 0,
						SpringLayout.SOUTH, subtasks);
				
				runLabel.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent arg0) {
						// do nothing
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						// do nothing
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						// do nothing
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						// do nothing
					}

					@Override
					public void mouseClicked(MouseEvent event) {
						if (event.getButton() == MouseEvent.BUTTON1
								&& event.getClickCount() > 1) {
							showCommentUpdateDialog(run);
						} else {
							// do nothing
						}
					}

				});
			}
		}

		initListeners();
	}

	private void initListeners() {
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// do nothing
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// do nothing
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// do nothing
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// do nothing
			}

			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1
						&& event.getClickCount() > 1) {
					showTaskUpdateDialog();
				} else {
					// do nothing
				}
			}

		});
	}

	public Date getStart() {
		Date min = task.getStart();
		for (TaskRun run : task.getRunningHistory()) {
			Date start = run.getStart();
			if (min == null || start.before(min)) {
				min = start;
			}
		}
		return min;
	}

	public Date getStop() {
		Date max = task.getDeadline();
		for (TaskRun run : task.getRunningHistory()) {
			Date stop = run.getStop();
			if (max == null || stop.after(max)) {
				max = stop;
			}
		}
		return max;
	}

	public Task getTask() {
		return task;
	}

	public boolean isBoundedPeriod() {
		return getStart() != null && getStop() != null;
	}

	public boolean isStartMilestone() {
		return getStart() != null && getStop() == null;
	}

	public boolean isStopMilestone() {
		return getStart() == null && getStop() != null;
	}

	public boolean isMilestone() {
		return isStartMilestone() || isStopMilestone();
	}

	public void showTaskUpdateDialog() {
		new TaskUpdateDialog(getTask()).setVisible(true);
	}
	
	public void showCommentUpdateDialog(TaskRun run) {
		new CommentUpdateDialog(getTask(), run).setVisible(true);
	}
}

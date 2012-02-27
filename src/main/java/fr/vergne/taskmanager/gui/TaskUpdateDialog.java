package fr.vergne.taskmanager.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.TaskStatus;

// TODO manage manual modification of dates (retyping, removing, ...)
@SuppressWarnings("serial")
public class TaskUpdateDialog extends JDialog {

	public boolean validated = false;
	private final Task task;
	private final JTextField titleEntry = new JTextField();
	private final JDateField startEntry = new JDateField(true);
	private final JDateField deadlineEntry = new JDateField(true);
	private final JComboBox statusEntry = new JComboBox();

	public TaskUpdateDialog(Task task) {
		this.task = task;
		initDialog();
		pack();
	}

	private void initDialog() {
		initComponents();
		initListeners();

		titleEntry.setText(task.getTitle());
		startEntry.setDate(task.getStart());
		deadlineEntry.setDate(task.getDeadline());
		statusEntry.setSelectedItem(task.getStatus());

		setTitle(task.getTitle());
		setModal(true);
	}

	private void initComponents() {
		setLayout(new GridLayout(5, 2, 5, 5));
		{
			JLabel label = new JLabel("Title:");
			label.setLabelFor(titleEntry);
			add(label);
			add(titleEntry);
		}

		{
			JLabel label = new JLabel("Start:");
			label.setLabelFor(titleEntry);
			add(label);
			add(startEntry);
		}

		{
			JLabel label = new JLabel("Deadline:");
			label.setLabelFor(deadlineEntry);
			add(label);
			add(deadlineEntry);
		}

		{
			JLabel label = new JLabel("Status:");
			label.setLabelFor(statusEntry);
			add(label);
			add(statusEntry);
			for (TaskStatus value : TaskStatus.values()) {
				statusEntry.addItem(value);
			}
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
		titleEntry.addKeyListener(entryListener);
		startEntry.addKeyListener(entryListener);
		deadlineEntry.addKeyListener(entryListener);
		statusEntry.addKeyListener(entryListener);
	}

	private void validateEntriesAndDispose() {
		task.setTitle(titleEntry.getText());
		task.setStart((Date) startEntry.getDate());
		task.setDeadline((Date) deadlineEntry.getDate());
		task.setStatus((TaskStatus) statusEntry.getSelectedItem());
		validated = true;
		dispose();
	}

	static class IntegerFormatter extends AbstractFormatter {

		@Override
		public Object stringToValue(String string) throws ParseException {
			return Integer.parseInt(string);
		}

		@Override
		public String valueToString(Object integer) throws ParseException {
			if (integer == null) {
				return "";
			} else {
				return String.format("%02d", integer);
			}
		}
	}

	static class JDateField extends JPanel {
		private Calendar calendar = new GregorianCalendar();
		private final JTextField dateField = new JTextField();
		private final JCheckBox checkbox = new JCheckBox();
		private final boolean isFacultative;

		public JDateField(boolean facultative) {
			isFacultative = facultative;
			initComponents();
			initListeners();
		}

		public JDateField() {
			this(false);
		}

		private void initListeners() {
			checkbox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					updateDateField();
				}

			});

			KeyListener keyListener = new KeyListener() {

				@Override
				public void keyTyped(KeyEvent arg0) {
					// do nothing
				}

				@Override
				public void keyReleased(KeyEvent event) {
					int caretPosition = dateField.getCaretPosition();

					int unit;
					if (caretPosition > 16) {
						unit = Calendar.SECOND;
					} else if (caretPosition > 13) {
						unit = Calendar.MINUTE;
					} else if (caretPosition > 10) {
						unit = Calendar.HOUR_OF_DAY;
					} else if (caretPosition > 7) {
						unit = Calendar.DAY_OF_MONTH;
					} else if (caretPosition > 4) {
						unit = Calendar.MONTH;
					} else if (caretPosition >= 0) {
						unit = Calendar.YEAR;
					} else {
						throw new RuntimeException(
								"This case should not happen.");
					}

					int step;
					if (event.getKeyCode() == KeyEvent.VK_DOWN) {
						step = -1;
					} else if (event.getKeyCode() == KeyEvent.VK_UP) {
						step = 1;
					} else if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
						switch (unit) {
						case Calendar.SECOND:
						case Calendar.MINUTE:
						case Calendar.DAY_OF_MONTH:
						case Calendar.YEAR:
							step = 10;
							break;
						case Calendar.HOUR_OF_DAY:
						case Calendar.MONTH:
							step = 6;
							break;

						default:
							throw new RuntimeException(unit
									+ " is not a managed case.");
						}
					} else if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
						switch (unit) {
						case Calendar.SECOND:
						case Calendar.MINUTE:
						case Calendar.DAY_OF_MONTH:
						case Calendar.YEAR:
							step = -10;
							break;
						case Calendar.HOUR_OF_DAY:
						case Calendar.MONTH:
							step = -6;
							break;

						default:
							throw new RuntimeException(unit
									+ " is not a managed case.");
						}
					} else {
						step = 0;
					}

					if (step != 0) {
						calendar.add(unit, step);
						updateDateField();
						dateField.setCaretPosition(caretPosition);
					} else {
						// update nothing
					}
				}

				@Override
				public void keyPressed(KeyEvent arg0) {
					// do nothing
				}
			};

			dateField.addKeyListener(keyListener);
		}

		private void initComponents() {
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			add(dateField);

			dateField.setColumns(19);

			if (isFacultative) {
				add(new JLabel("  "));
				add(checkbox);
			}
		}

		public Date getDate() {
			return checkbox.isSelected() ? calendar.getTime() : null;
		}

		public void setDate(Date date) {
			if (date != null) {
				checkbox.setSelected(true);
			} else {
				checkbox.setSelected(false);
				date = new Date();
			}
			calendar.setTime(date);
			updateDateField();
		}

		private void updateDateField() {
			boolean checked = checkbox.isSelected();
			dateField.setEnabled(checked);

			dateField.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(calendar.getTime()));
		}

		@Override
		public synchronized void addKeyListener(KeyListener keyListener) {
			dateField.addKeyListener(keyListener);
		}

		@Override
		public synchronized KeyListener[] getKeyListeners() {
			return dateField.getKeyListeners();
		}
	}

}

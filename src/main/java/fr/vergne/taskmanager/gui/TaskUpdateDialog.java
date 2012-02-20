package fr.vergne.taskmanager.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.vergne.taskmanager.Task;

@SuppressWarnings("serial")
public class TaskUpdateDialog extends JDialog {

	private final Task task;
	private final JTextField titleEntry = new JTextField();
	private final JDateField startEntry = new JDateField();
	private final JDateField stopEntry = new JDateField();

	public TaskUpdateDialog(Task task) {
		this.task = task;
		initDialog();
		pack();
	}

	private void initDialog() {
		initComponents();
		initListeners();

		titleEntry.setText(task.getTitle());
		startEntry.setDate(task.getCreationDate());
		stopEntry.setDate(task.getDeadline());

		setTitle(task.getTitle());
		setModal(true);
	}

	private void initComponents() {
		setLayout(new GridLayout(4, 2, 5, 5));
		{
			JLabel label = new JLabel("Name:");
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
			JLabel label = new JLabel("Stop:");
			label.setLabelFor(stopEntry);
			add(label);
			add(stopEntry);
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
		stopEntry.addKeyListener(entryListener);
	}

	private void validateEntriesAndDispose() {
		task.setTitle(titleEntry.getText());
		task.setCreationDate((Date) startEntry.getDate());
		task.setDeadline((Date) stopEntry.getDate());
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
		private final JFormattedTextField yearField = new JFormattedTextField(
				new IntegerFormatter());
		private final JFormattedTextField monthField = new JFormattedTextField(
				new IntegerFormatter());
		private final JFormattedTextField dayField = new JFormattedTextField(
				new IntegerFormatter());
		private final JFormattedTextField hourField = new JFormattedTextField(
				new IntegerFormatter());
		private final JFormattedTextField minuteField = new JFormattedTextField(
				new IntegerFormatter());
		private final JFormattedTextField secondField = new JFormattedTextField(
				new IntegerFormatter());
		private final JFormattedTextField[] fields = new JFormattedTextField[] {
				yearField, monthField, dayField, hourField, minuteField,
				secondField };

		public JDateField() {
			initComponents();
			initListeners();
		}

		private void initListeners() {
			PropertyChangeListener valueListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getNewValue() != null) {
						if (event.getSource() == yearField) {
							calendar.set(Calendar.YEAR,
									(Integer) yearField.getValue());
						} else if (event.getSource() == monthField) {
							calendar.set(Calendar.MONTH,
									(Integer) monthField.getValue() - 1);
						} else if (event.getSource() == dayField) {
							calendar.set(Calendar.DAY_OF_MONTH,
									(Integer) dayField.getValue());
						} else if (event.getSource() == hourField) {
							calendar.set(Calendar.HOUR_OF_DAY,
									(Integer) hourField.getValue());
						} else if (event.getSource() == minuteField) {
							calendar.set(Calendar.MINUTE,
									(Integer) minuteField.getValue());
						} else if (event.getSource() == secondField) {
							calendar.set(Calendar.SECOND,
									(Integer) secondField.getValue());
						} else {
							throw new IllegalStateException(
									"This case should not happen.");
						}
					}
				}
			};

			KeyListener keyListener = new KeyListener() {

				@Override
				public void keyTyped(KeyEvent arg0) {
					// do nothing
				}

				@Override
				public void keyReleased(KeyEvent event) {
					if (event.getKeyCode() == KeyEvent.VK_DOWN) {
						JFormattedTextField field = (JFormattedTextField) event
								.getSource();
						field.setValue((Integer) field.getValue() - 1);
						updateFields();
					} else if (event.getKeyCode() == KeyEvent.VK_UP) {
						JFormattedTextField field = (JFormattedTextField) event
								.getSource();
						field.setValue((Integer) field.getValue() + 1);
						updateFields();
					} else if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
						JFormattedTextField field = (JFormattedTextField) event
								.getSource();
						field.setValue((Integer) field.getValue() + 10);
						updateFields();
					} else if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
						JFormattedTextField field = (JFormattedTextField) event
								.getSource();
						field.setValue((Integer) field.getValue() - 10);
						updateFields();
					} else {
						// do nothing
					}
				}

				@Override
				public void keyPressed(KeyEvent arg0) {
					// do nothing
				}
			};

			for (JFormattedTextField field : fields) {
				field.addPropertyChangeListener("value", valueListener);
				field.addKeyListener(keyListener);
			}
		}

		private void initComponents() {
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			add(yearField);
			add(new JLabel("-"));
			add(monthField);
			add(new JLabel("-"));
			add(dayField);
			add(new JLabel("    "));
			add(hourField);
			add(new JLabel(":"));
			add(minuteField);
			add(new JLabel(":"));
			add(secondField);

			yearField.setColumns(4);
			monthField.setColumns(2);
			dayField.setColumns(2);
			hourField.setColumns(2);
			minuteField.setColumns(2);
			secondField.setColumns(2);
		}

		public Date getDate() {
			return calendar.getTime();
		}

		public void setDate(Date date) {
			calendar.setTime(date);
			updateFields();
		}

		private void updateFields() {
			yearField.setValue(calendar.get(Calendar.YEAR));
			monthField.setValue(calendar.get(Calendar.MONTH) + 1);
			dayField.setValue(calendar.get(Calendar.DAY_OF_MONTH));
			hourField.setValue(calendar.get(Calendar.HOUR_OF_DAY));
			minuteField.setValue(calendar.get(Calendar.MINUTE));
			secondField.setValue(calendar.get(Calendar.SECOND));
		}

		@Override
		public synchronized void addKeyListener(KeyListener keyListener) {
			for (JFormattedTextField field : fields) {
				field.addKeyListener(keyListener);
			}
		}

		@Override
		public synchronized KeyListener[] getKeyListeners() {
			return fields[0].getKeyListeners();
		}
	}
}

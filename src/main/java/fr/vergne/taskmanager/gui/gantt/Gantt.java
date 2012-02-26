package fr.vergne.taskmanager.gui.gantt;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import fr.vergne.taskmanager.gui.gantt.TimeBar.UnitDescriptor;
import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.TaskList;

// TODO export to pictures
// TODO add cursor looking for the position of the mouse
// TODO display running parts regarding tasks history
@SuppressWarnings("serial")
public class Gantt extends JPanel {

	private final TimeBar highTimeBar = new TimeBar();
	private final TimeBar lowTimeBar = new TimeBar() {
		public void paint(Graphics arg0) {
			super.paint(arg0);

			UnitDescriptor ref = lowTimeBar.getCurrentDescriptor();
			long refValue = ref.getUnitMultiplicator() * ref.getUnitStep();
			for (UnitDescriptor desc : highTimeBar.getDescriptors()) {
				long descValue = desc.getUnitMultiplicator()
						* desc.getUnitStep();
				if (descValue >= 3 * refValue) {
					highTimeBar.setForcedDescriptor(desc);
					break;
				}
			}
		};
	};
	private final PeriodCanvas periodCanvas = new PeriodCanvas();
	private final JScrollPane periodPane = new JScrollPane(periodCanvas);
	private final JPanel options = new JPanel();
	private final JPanel timeCursor = new JPanel();
	private final SpringLayout layout = new SpringLayout();
	private long median = new Date().getTime();
	private long radius = 1800000;
	private final Thread cursorThread;

	public Gantt() {
		initComponents();
		initListeners();

		final Gantt gantt = this;
		cursorThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (gantt.isFocusable()) {
					try {
						Thread.sleep(Math.min(Math.max(radius / 1000, 100),
								1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					updateCursor();
					validate();
				}
			}
		});
		cursorThread.start();
	}

	private void initListeners() {
		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// do nothing
			}

			@Override
			public void keyReleased(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
					zoom(0.8);
				} else if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
					zoom(1.0 / 0.8);
				} else if (event.getKeyCode() == KeyEvent.VK_F5) {
					resetDisplay();
				} else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
					goTo(new Date((long) (median - radius * 0.1)));
				} else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
					goTo(new Date((long) (median + radius * 0.1)));
				} else {
					// do nothing
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// do nothing
			}
		};
		addKeyListener(keyListener);
		for (Component component : options.getComponents()) {
			component.addKeyListener(keyListener);
		}
	}

	public void resetDisplay() {
		periodCanvas.setMinDate(periodCanvas.getActualMinDate());
		periodCanvas.setMaxDate(periodCanvas.getActualMaxDate());
		long min = periodCanvas.getMinDate().getTime();
		long max = periodCanvas.getMaxDate().getTime();
		median = (max + min) / 2;
		radius = median - min;
		repaint();
	}

	private void initComponents() {
		options.setLayout(new FlowLayout());
		options.add(new JButton(new AbstractAction("Start sort") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				periodCanvas.sortByStartDate();
			}
		}));
		options.add(new JButton(new AbstractAction("Stop sort") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				periodCanvas.sortByStopDate();
			}
		}));

		initTimeBars();

		add(lowTimeBar);
		add(highTimeBar);
		add(periodPane);
		add(timeCursor);
		add(options);
		timeCursor.setBackground(Color.GREEN);

		setLayout(layout);
		layout.putConstraint(SpringLayout.NORTH, highTimeBar, 0,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, highTimeBar, 0,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, highTimeBar, 0,
				SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, lowTimeBar, 0,
				SpringLayout.SOUTH, highTimeBar);
		layout.putConstraint(SpringLayout.WEST, lowTimeBar, 0,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, lowTimeBar, 0,
				SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.SOUTH, options, 0,
				SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, lowTimeBar, 0,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, lowTimeBar, 0,
				SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, periodPane, 0,
				SpringLayout.SOUTH, lowTimeBar);
		layout.putConstraint(SpringLayout.WEST, periodPane, 0,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, periodPane, 0,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, periodPane, 0,
				SpringLayout.NORTH, options);

		layout.putConstraint(SpringLayout.NORTH, timeCursor, 0,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, timeCursor, 0,
				SpringLayout.SOUTH, periodPane);
		layout.putConstraint(SpringLayout.WIDTH, timeCursor, 2,
				SpringLayout.WEST, this);
	}

	private void initTimeBars() {
		long mult = 1000;
		int unit = Calendar.SECOND;
		String format = "HH:mm:ss";
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 5, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 10, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 30, unit, format));

		format = "yyyy-MM-dd, HH:mm:ss";
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 2, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 5, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 10, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 30, unit, format));

		mult *= 60;
		unit = Calendar.MINUTE;
		format = "HH:mm";
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 5, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 10, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 30, unit, format));

		format = "yyyy-MM-dd, HH:mm";
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 2, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 5, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 10, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 20, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 30, unit, format));

		mult *= 60;
		unit = Calendar.HOUR_OF_DAY;
		format = "HH'h'";
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 6, unit, format));

		format = "yyyy-MM-dd, HH'h'";
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 3, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 6, unit, format));

		mult *= 24;
		unit = Calendar.DAY_OF_MONTH;
		format = "EEE dd";
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 7, unit, format));

		format = "yyyy-MM-dd";
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 3, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 7, unit, format));

		mult *= 30;
		unit = Calendar.MONTH;
		format = "MMM";
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));

		format = "yyyy-MM";
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 3, unit, format));

		mult *= 12;
		unit = Calendar.YEAR;
		format = "yyyy";
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 5, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 10, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 20, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 50, unit, format));
		lowTimeBar.addDescriptor(new UnitDescriptor(mult, 100, unit, format));

		format = "yyyy";
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 1, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 5, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 10, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 20, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 50, unit, format));
		highTimeBar.addDescriptor(new UnitDescriptor(mult, 100, unit, format));
	}

	@Override
	public void paint(Graphics arg0) {
		Date minDate = new Date(median - radius);
		Date maxDate = new Date(median + radius);

		lowTimeBar.setMinDate(minDate);
		lowTimeBar.setMaxDate(maxDate);
		lowTimeBar.invalidate();

		highTimeBar.setMinDate(minDate);
		highTimeBar.setMaxDate(maxDate);
		highTimeBar.invalidate();

		periodCanvas.setMinDate(minDate);
		periodCanvas.setMaxDate(maxDate);
		periodCanvas.invalidate();

		super.paint(arg0);

		updateCursor();
		timeCursor.repaint();
	}

	private void updateCursor() {
		long min = lowTimeBar.getMinDate().getTime();
		long ref = new Date().getTime();
		long delta = lowTimeBar.getMaxDate().getTime() - min;
		int x = (int) ((ref - min) * getWidth() / delta);
		x = Math.min(Math.max(-timeCursor.getWidth(), x), getWidth());
		layout.putConstraint(SpringLayout.WEST, timeCursor, x,
				SpringLayout.WEST, this);
		timeCursor.invalidate();
	}

	public void zoom(double ratio) {
		radius *= ratio;
		radius = (long) Math.min(Math.max(radius, 1000), 50 * 365.25 * 24 * 60
				* 60 * 1000);
		repaint();
	}

	public void goTo(Date date) {
		median = date.getTime();
		repaint();
	}

	public void addPeriod(Period period) {
		periodCanvas.addPeriod(period);
	}

	UpdateListener listener = new UpdateListener() {

		@Override
		public void update() {
			renewPeriods();
			repaint();
		}
	};

	private TaskList tasks;

	public void applyTaskList(TaskList list) {
		if (tasks != null) {
			tasks.removeUpdateListener(listener);
		}
		tasks = list;

		renewPeriods();
		tasks.addUpdateListener(listener);
	}

	private void renewPeriods() {
		periodCanvas.clear();
		for (final Task task : tasks) {
			if (task.hasDeadline()) {
				final Period period = new Period(task);
				JPopupMenu menu = new JPopupMenu();
				menu.add(new AbstractAction("Edit...") {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						period.showUpdateDialog();
					}

				});
				menu.add(new AbstractAction("Remove") {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						tasks.remove(task);
					}

				});
				period.setComponentPopupMenu(menu);
				periodCanvas.addPeriod(period);
			}
		}
	}
}

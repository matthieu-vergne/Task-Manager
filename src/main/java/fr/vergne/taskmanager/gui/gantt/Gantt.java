package fr.vergne.taskmanager.gui.gantt;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;

import fr.vergne.taskmanager.gui.gantt.TimeBar.UnitDescriptor;
import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.TaskList;

// TODO add cursor looking for the position of the mouse
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
	private final JPanel periods = new JPanel();
	private final JPanel options = new JPanel();
	private final JPanel timeCursor = new JPanel();
	private final SpringLayout periodsLayout = new SpringLayout();
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
				} else if (event.getKeyCode() == KeyEvent.VK_P
						&& event.isControlDown()) {
					snapshot(false);
					event.setKeyCode(0);
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
		periodCanvas.resetMinMax();
		long min = periodCanvas.getMinDate().getTime();
		long max = periodCanvas.getMaxDate().getTime();
		median = (max + min) / 2;
		radius = median - min;
		repaint();
	}

	private void initComponents() {
		{
			initTimeBars();
			periods.setBackground(new Color(0, 0, 0, 0));
			periods.setLayout(periodsLayout);
			periods.add(timeCursor);
			periods.add(lowTimeBar);
			periods.add(highTimeBar);
			periods.add(periodCanvas);
			timeCursor.setBackground(Color.GREEN);

			periodsLayout.putConstraint(SpringLayout.NORTH, highTimeBar, 0,
					SpringLayout.NORTH, periods);
			periodsLayout.putConstraint(SpringLayout.WEST, highTimeBar, 0,
					SpringLayout.WEST, periods);
			periodsLayout.putConstraint(SpringLayout.EAST, highTimeBar, 0,
					SpringLayout.EAST, periods);

			periodsLayout.putConstraint(SpringLayout.NORTH, lowTimeBar, 0,
					SpringLayout.SOUTH, highTimeBar);
			periodsLayout.putConstraint(SpringLayout.WEST, lowTimeBar, 0,
					SpringLayout.WEST, periods);
			periodsLayout.putConstraint(SpringLayout.EAST, lowTimeBar, 0,
					SpringLayout.EAST, periods);

			periodsLayout.putConstraint(SpringLayout.NORTH, periodCanvas, 0,
					SpringLayout.SOUTH, lowTimeBar);
			periodsLayout.putConstraint(SpringLayout.WEST, periodCanvas, 0,
					SpringLayout.WEST, periods);
			periodsLayout.putConstraint(SpringLayout.EAST, periodCanvas, 0,
					SpringLayout.EAST, periods);
			periodsLayout.putConstraint(SpringLayout.SOUTH, periodCanvas, 0,
					SpringLayout.SOUTH, periods);

			periodsLayout.putConstraint(SpringLayout.NORTH, timeCursor, 0,
					SpringLayout.NORTH, periods);
			periodsLayout.putConstraint(SpringLayout.SOUTH, timeCursor, 0,
					SpringLayout.SOUTH, periods);
			periodsLayout.putConstraint(SpringLayout.WIDTH, timeCursor, 2,
					SpringLayout.WEST, periods);
		}

		{
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
			options.add(new JButton(new AbstractAction("Snapshot") {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					snapshot(false);
				}
			}));
			options.add(new JButton(new AbstractAction("Snapshot (cursors)") {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					snapshot(true);
				}
			}));
		}

		{
			setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridy = 0;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 1;
			constraints.weighty = 1;
			add(periods, constraints);
			constraints.gridy = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weighty = 0;
			add(options, constraints);
		}
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
		x = Math.min(Math.max(-timeCursor.getWidth(), x), periods.getWidth());
		periodsLayout.putConstraint(SpringLayout.WEST, timeCursor, x,
				SpringLayout.WEST, periods);
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
		for (final Task task : tasks.getAllTasks(false)) {
			final Period period = new Period(task);
			JPopupMenu menu = new JPopupMenu();
			menu.add(new AbstractAction("Edit...") {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					period.showTaskUpdateDialog();
				}

			});
			menu.add(new AbstractAction("Remove") {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					tasks.delete(task);
				}

			});
			period.setComponentPopupMenu(menu);
			periodCanvas.addPeriod(period);
		}
	}

	public void snapshot(boolean displayCursors) {
		BufferedImage image = new BufferedImage(periods.getWidth(),
				periods.getHeight(), BufferedImage.TYPE_INT_ARGB);

		timeCursor.setVisible(displayCursors);
		periods.paint(image.getGraphics());
		timeCursor.setVisible(true);

		File file = new File("snapshot.png");
		Logger.getAnonymousLogger().info("Snapshot: " + file);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

package fr.vergne.taskmanager.gui.gantt;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class PeriodCanvas extends JPanel {

	private final List<Period> periods = new LinkedList<Period>();
	private Date minDate = new Date();
	private Date maxDate = new Date(minDate.getTime() + 3600000);

	public PeriodCanvas() {
		setBackground(new Color(0, 0, 0, 0));
	}

	public Date getActualMinDate() {
		Date min = null;
		for (Period period : periods) {
			Date start = period.getStart();
			if (min == null || start != null && start.before(min)) {
				min = start;
			}
		}
		return min;
	}

	public Date getActualMaxDate() {
		Date max = null;
		for (Period period : periods) {
			Date stop = period.getStop();
			if (max == null || stop != null && stop.after(max)) {
				max = stop;
			}
		}
		return max;
	}

	private Date oldMinDate = null;
	private Date oldMaxDate = null;
	private boolean periodUpdated = false;

	@Override
	public void paint(Graphics graphics) {
		graphics.clearRect(0, 0, getWidth(), getHeight());

		if (!periodUpdated && minDate.equals(oldMinDate)
				&& maxDate.equals(oldMaxDate)) {
			// do not recompute the periods
		} else {
			placePeriods();
			oldMinDate = minDate;
			oldMaxDate = maxDate;
			periodUpdated = false;
		}

		super.paint(graphics);
	}

	private void placePeriods() {
		final long min = minDate.getTime();
		final long max = maxDate.getTime();
		final long delta = max - min;
		Period lastDisplayedPeriod = null;
		SpringLayout layout = new SpringLayout();
		Collection<Period> milestones = new LinkedList<Period>();
		for (Period period : periods) {
			if (period.isBoundedPeriod() || period.isMilestone()) {
				if (period.isBoundedPeriod() || period.isStartMilestone()) {
					long start = period.getStart().getTime();
					float factor = (float) (start - min) / delta;
					Spring scale = Spring.scale(new SpringWidth(this), factor);
					layout.putConstraint(SpringLayout.WEST, period, scale,
							SpringLayout.WEST, this);
				}

				if (period.isBoundedPeriod() || period.isStopMilestone()) {
					long stop = period.getStop().getTime();
					float factor = (float) (stop - min) / delta;
					Spring scale = Spring.scale(new SpringWidth(this), factor);
					layout.putConstraint(SpringLayout.EAST, period, scale,
							SpringLayout.WEST, this);
				}

				if (period.isMilestone()) {
					milestones.add(period);
				} else {
					if (lastDisplayedPeriod == null) {
						layout.putConstraint(SpringLayout.NORTH, period, 5,
								SpringLayout.NORTH, this);
					} else {
						layout.putConstraint(SpringLayout.NORTH, period, 5,
								SpringLayout.SOUTH, lastDisplayedPeriod);
					}
					lastDisplayedPeriod = period;
				}
			} else {
				layout.putConstraint(SpringLayout.SOUTH, period, 0,
						SpringLayout.NORTH, this);
			}
		}

		clearAvailableCursors();
		for (Period period : milestones) {
			if (lastDisplayedPeriod == null) {
				layout.putConstraint(SpringLayout.NORTH, period, 5,
						SpringLayout.NORTH, this);
			} else {
				layout.putConstraint(SpringLayout.NORTH, period, 5,
						SpringLayout.SOUTH, lastDisplayedPeriod);
			}
			JPanel cursor = getAvailableCursor();
			layout.putConstraint(SpringLayout.WIDTH, cursor, 1,
					SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.NORTH, cursor, 0,
					SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.SOUTH, cursor, 0,
					SpringLayout.SOUTH, period);
			String side;
			if (period.isStartMilestone()) {
				side = SpringLayout.WEST;
			} else {
				side = SpringLayout.EAST;
			}
			layout.putConstraint(side, cursor, 0, side, period);
		}

		if (lastDisplayedPeriod != null) {
			layout.putConstraint(SpringLayout.SOUTH, this, 0,
					SpringLayout.SOUTH, lastDisplayedPeriod);
		}
		setLayout(layout);
	}

	public void addPeriod(Period period) {
		periods.add(period);
		add(period);
		periodUpdated = true;
	}

	public void clear() {
		periods.clear();
		removeAll();
		periodUpdated = true;
	}

	public Collection<Period> getPeriods() {
		return periods;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public void sortByStartDate() {
		Collections.sort(periods, new Comparator<Period>() {
			@Override
			public int compare(Period p0, Period p1) {
				Date d1 = p0.getStart();
				Date d2 = p1.getStart();
				if (d1 != null && d2 != null) {
					return d1.compareTo(d2);
				} else if (d1 != null) {
					return 1;
				} else if (d2 != null) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		periodUpdated = true;
		repaint();
	}

	public void sortByStopDate() {
		Collections.sort(periods, new Comparator<Period>() {
			@Override
			public int compare(Period p0, Period p1) {
				Date d1 = p0.getStop();
				Date d2 = p1.getStop();
				if (d1 != null && d2 != null) {
					return d1.compareTo(d2);
				} else if (d1 != null) {
					return 1;
				} else if (d2 != null) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		periodUpdated = true;
		repaint();
	}

	public boolean isBounded() {
		return minDate != null && maxDate != null;
	}

	public boolean hasDisplayablePeriods() {
		for (Period period : periods) {
			if (period.getStart() != null && period.getStop() != null) {
				return true;
			}
		}
		return false;
	}

	private final Map<JPanel, Boolean> availableCursors = new HashMap<JPanel, Boolean>();

	private JPanel getAvailableCursor() {
		for (Map.Entry<JPanel, Boolean> entry : availableCursors.entrySet()) {
			if (entry.getValue()) {
				entry.setValue(false);
				JPanel cursor = entry.getKey();
				add(cursor);
				return cursor;
			}
		}

		JPanel newCursor = new JPanel();
		newCursor.setBackground(Color.BLACK);
		add(newCursor);
		availableCursors.put(newCursor, false);
		return newCursor;
	}

	private void clearAvailableCursors() {
		for (Map.Entry<JPanel, Boolean> entry : availableCursors.entrySet()) {
			entry.setValue(true);
			remove(entry.getKey());
		}
	}

	public void resetMinMax() {
		Date min = getActualMinDate();
		Date max = getActualMaxDate();
		if (min == null) {
			Calendar calendar = new GregorianCalendar();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			min = calendar.getTime();
		} else if (max == null) {
			Calendar calendar = new GregorianCalendar();
			calendar.add(Calendar.DAY_OF_MONTH, +1);
			max = calendar.getTime();
		}
		setMinDate(min);
		setMaxDate(max);
	}
}

package fr.vergne.taskmanager.gui.gantt;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
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
		Date min = new Date(Long.MAX_VALUE);
		for (Period period : periods) {
			Date start = period.getStart();
			if (start.before(min)) {
				min = start;
			}
		}
		return min;
	}

	public Date getActualMaxDate() {
		Date max = new Date(0);
		for (Period period : periods) {
			Date stop = period.getStop();
			if (stop != null && stop.after(max)) {
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
			computePeriods();
			oldMinDate = minDate;
			oldMaxDate = maxDate;
			periodUpdated = false;
		}

		super.paint(graphics);
	}

	private void computePeriods() {
		final long min = minDate.getTime();
		final long max = maxDate.getTime();
		final long delta = max - min;
		Period lastPeriod = null;
		SpringLayout layout = new SpringLayout();
		for (Period period : periods) {
			if (period.isBoundedPeriod()) {
				long start = period.getStart().getTime();
				int pixelStart = (int) ((start - min) * getWidth() / delta);
				layout.putConstraint(SpringLayout.WEST, period, pixelStart,
						SpringLayout.WEST, this);

				long stop = period.getStop().getTime();
				int pixelStop = (int) ((max - stop) * getWidth() / delta);
				layout.putConstraint(SpringLayout.EAST, period, -pixelStop,
						SpringLayout.EAST, this);

				if (lastPeriod == null) {
					layout.putConstraint(SpringLayout.NORTH, period, 5,
							SpringLayout.NORTH, this);
				} else {
					layout.putConstraint(SpringLayout.NORTH, period, 5,
							SpringLayout.SOUTH, lastPeriod);
				}
				lastPeriod = period;
			} else {
				layout.putConstraint(SpringLayout.SOUTH, period, 0,
						SpringLayout.NORTH, this);
			}
		}
		layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH,
				lastPeriod);
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
}

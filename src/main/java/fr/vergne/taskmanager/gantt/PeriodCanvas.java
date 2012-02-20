package fr.vergne.taskmanager.gantt;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class PeriodCanvas extends JPanel {

	private final SpringLayout layout = new SpringLayout();
	private final Collection<Period> periods = new LinkedList<Period>();
	private Date minDate = new Date();
	private Date maxDate = new Date(minDate.getTime() + 3600000);

	public PeriodCanvas() {
		setBackground(Color.WHITE);
		setLayout(layout);
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent arg0) {
				updateDisplay();
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				updateDisplay();
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// nothing to do
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// nothing to do
			}
		});
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
			if (stop.after(max)) {
				max = stop;
			}
		}
		return max;
	}

	public void updateDisplay() {
		long min = minDate.getTime();
		long max = maxDate.getTime();
		long delta = max - min;

		Period lastPeriod = null;
		for (Period period : periods) {
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
		}
		revalidate();
	}

	public void addPeriod(Period period) {
		periods.add(period);
		add(period);
	}

	public void clear() {
		periods.clear();
		removeAll();
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
}

package fr.vergne.taskmanager.gantt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class TimeBar extends JPanel {
	private Date minDate = new Date();
	private Date maxDate = new Date();
	private final List<UnitDescriptor> descriptors = new LinkedList<UnitDescriptor>();
	private UnitDescriptor forcedDescriptor = null;
	private UnitDescriptor currentDescriptor = null;

	public TimeBar() {
		setBorder(new LineBorder(Color.BLACK));
		setPreferredSize(new Dimension(0, 20));

		addHierarchyBoundsListener(new HierarchyBoundsListener() {

			@Override
			public void ancestorResized(HierarchyEvent arg0) {
				updateDisplay();
			}

			@Override
			public void ancestorMoved(HierarchyEvent arg0) {
				// do nothing
			}
		});
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

	public void updateDisplay() {
		final int totalPixels = getWidth();
		final long min = minDate.getTime();
		final long max = maxDate.getTime();
		final long delta = max - min;
		int calendarUnit = Calendar.MILLISECOND;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(minDate);
		int unitStep = 1;
		long unitMultiplicator = 1;
		long numberOfComponents = 0;
		long maxComponents = 0;
		Iterator<UnitDescriptor> iterator = descriptors.iterator();
		do {
			currentDescriptor = iterator.next();

			unitMultiplicator = currentDescriptor.getUnitMultiplicator();
			unitStep = currentDescriptor.getUnitStep();
			if (calendarUnit != currentDescriptor.getCalendarUnit()) {
				calendar.set(calendarUnit, calendar.getActualMinimum(calendarUnit));
				calendarUnit = currentDescriptor.getCalendarUnit();
			} else {
				int flooredValue = (int) (unitStep * Math
						.floor((double) calendar.get(calendarUnit) / unitStep));
				calendar.set(calendarUnit, flooredValue);
			}

			// +1 to consider the last partial component
			numberOfComponents = (long) Math.ceil((double) delta
					/ unitMultiplicator / unitStep) + 1;

			String string = currentDescriptor.getDateFormat().replaceAll("'",
					"");
			maxComponents = totalPixels / string.length() / 8;
			
			if (currentDescriptor == forcedDescriptor) {
				break;
			}
		} while (isDescriptorForced() || iterator.hasNext() && numberOfComponents > maxComponents);

		SpringLayout lowLayout = new SpringLayout();
		setLayout(lowLayout);
		removeAll();
		long ref = calendar.getTime().getTime();
		TimeSlot previousSlot = null;
		int x = 0;
		SimpleDateFormat format = new SimpleDateFormat(
				currentDescriptor.getDateFormat());
		while (x < getWidth()) {
			TimeSlot slot = new TimeSlot(format.format(calendar.getTime()));
			add(slot);
			x = (int) ((ref - getMinDate().getTime()) * getWidth() / (getMaxDate()
					.getTime() - getMinDate().getTime()));
			lowLayout.putConstraint(SpringLayout.WEST, slot, x,
					SpringLayout.WEST, this);

			if (previousSlot == null) {
				// do nothing
			} else {
				lowLayout.putConstraint(SpringLayout.EAST, previousSlot, 0,
						SpringLayout.WEST, slot);
			}

			ref += currentDescriptor.getUnitMultiplicator()
					* currentDescriptor.getUnitStep();
			calendar.add(currentDescriptor.getCalendarUnit(),
					currentDescriptor.getUnitStep());
			previousSlot = slot;
		}

		revalidate();
	}

	public UnitDescriptor getForcedDescriptor() {
		return forcedDescriptor;
	}

	public void setForcedDescriptor(UnitDescriptor descriptor) {
		this.forcedDescriptor = descriptor;
	}

	public boolean isDescriptorForced() {
		return forcedDescriptor != null;
	}

	public void addDescriptor(UnitDescriptor descriptor) {
		descriptors.add(descriptor);
		Collections.sort(descriptors, new Comparator<UnitDescriptor>() {

			@Override
			public int compare(UnitDescriptor d1, UnitDescriptor d2) {
				long v1 = d1.getUnitMultiplicator() * d1.getUnitStep();
				long v2 = d2.getUnitMultiplicator() * d2.getUnitStep();
				return Long.valueOf(v1).compareTo(v2);
			}
		});
	}

	public Collection<UnitDescriptor> getDescriptors() {
		return new LinkedList<UnitDescriptor>(descriptors);
	}

	public UnitDescriptor getCurrentDescriptor() {
		return currentDescriptor;
	}

	static class TimeSlot extends JPanel {

		public TimeSlot(String text) {
			setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.LINE_START;
			add(new JLabel("|"), constraints);
			constraints.weightx = 1;
			add(new JLabel(text), constraints);
		}
	}

	public static class UnitDescriptor {
		private final long unitMultiplicator;
		private final int unitStep;
		private final int calendarUnit;
		private final String dateFormat;

		public UnitDescriptor(long unitMultiplicator, int unitStep,
				int calendarUnit, String dateFormat) {
			this.unitMultiplicator = unitMultiplicator;
			this.unitStep = unitStep;
			this.calendarUnit = calendarUnit;
			this.dateFormat = new String(dateFormat);
		}

		public long getUnitMultiplicator() {
			return unitMultiplicator;
		}

		public int getCalendarUnit() {
			return calendarUnit;
		}

		public String getDateFormat() {
			return dateFormat;
		}

		public int getUnitStep() {
			return unitStep;
		}
	}

}

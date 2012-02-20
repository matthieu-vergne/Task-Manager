package fr.vergne.taskmanager.history;

import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class History<T> implements Iterable<History.HistoryEntry<T>> {

	private final Deque<HistoryEntry<T>> history = new LinkedList<HistoryEntry<T>>();

	public void push(T value, Date date) {
		history.add(new HistoryEntry<T>(date, value));
	}

	public void push(T value) {
		push(value, new Date());
	}

	public T getCurrentValue() {
		return history.getLast().getValue();
	}

	public List<T> getHistorizedValues() {
		List<T> list = new LinkedList<T>();
		for (HistoryEntry<T> entry : history) {
			list.add(entry.getValue());
		}
		return list;
	}

	public List<Date> getHistorizedDates() {
		List<Date> list = new LinkedList<Date>();
		for (HistoryEntry<T> entry : history) {
			list.add(entry.getDate());
		}
		return list;
	}

	public Date getLastUpdateDate() {
		return history.getLast().getDate();
	}

	public void forgetLastUpdate() {
		history.removeLast();
	}

	@Override
	public Iterator<HistoryEntry<T>> iterator() {
		return history.iterator();
	}

	public Iterator<HistoryEntry<T>> reversedIterator() {
		return history.descendingIterator();
	}

	public Iterator<T> valuesIterator() {
		return new ValueIterator(iterator());
	}

	public Iterator<T> reversedValuesIterator() {
		return new ValueIterator(reversedIterator());
	}

	public Iterator<Date> datesIterator() {
		return new DateIterator(iterator());
	}

	public Iterator<Date> reversedDatesIterator() {
		return new DateIterator(reversedIterator());
	}

	public void clear() {
		history.clear();
	}

	public void clearBefore(Date limit) {
		Iterator<Date> iterator = datesIterator();
		while (iterator.hasNext()) {
			Date date = iterator.next();
			if (date.before(limit)) {
				iterator.remove();
			} else {
				break;
			}
		}
	}

	public void clearAfter(Date limit) {
		Iterator<Date> iterator = reversedDatesIterator();
		while (iterator.hasNext()) {
			Date date = iterator.next();
			if (date.after(limit)) {
				iterator.remove();
			} else {
				break;
			}
		}
	}

	public boolean isEmpty() {
		return history.isEmpty();
	}

	public int size() {
		return history.size();
	}

	public static class HistoryEntry<T> {

		private final T value;
		private final Date date;

		public HistoryEntry(Date date, T value) {
			this.date = date;
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public Date getDate() {
			return date;
		}

	}

	public class ValueIterator implements Iterator<T> {
		private final Iterator<HistoryEntry<T>> iterator;

		public ValueIterator(Iterator<HistoryEntry<T>> historyIterator) {
			this.iterator = historyIterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() {
			return iterator.next().getValue();
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}

	public class DateIterator implements Iterator<Date> {
		private final Iterator<HistoryEntry<T>> iterator;

		public DateIterator(Iterator<HistoryEntry<T>> historyIterator) {
			this.iterator = historyIterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Date next() {
			return iterator.next().getDate();
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}
}

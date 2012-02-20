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

	public Iterator<T> valuesIterator() {
		return new Iterator<T>() {
			private final Iterator<HistoryEntry<T>> iterator = iterator();

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
		};
	}

	public Iterator<Date> datesIterator() {
		return new Iterator<Date>() {
			private final Iterator<HistoryEntry<T>> iterator = iterator();

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
		};
	}

	public void clear() {
		history.clear();
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
}

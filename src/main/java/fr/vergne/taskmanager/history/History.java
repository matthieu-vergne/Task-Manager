package fr.vergne.taskmanager.history;

import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import fr.vergne.taskmanager.export.Exportable;

public abstract class History<T> implements Iterable<History.HistoryEntry<T>>,
		Exportable {

	private final Deque<HistoryEntry<T>> history = new LinkedList<HistoryEntry<T>>();
	private final Map<HistoryEntry<T>, String> comments = new HashMap<HistoryEntry<T>, String>();

	protected void push(T value, String comment, Date date) {
		if (history.isEmpty() || getYoungestValue() != null
				&& !getYoungestValue().equals(value)
				|| getYoungestValue() == null && value != null) {
			history.addLast(new HistoryEntry<T>(date, value));
		} else {
			// do not insert double
		}

		HistoryEntry<T> entry = history.getLast();
		setComment(entry, comment);
	}

	public void setComment(HistoryEntry<T> entry, String comment) {
		if (comment != null && !comment.isEmpty()) {
			comments.put(entry, comment);
		} else {
			comments.remove(entry);
		}
	}

	public String getComment(HistoryEntry<T> entry) {
		return comments.get(entry);
	}

	protected void push(T value, Date date) {
		push(value, null, date);
	}

	public void push(T value, String comment) {
		push(value, comment, new Date());
	}

	public void push(T value) {
		push(value, (String) null);
	}

	public T getYoungestValue() {
		return history.getLast().getValue();
	}

	public T getOldestValue() {
		return history.getFirst().getValue();
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

	public Date getYoungestDate() {
		return history.getLast().getDate();
	}

	public Date getOldestDate() {
		return history.getFirst().getDate();
	}

	public void forgetLastValue() {
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
		if (!history.isEmpty()) {
			T lastValue = getYoungestValue();
			Iterator<Date> iterator = datesIterator();
			while (iterator.hasNext()) {
				Date date = iterator.next();
				if (date.before(limit)) {
					iterator.remove();
				} else {
					break;
				}
			}
			if (history.isEmpty()) {
				push(lastValue, limit);
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

	@Override
	public void write(TransformerHandler handler) throws SAXException {
		{
			AttributesImpl attributes = new AttributesImpl();
			handler.startElement("", "", "history", attributes);
		}
		for (HistoryEntry<T> data : this) {
			{
				AttributesImpl attributes = new AttributesImpl();
				attributes.addAttribute("", "", "date", "CDATA", ""
						+ data.getDate().getTime());
				String comment = comments.get(data);
				if (comment != null) {
					attributes
							.addAttribute("", "", "comment", "CDATA", comment);
				}
				handler.startElement("", "", "value", attributes);
			}
			T value = data.getValue();
			if (value != null) {
				String string = dataToString(value);
				handler.characters(string.toCharArray(), 0, string.length());
			}
			handler.endElement("", "", "value");
		}
		handler.endElement("", "", "history");
	}

	@Override
	public void read(Node node) throws SAXException {
		clear();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			NamedNodeMap attributes = child.getAttributes();

			String time = attributes.getNamedItem("date").getNodeValue();
			Date date = new Date(Long.parseLong(time));

			String comment = null;
			Node commentNode = attributes.getNamedItem("comment");
			if (commentNode != null) {
				comment = commentNode.getNodeValue();
			}

			String string = child.getTextContent();
			T value = string.isEmpty() ? null : stringToData(string);

			push(value, comment, date);
		}
	}

	public abstract String dataToString(T data);

	public abstract T stringToData(String string);
}

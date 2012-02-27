package fr.vergne.taskmanager.history;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

import fr.vergne.taskmanager.history.History.HistoryEntry;

public class HistoryTest {
	class TestHistory extends History<Integer> {

		@Override
		public String dataToString(Integer data) {
			return data.toString();
		}

		@Override
		public Integer stringToData(String string) {
			return Integer.parseInt(string);
		}

	}

	@Test
	public void testHistoryEntry() {
		HistoryEntry<Integer> entry = new History.HistoryEntry<Integer>(
				new Date(50), 3);
		assertEquals(3, (int) entry.getValue());
		assertEquals(50, entry.getDate().getTime());
	}

	@Test
	public void testSimplePush() {
		History<Integer> history = new TestHistory();

		history.push(1);
		assertEquals(1, (int) history.getYoungestValue());
		history.push(0);
		assertEquals(0, (int) history.getYoungestValue());
		history.push(-5);
		assertEquals(-5, (int) history.getYoungestValue());

		assertEquals(3, history.size());
		assertEquals(1, (int) history.getHistorizedValues().get(0));
		assertEquals(0, (int) history.getHistorizedValues().get(1));
		assertEquals(-5, (int) history.getHistorizedValues().get(2));
	}

	@Test
	public void testDatedPush() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		assertEquals(1, (int) history.getYoungestValue());
		assertEquals(0, history.getYoungestDate().getTime());
		history.push(0, new Date(5));
		assertEquals(0, (int) history.getYoungestValue());
		assertEquals(5, history.getYoungestDate().getTime());
		history.push(-5, new Date(10));
		assertEquals(-5, (int) history.getYoungestValue());
		assertEquals(10, history.getYoungestDate().getTime());

		assertEquals(3, history.size());
		assertEquals(1, (int) history.getHistorizedValues().get(0));
		assertEquals(0, (int) history.getHistorizedValues().get(1));
		assertEquals(-5, (int) history.getHistorizedValues().get(2));
		assertEquals(0, history.getHistorizedDates().get(0).getTime());
		assertEquals(5, history.getHistorizedDates().get(1).getTime());
		assertEquals(10, history.getHistorizedDates().get(2).getTime());
	}

	@Test
	public void testYoungestValue() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		assertEquals(1, (int) history.getYoungestValue());
		history.push(0, new Date(5));
		assertEquals(0, (int) history.getYoungestValue());
		history.push(-5, new Date(10));
		assertEquals(-5, (int) history.getYoungestValue());
		history.forgetLastValue();
		assertEquals(0, (int) history.getYoungestValue());
		history.push(4);
		assertEquals(4, (int) history.getYoungestValue());
	}

	@Test
	public void testOldestValue() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		assertEquals(1, (int) history.getOldestValue());
		history.push(0, new Date(5));
		assertEquals(1, (int) history.getOldestValue());
		history.push(-5, new Date(10));
		assertEquals(1, (int) history.getOldestValue());
		history.clearBefore(new Date(5));
		assertEquals(0, (int) history.getOldestValue());
	}

	@Test
	public void testYoungestDate() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		assertEquals(0, history.getYoungestDate().getTime());
		history.push(0, new Date(5));
		assertEquals(5, history.getYoungestDate().getTime());
		history.push(-5, new Date(10));
		assertEquals(10, history.getYoungestDate().getTime());
		history.forgetLastValue();
		assertEquals(5, history.getYoungestDate().getTime());
		history.push(4, new Date(15));
		assertEquals(15, history.getYoungestDate().getTime());
	}

	@Test
	public void testOldestDate() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		assertEquals(0, history.getOldestDate().getTime());
		history.push(0, new Date(5));
		assertEquals(0, history.getOldestDate().getTime());
		history.push(-5, new Date(10));
		assertEquals(0, history.getOldestDate().getTime());
		history.clearBefore(new Date(5));
		assertEquals(5, history.getOldestDate().getTime());
	}

	@Test
	public void testHistorizedValues() {
		History<Integer> history = new TestHistory();

		history.push(1);
		history.push(0);
		history.push(-5);

		assertEquals(3, history.size());
		assertEquals(1, (int) history.getHistorizedValues().get(0));
		assertEquals(0, (int) history.getHistorizedValues().get(1));
		assertEquals(-5, (int) history.getHistorizedValues().get(2));
	}

	@Test
	public void testHistorizedDates() {
		History<Integer> history = new TestHistory();

		history.push(0, new Date(0));
		history.push(1, new Date(5));
		history.push(2, new Date(10));

		assertEquals(3, history.size());
		assertEquals(0, history.getHistorizedDates().get(0).getTime());
		assertEquals(5, history.getHistorizedDates().get(1).getTime());
		assertEquals(10, history.getHistorizedDates().get(2).getTime());
	}

	@Test
	public void testForget() {
		History<Integer> history = new TestHistory();

		history.push(1);
		history.push(0);
		history.push(-5);

		assertEquals(3, history.size());
		assertEquals(-5, (int) history.getYoungestValue());
		history.forgetLastValue();
		assertEquals(2, history.size());
		assertEquals(0, (int) history.getYoungestValue());
		history.forgetLastValue();
		assertEquals(1, history.size());
		assertEquals(1, (int) history.getYoungestValue());
		history.forgetLastValue();
		assertEquals(0, history.size());
	}

	@Test
	public void testHistoryIterator() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		history.push(0, new Date(5));
		history.push(-5, new Date(10));

		Iterator<HistoryEntry<Integer>> iterator = history.iterator();
		assertTrue(iterator.hasNext());
		{
			HistoryEntry<Integer> entry = iterator.next();
			assertEquals(1, (int) entry.getValue());
			assertEquals(0, entry.getDate().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			HistoryEntry<Integer> entry = iterator.next();
			assertEquals(0, (int) entry.getValue());
			assertEquals(5, entry.getDate().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			HistoryEntry<Integer> entry = iterator.next();
			assertEquals(-5, (int) entry.getValue());
			assertEquals(10, entry.getDate().getTime());
			assertFalse(iterator.hasNext());
		}
	}

	@Test
	public void testReversedHistoryIterator() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		history.push(0, new Date(5));
		history.push(-5, new Date(10));

		Iterator<HistoryEntry<Integer>> iterator = history.reversedIterator();
		assertTrue(iterator.hasNext());
		{
			HistoryEntry<Integer> entry = iterator.next();
			assertEquals(-5, (int) entry.getValue());
			assertEquals(10, entry.getDate().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			HistoryEntry<Integer> entry = iterator.next();
			assertEquals(0, (int) entry.getValue());
			assertEquals(5, entry.getDate().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			HistoryEntry<Integer> entry = iterator.next();
			assertEquals(1, (int) entry.getValue());
			assertEquals(0, entry.getDate().getTime());
			assertFalse(iterator.hasNext());
		}
	}

	@Test
	public void testValuesIterator() {
		History<Integer> history = new TestHistory();

		history.push(1);
		history.push(0);
		history.push(-5);

		Iterator<Integer> iterator = history.valuesIterator();
		assertTrue(iterator.hasNext());
		{
			assertEquals(1, (int) iterator.next());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(0, (int) iterator.next());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(-5, (int) iterator.next());
			assertFalse(iterator.hasNext());
		}
	}

	@Test
	public void testReversedValuesIterator() {
		History<Integer> history = new TestHistory();

		history.push(1);
		history.push(0);
		history.push(-5);

		Iterator<Integer> iterator = history.reversedValuesIterator();
		assertTrue(iterator.hasNext());
		{
			assertEquals(-5, (int) iterator.next());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(0, (int) iterator.next());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(1, (int) iterator.next());
			assertFalse(iterator.hasNext());
		}
	}

	@Test
	public void testDatesIterator() {
		History<Integer> history = new TestHistory();

		history.push(0, new Date(0));
		history.push(1, new Date(5));
		history.push(2, new Date(10));

		Iterator<Date> iterator = history.datesIterator();
		assertTrue(iterator.hasNext());
		{
			assertEquals(0, iterator.next().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(5, iterator.next().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(10, iterator.next().getTime());
			assertFalse(iterator.hasNext());
		}
	}

	@Test
	public void testReversedDatesIterator() {
		History<Integer> history = new TestHistory();

		history.push(0, new Date(0));
		history.push(1, new Date(5));
		history.push(2, new Date(10));

		Iterator<Date> iterator = history.reversedDatesIterator();
		assertTrue(iterator.hasNext());
		{
			assertEquals(10, iterator.next().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(5, iterator.next().getTime());
			assertTrue(iterator.hasNext());
		}
		{
			assertEquals(0, iterator.next().getTime());
			assertFalse(iterator.hasNext());
		}
	}

	@Test
	public void testClear() {
		History<Integer> history = new TestHistory();

		history.push(1);
		history.push(0);
		history.push(-5);

		assertEquals(3, history.size());
		history.clear();
		assertEquals(0, history.size());
	}

	@Test
	public void testClearBefore() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		history.push(0, new Date(5));
		history.push(-5, new Date(10));

		history.clearBefore(new Date(5));
		assertEquals(2, history.size());
		assertEquals(0, (int) history.getHistorizedValues().get(0));
		assertEquals(5, history.getHistorizedDates().get(0).getTime());
		assertEquals(-5, (int) history.getHistorizedValues().get(1));
		assertEquals(10, history.getHistorizedDates().get(1).getTime());
	}

	@Test
	public void testClearBeforeAll() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		history.push(0, new Date(5));
		history.push(-5, new Date(10));

		history.clearBefore(new Date(15));
		assertEquals(1, history.size());
		assertEquals(-5, (int) history.getHistorizedValues().get(0));
		assertEquals(15, history.getHistorizedDates().get(0).getTime());
	}

	@Test
	public void testClearAfter() {
		History<Integer> history = new TestHistory();

		history.push(1, new Date(0));
		history.push(0, new Date(5));
		history.push(-5, new Date(10));

		history.clearAfter(new Date(5));
		assertEquals(2, history.size());
		assertEquals(1, (int) history.getHistorizedValues().get(0));
		assertEquals(0, history.getHistorizedDates().get(0).getTime());
		assertEquals(0, (int) history.getHistorizedValues().get(1));
		assertEquals(5, history.getHistorizedDates().get(1).getTime());
	}

	@Test
	public void testIsEmpty() {
		History<Integer> history = new TestHistory();

		assertTrue(history.isEmpty());
		history.push(1);
		assertFalse(history.isEmpty());
		history.push(0);
		assertFalse(history.isEmpty());
		history.push(-5);
		assertFalse(history.isEmpty());

		history.forgetLastValue();
		assertFalse(history.isEmpty());
		history.forgetLastValue();
		assertFalse(history.isEmpty());
		history.forgetLastValue();
		assertTrue(history.isEmpty());

		history.push(3);
		assertFalse(history.isEmpty());
		history.push(2);
		assertFalse(history.isEmpty());
		history.push(349876);
		assertFalse(history.isEmpty());

		history.clear();
		assertTrue(history.isEmpty());
	}

	@Test
	public void testSize() {
		History<Integer> history = new TestHistory();

		assertEquals(0, history.size());
		history.push(1);
		assertEquals(1, history.size());
		history.push(0);
		assertEquals(2, history.size());
		history.push(-5);
		assertEquals(3, history.size());

		history.forgetLastValue();
		assertEquals(2, history.size());
		history.forgetLastValue();
		assertEquals(1, history.size());
		history.forgetLastValue();
		assertEquals(0, history.size());

		history.push(3);
		assertEquals(1, history.size());
		history.push(2);
		assertEquals(2, history.size());
		history.push(349876);
		assertEquals(3, history.size());

		history.clear();
		assertEquals(0, history.size());
	}

}

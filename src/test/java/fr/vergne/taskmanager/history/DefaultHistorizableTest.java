package fr.vergne.taskmanager.history;

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultHistorizableTest extends HistorizableTest<Integer> {

	@Override
	protected Historizable<Integer> instantiateHistorizable() {
		return new DefaultHistorizable<Integer>();
	}

	@Test
	public void testSetGet() {
		DefaultHistorizable<Integer> historizable = new DefaultHistorizable<Integer>();
		historizable.set(5);
		assertEquals(5, (int) historizable.get());
		historizable.set(3);
		assertEquals(3, (int) historizable.get());
		historizable.set(-345);
		assertEquals(-345, (int) historizable.get());
	}

	@Test
	public void testHistoryFilling() {
		DefaultHistorizable<Integer> historizable = new DefaultHistorizable<Integer>();
		historizable.set(5);
		historizable.set(3);
		historizable.set(-345);

		assertEquals(3, historizable.getHistory().size());
		assertEquals(5, (int) historizable.getHistory().getHistorizedValues()
				.get(0));
		assertEquals(3, (int) historizable.getHistory().getHistorizedValues()
				.get(1));
		assertEquals(-345, (int) historizable.getHistory()
				.getHistorizedValues().get(2));
	}

	@Test
	public void testNullManagement() {
		DefaultHistorizable<Integer> historizable = new DefaultHistorizable<Integer>(
				false);
		historizable.set(5);
		assertEquals(5, (int) historizable.get());
		historizable.set(null);
		assertEquals(null, historizable.get());
		historizable.set(-345);
		assertEquals(-345, (int) historizable.get());

		historizable = new DefaultHistorizable<Integer>(true);
		historizable.set(5);
		assertEquals(5, (int) historizable.get());
		try {
			historizable.set(null);
			fail("No exception thrown");
		} catch (NullPointerException ex) {
		}
		assertEquals(5, (int) historizable.get());
		historizable.set(-345);
		assertEquals(-345, (int) historizable.get());
	}
}

package fr.vergne.taskmanager.history;

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultHistorizableTest extends HistorizableTest<Integer> {

	class TestHistorizable extends AbstractHistorizable<Integer> {
		public TestHistorizable(Integer initialValue, boolean forbidNull) {
			super(initialValue, forbidNull);
		}

		public TestHistorizable(Integer initialValue) {
			super(initialValue);
		}

		public TestHistorizable(boolean forbidNull) {
			super(forbidNull);
		}

		public TestHistorizable() {
			super();
		}

		@Override
		public String formatDataInString(Integer data) {
			return data.toString();
		}

		@Override
		public Integer formatStringInData(String string) {
			return Integer.parseInt(string);
		}
	}

	@Override
	protected Historizable<Integer> instantiateHistorizable() {
		return new TestHistorizable();
	}

	@Test
	public void testSetGet() {
		AbstractHistorizable<Integer> historizable = new TestHistorizable();
		historizable.set(5);
		assertEquals(5, (int) historizable.get());
		historizable.set(3);
		assertEquals(3, (int) historizable.get());
		historizable.set(-345);
		assertEquals(-345, (int) historizable.get());
	}

	@Test
	public void testHistoryFilling() {
		AbstractHistorizable<Integer> historizable = new TestHistorizable();
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
		AbstractHistorizable<Integer> historizable = new TestHistorizable(false);
		historizable.set(5);
		assertEquals(5, (int) historizable.get());
		historizable.set(null);
		assertEquals(null, historizable.get());
		historizable.set(-345);
		assertEquals(-345, (int) historizable.get());

		historizable = new TestHistorizable(true);
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

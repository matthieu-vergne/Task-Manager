package fr.vergne.taskmanager.history;

import static org.junit.Assert.*;

import org.junit.Test;

public abstract class HistorizableTest<T> {
	
	protected abstract Historizable<T> instantiateHistorizable();

	@Test
	public void testNotNullHistorizable() {
		Historizable<T> historizable = instantiateHistorizable();
		assertNotNull(historizable);
	}

	@Test
	public void testNotNullHistory() {
		Historizable<T> historizable = instantiateHistorizable();
		assertNotNull(historizable.getHistory());
	}

}

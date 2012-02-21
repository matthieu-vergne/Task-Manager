package fr.vergne.taskmanager.history;

public class DefaultHistorizable<T> implements Historizable<T> {

	private final boolean isNullForbiden;

	public DefaultHistorizable(T initialValue, boolean forbidNull) {
		this(forbidNull);
		set(initialValue);
	}

	public DefaultHistorizable(T initialValue) {
		this(initialValue, false);
	}

	public DefaultHistorizable(boolean forbidNull) {
		isNullForbiden = forbidNull;
	}

	public DefaultHistorizable() {
		this(false);
	}

	public T get() {
		return history.getYoungestValue();
	}

	public void set(T value) {
		if (isNullForbiden && value == null) {
			throw new NullPointerException("Null values are forbidden.");
		} else {
			history.push(value);
		}
	}

	private final History<T> history = new History<T>();

	@Override
	public History<T> getHistory() {
		return history;
	}

	@Override
	public String toString() {
		return history.getYoungestValue().toString();
	}

	public boolean isNullForbiden() {
		return isNullForbiden;
	}
}

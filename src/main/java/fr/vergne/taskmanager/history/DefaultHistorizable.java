package fr.vergne.taskmanager.history;

public class DefaultHistorizable<T> implements Historizable<T> {

	private final boolean isNullForbiden;

	public DefaultHistorizable(boolean forbidNull) {
		isNullForbiden = forbidNull;
	}

	public DefaultHistorizable() {
		this(false);
	}

	public T get() {
		return history.getCurrentValue();
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
		return history.getCurrentValue().toString();
	}

	public boolean isNullForbiden() {
		return isNullForbiden;
	}
}

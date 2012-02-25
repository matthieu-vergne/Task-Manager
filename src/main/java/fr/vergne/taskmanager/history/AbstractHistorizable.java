package fr.vergne.taskmanager.history;

public abstract class AbstractHistorizable<T> implements Historizable<T> {

	private final boolean isNullForbiden;

	public AbstractHistorizable(T initialValue, boolean forbidNull) {
		this(forbidNull);
		set(initialValue);
	}

	public AbstractHistorizable(T initialValue) {
		this(initialValue, false);
	}

	public AbstractHistorizable(boolean forbidNull) {
		isNullForbiden = forbidNull;
	}

	public AbstractHistorizable() {
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

	private final History<T> history = new History<T>() {
		@Override
		public String dataToString(T data) {
			return formatDataInString(data);
		};

		@Override
		public T stringToData(String string) {
			return formatStringInData(string);
		}
	};

	public abstract String formatDataInString(T data);

	public abstract T formatStringInData(String string);

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

package fr.vergne.taskmanager.history;

public abstract class AbstractHistorizable<T> implements Historizable<T> {
	
	public T get() {
		return history.getCurrentValue();
	}
	
	public void set(T value) {
		history.push(value);
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
}

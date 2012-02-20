package fr.vergne.taskmanager.history;

public interface Historizable<T> {
	public History<T> getHistory();
}

package fr.vergne.taskmanager.history;

public class HistorizableString extends AbstractHistorizable<String> {

	public HistorizableString(String initialValue, boolean forbidNull) {
		super(initialValue, forbidNull);
	}

	public HistorizableString(boolean forbidNull) {
		super(forbidNull);
	}

	public HistorizableString(String initialValue) {
		super(initialValue);
	}

	public HistorizableString() {
		super();
	}

	@Override
	public String formatDataInString(String data) {
		return data;
	}

	@Override
	public String formatStringInData(String string) {
		return string;
	}

}

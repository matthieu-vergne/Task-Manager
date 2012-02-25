package fr.vergne.taskmanager.history;

import java.util.Date;

public class HistorizableDate extends AbstractHistorizable<Date> {
	public HistorizableDate(Date initialValue, boolean forbidNull) {
		super(initialValue, forbidNull);
	}

	public HistorizableDate(boolean forbidNull) {
		super(forbidNull);
	}

	public HistorizableDate(Date initialValue) {
		super(initialValue);
	}

	public HistorizableDate() {
		super();
	}

	@Override
	public String formatDataInString(Date data) {
		return "" + data.getTime();
	}

	@Override
	public Date formatStringInData(String string) {
		return new Date(Long.parseLong(string));
	}

}

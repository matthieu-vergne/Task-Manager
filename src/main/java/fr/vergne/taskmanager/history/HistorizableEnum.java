package fr.vergne.taskmanager.history;

public abstract class HistorizableEnum<T extends Enum<?>> extends
		AbstractHistorizable<T> {
	public HistorizableEnum(T initialValue, boolean forbidNull) {
		super(initialValue, forbidNull);
	}

	public HistorizableEnum(boolean forbidNull) {
		super(forbidNull);
	}

	public HistorizableEnum(T initialValue) {
		super(initialValue);
	}

	public HistorizableEnum() {
		super();
	}

	@Override
	public String formatDataInString(T data) {
		return data.toString();
	}

	@Override
	public T formatStringInData(String string) {
		for (T constant : getEnumClass().getEnumConstants()) {
			if (constant.name().equals(string)) {
				return constant;
			}
		}
		throw new IllegalArgumentException(string
				+ " does not correspond to a value of "
				+ getEnumClass().getName());
	}

	public abstract Class<T> getEnumClass();
}

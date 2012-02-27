package fr.vergne.taskmanager.gui.gantt;

import java.awt.Component;

import javax.swing.Spring;

public class SpringWidth extends Spring {
	
	private final Component component;
	
	public SpringWidth(Component c) {
		component = c;
	}

	@Override
	public int getMaximumValue() {
		return getValue();
	}

	@Override
	public int getMinimumValue() {
		return getValue();
	}

	@Override
	public int getPreferredValue() {
		return getValue();
	}

	@Override
	public int getValue() {
		return component.getWidth();
	}

	@Override
	public void setValue(int arg0) {
		// do nothing
	}

}

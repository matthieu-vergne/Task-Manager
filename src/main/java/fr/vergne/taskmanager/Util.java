package fr.vergne.taskmanager;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JDialog;

public class Util {

	private Util() {
		// forbidden constructor
		throw new RuntimeException("You cannot instantiate this class.");
	}

	public static void centerDialog(JDialog dialog) {
		centerDialog(dialog, new Point(), Toolkit.getDefaultToolkit()
				.getScreenSize());
	}

	public static void centerDialog(JDialog dialog, Container container) {
		Dimension size = container.getSize();
		Point location = container.getLocation();
		while (container.getParent() != null) {
			container = container.getParent();
			location.x += container.getX() + container.getInsets().left;
			location.y += container.getY() + container.getInsets().top;
		}
		centerDialog(dialog, location, size);
	}

	public static void centerDialog(JDialog dialog, Point location,
			Dimension area) {
		Dimension dialogSize = dialog.getSize();
		int x = location.x + (area.width - dialogSize.width) / 2;
		int y = location.y + (area.height - dialogSize.height) / 2;
		dialog.setLocation(x, y);
	}

}

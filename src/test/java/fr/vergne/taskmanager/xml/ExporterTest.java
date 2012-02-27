package fr.vergne.taskmanager.xml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.util.Date;

import org.junit.Test;

import fr.vergne.taskmanager.task.Task;
import fr.vergne.taskmanager.task.TaskList;
import fr.vergne.taskmanager.task.TaskStatus;

public class ExporterTest {

	@Test
	public void testWriteRead() {
		TaskList list = new TaskList();

		{
			Task task = new Task("task 1");
			list.add(task);
		}

		{
			Task task = new Task("task 2");
			task.setDescription("Description task 2.");
			task.setDeadline(new Date(60000));
			task.setStatus(TaskStatus.RUNNING);
			list.add(task);
		}

		File file = new File("test.xml");
		file.deleteOnExit();
		Exporter.write(list, file);

		TaskList list2 = new TaskList();
		Exporter.read(list2, file);

		assertEquals(list.size(), list2.size());
		for (int i = 0; i < list.size(); i++) {
			Task task1 = list.get(i);
			Task task2 = list2.get(i);
			assertEquals(task1.getTitle(), task2.getTitle());
			assertEquals(task1.getStart(), task2.getStart());
			assertEquals(task1.getDeadline(), task2.getDeadline());
			assertEquals(task1.getDescription(), task2.getDescription());
			assertEquals(task1.getCreationDate(), task2.getCreationDate());
			assertEquals(task1.getStatus(), task2.getStatus());
		}

		File file2 = new File("test2.xml");
		file2.deleteOnExit();
		Exporter.write(list2, file2);

		String string1 = getFileContent(file);
		assertNotNull(string1);
		assertFalse(string1.isEmpty());

		String string2 = getFileContent(file2);
		assertEquals(string1, string2);

		file.delete();
		file2.delete();
	}

	private String getFileContent(File file) {
		try {
			StringBuffer buffer = new StringBuffer();
			FileReader reader = new FileReader(file);
			while (reader.ready()) {
				buffer.append((char) reader.read());
			}
			reader.close();
			return buffer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

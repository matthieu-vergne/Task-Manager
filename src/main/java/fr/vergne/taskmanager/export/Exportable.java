package fr.vergne.taskmanager.export;

import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public interface Exportable {
	public void write(TransformerHandler handler) throws SAXException;

	public void read(Node node) throws SAXException;
}

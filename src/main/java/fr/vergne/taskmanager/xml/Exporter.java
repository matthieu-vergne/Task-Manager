package fr.vergne.taskmanager.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.vergne.taskmanager.export.Exportable;

public class Exporter {

	private Exporter() {
		// private constructor because utilitary class
	}

	public static void write(Exportable exportable, File file) {
		try {
			PrintWriter out = new PrintWriter(file);
			try {
				TransformerHandler handler = ((SAXTransformerFactory) SAXTransformerFactory
						.newInstance()).newTransformerHandler();
				{
					Transformer serializer = handler.getTransformer();
					serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
					// serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				}
				handler.setResult(new StreamResult(out));
				handler.startDocument();
				exportable.write(handler);
				handler.endDocument();
			} catch (Exception e) {
				out.close();
				throw new RuntimeException(e);
			}

			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void read(Exportable exportable, File file) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			Node node = doc.getFirstChild();
			cleanIndent(node);
			exportable.read(node);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void cleanIndent(Node node) {
		NodeList children = node.getChildNodes();

		if (children.getLength() > 1) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					node.removeChild(child);
					i--;
				} else {
					cleanIndent(child);
				}
			}
		} else if (children.getLength() == 1) {
			cleanIndent(node.getFirstChild());
		} else {
			// no child to process
		}
	}
}

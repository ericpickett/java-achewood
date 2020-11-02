package archiver;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class PlistProcessor {
	private static final String SAVEPATH = "src/main/resources/plist/comics.plist";
	
	public Set<Comic> comics = new HashSet<Comic>();
	
	public void saveDocument() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			throw new RuntimeException();
		}
		DOMImplementation implementation = builder.getDOMImplementation();
		DocumentType type = implementation.createDocumentType("plist", "-//Apple//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd");
		Document document = implementation.createDocument("", "plist", type);
		document.setXmlStandalone(true);
		
		Element rootElement = document.getDocumentElement();
		rootElement.setAttribute("version", "1.0");
		
		Element rootArrayElement = document.createElement("array");
		rootElement.appendChild(rootArrayElement);
		
		for(Comic comic : comics) {
			Element comicDictElement = document.createElement("dict");
			rootArrayElement.appendChild(comicDictElement);
			
			Element dateKeyElement = document.createElement("key");
			dateKeyElement.setTextContent("date");
			comicDictElement.appendChild(dateKeyElement);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = format.format(comic.getDate());
			
			Element rootDictKeyElement = document.createElement("string");
			rootDictKeyElement.setTextContent(dateString);
			comicDictElement.appendChild(rootDictKeyElement);
			
			Element titleKeyElement = document.createElement("key");
			titleKeyElement.setTextContent("title");
			comicDictElement.appendChild(titleKeyElement);
			Element titleValueElement = document.createElement("string");
			titleValueElement.setTextContent(comic.getTitle());
			comicDictElement.appendChild(titleValueElement);
			
			Element captionKeyElement = document.createElement("key");
			captionKeyElement.setTextContent("caption");
			comicDictElement.appendChild(captionKeyElement);
			Element captionValueElement = document.createElement("string");
			captionValueElement.setTextContent(comic.getCaption());
			comicDictElement.appendChild(captionValueElement);
			
			Element datelineKeyElement = document.createElement("key");
			datelineKeyElement.setTextContent("dateline");
			comicDictElement.appendChild(datelineKeyElement);
			Element datelineValueElement = document.createElement("string");
			datelineValueElement.setTextContent(comic.getDateline());
			comicDictElement.appendChild(datelineValueElement);
		}
		
		DOMSource domSource = new DOMSource(document);
		DocumentType documentType = document.getDoctype();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException();
		}
		
		
		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);
		try {
			transformer.transform(domSource, streamResult);
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException();
		}
		String xmlString = stringWriter.toString();
		try {
			OutputStream outputStream = new FileOutputStream(SAVEPATH);
			Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
			writer.write(xmlString);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	private static PlistProcessor instancePlistProcessor = null;
	
	private PlistProcessor() { }
	
	public static PlistProcessor getInstance() {
		if(instancePlistProcessor == null) {
			instancePlistProcessor = new PlistProcessor();
		}
		
		return instancePlistProcessor;
	}
}

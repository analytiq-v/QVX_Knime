package edu.njit.qvx;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class PartialXmlEventReader implements XMLEventReader {

	private final XMLEventReader reader;
	private boolean skip = false;
	
	public PartialXmlEventReader(final XMLEventReader reader) {
	    this.reader = reader;
	}
	
	@Override
	public String getElementText() throws XMLStreamException {
	    return reader.getElementText();
	}
	
	@Override
	public Object getProperty(final String name) throws IllegalArgumentException {
	    return reader.getProperty(name);
	}
	
	@Override
	public boolean hasNext() {
	    return reader.hasNext();
	}
	
	@Override
	public XMLEvent nextEvent() throws XMLStreamException {
	    return reader.nextEvent();
	}
	
	@Override
	public XMLEvent nextTag() throws XMLStreamException {
	    return reader.nextTag();
	}
	
	@Override
	public XMLEvent peek() throws XMLStreamException {
	    return reader.peek();
	}
	
	@Override
	public Object next() {
	    return reader.next();
	}
	
	@Override
	public void remove() {
	    reader.remove();
	}
	
	@Override
	public void close() throws XMLStreamException {
	    reader.close();
	}
	


}
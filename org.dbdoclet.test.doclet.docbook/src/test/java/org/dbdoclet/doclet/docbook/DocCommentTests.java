package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DocCommentTests extends AbstractTestCase {

	@Test
	public void docCommentText_1() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("Lorem ipsum <b>dolor</b> sit amet.");
		
		String value = xpath("//db:sect2/db:para");
		assertEquals("Lorem ipsum dolor sit amet.", value);
	}

	@Test
	public void docCommentText_2() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("Lorem ipsum <h3>dolor</h3><p> sit amet.</p>");
		String value = xpath("//db:sect2/db:para");
		assertEquals("Lorem ipsum", value);
	}

	@Test
	public void docCommentText_3() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("Lorem ipsum <a href=\"http://www.dbdoclet.org\">dbdoclet</a> sit amet.");
		String value = xpath("//db:sect2/db:para");
		assertEquals("Lorem ipsum dbdoclet sit amet.", value);
	}

	@Test
	public void docCommentText_4() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("<p xml:id='lorem'>Lorem ipsum dolor sit amet.</p>");
		String value = xpath("//db:sect2/db:para");
		assertEquals("Lorem ipsum dolor sit amet.", value);
	}

	@Test
	public void docCommentText_5() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("<b>Inline</b>");
		String value = xpath("//db:sect2/db:para");
		assertEquals("Inline", value);
	}

	@Test
	public void docCommentText_6() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("This is some <code>code</code>!");
		String value = xpath("//db:sect2/db:para/db:literal");
		printDocBookFile();
		assertEquals("code", value);
	}
	
	@Test
	public void docCommentText_7() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("<code>code</code>");
		String value = xpath("//db:sect2/db:para/db:literal");
		// printDocBookFile();
		assertEquals("code", value);
	}

	@Test
	public void docCommentText_8() throws IOException, SAXException,
			ParserConfigurationException {

		docComment("<img src='doc-files/picture.png'>");
		String value = xpath("//db:sect2//db:imageobject[@role='html']/db:imagedata/@fileref");
		// printDocBookFile();
		assertEquals("img/doc-files/picture.png", value);
	}

}

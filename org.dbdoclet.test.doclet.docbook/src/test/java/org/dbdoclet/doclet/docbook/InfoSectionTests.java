package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.dbdoclet.Sfv;
import org.dbdoclet.xiphias.XPathServices;
import org.dbdoclet.xiphias.XmlServices;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.tools.javadoc.Main;

public class InfoSectionTests extends AbstractTestCase {

	private static final String PROFILE_MAXIMAL = "src/main/resources/profile/showAll.her";
	private static final String PROFILE_MINIMAL = "src/main/resources/profile/showMinimal.her";
	
	@Test
	public void abstractDefined() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);

		String value = xpath("//db:abstract");
		assertTrue(value.length() > 0);
	}

	@Test
	public void abstractUndefined() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:abstract");
		assertEquals(0, value.length());
	}

	@Test
	public void authorEmailDefined() {

		javadoc("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:author/db:affiliation/db:address/db:email");
		assertNotNull(value);
		assertEquals("michael.fuchs@dbdoclet.org", value);
	}

	@Test
	public void authorEmailUndefined() {

		javadoc("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info//db:author/db:affiliation/db:address/db:email");
		assertNull(value);
	}

	@Test
	public void authorFirstnameDefined() {

		javadoc("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:author/db:personname/db:firstname");
		assertNotNull(value);
		assertEquals("Michael", value);
	}

	@Test
	public void authorFirstnameUndefined() {

		javadoc("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info//db:author/db:personname/db:firstname");
		assertNull(value);
	}

	@Test
	public void authorSurnameDefined() {

		javadoc("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:author/db:personname/db:surname");
		assertNotNull(value);
		assertEquals("Fuchs", value);
	}

	@Test
	public void authorSurnameUndefined() {

		javadoc("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info//db:author/db:personname/db:surname");
		assertNull(value);
	}

	@Test
	public void corporationDefined() {

		javadoc("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:legalnotice/db:simpara");
		assertTrue(value.indexOf("Michael Fuchs") != -1);
	}

	@Test
	public void corporationUndefined() {

		javadoc("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info/db:legalnotice/db:simpara");
		assertNull(value);
	}

	@Test
	public void testDocBookTitle() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", "src/main/resources/profile/title.her");
		Document doc = XmlServices.parse(new File(destPath, "Reference.xml"));
		Node node = XPathServices.getNode(doc, "d", Sfv.NS_DOCBOOK,
				"/d:book/d:title");
		assertNotNull("//d:title", node);
		assertEquals("Musikeditor", node.getTextContent());
	}
}

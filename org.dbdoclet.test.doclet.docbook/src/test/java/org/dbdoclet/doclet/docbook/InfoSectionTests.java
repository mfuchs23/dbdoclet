package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.dbdoclet.Sfv;
import org.dbdoclet.xiphias.XPathServices;
import org.dbdoclet.xiphias.XmlServices;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class InfoSectionTests extends AbstractTestCase {

	private static final String PROFILE_MAXIMAL = "showAll.her";
	private static final String PROFILE_MINIMAL = "showMinimal.her";
	
	@Test
	public void abstractDefined() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);

		String value = xpath("//db:abstract");
		assertNotNull(value);
		assertTrue(value.length() > 0);
	}

	@Test
	public void abstractUndefined() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:abstract");
		assertNull(value);
	}

	@Test
	public void authorEmailDefined() {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:author/db:affiliation/db:address/db:email");
		assertNotNull(value);
		assertEquals("michael.fuchs@dbdoclet.org", value);
	}

	@Test
	public void authorEmailUndefined() {

		javadocTestPackage("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info//db:author/db:affiliation/db:address/db:email");
		assertNull(value);
	}

	@Test
	public void authorFirstnameDefined() {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:author/db:personname/db:firstname");
		assertNotNull(value);
		assertEquals("Michael", value);
	}

	@Test
	public void authorFirstnameUndefined() {

		javadocTestPackage("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info//db:author/db:personname/db:firstname");
		assertNull(value);
	}

	@Test
	public void authorSurnameDefined() {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:author/db:personname/db:surname");
		assertNotNull(value);
		assertEquals("Fuchs", value);
	}

	@Test
	public void authorSurnameUndefined() {

		javadocTestPackage("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info//db:author/db:personname/db:surname");
		assertNull(value);
	}

	@Test
	public void corporationDefined() {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:legalnotice/db:simpara");
		assertNotNull(value);
		assertTrue(value.indexOf("Michael Fuchs") != -1);
	}

	@Test
	public void corporationUndefined() {

		javadocTestPackage("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info/db:legalnotice/db:simpara");
		assertNull(value);
	}

	@Test
	public void testDocBookTitle() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", "title.her");
		Document doc = XmlServices.parse(new File(destPath, "Reference.xml"));
		Node node = XPathServices.getNode(doc, "d", Sfv.NS_DOCBOOK,
				"/d:book/d:title");
		assertNotNull("//d:title", node);
		assertEquals("Musikeditor", node.getTextContent());
	}
}

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

public class StandardOptionsTests extends AbstractTestCase {

	private void javadoc(String... options) {

		String[] mandatoryOptions = { "-d", destPath, "-sourcepath",
				sourcePath, "org.dbdoclet.music" };

		ArrayList<String> optionList = new ArrayList<String>();

		for (String option : options) {
			optionList.add(option);
		}

		for (String option : mandatoryOptions) {
			optionList.add(option);
		}

		String[] cmd = optionList.toArray(new String[optionList.size()]);
		Main.execute("Test", "org.dbdoclet.doclet.docbook.DocBookDoclet", cmd);
	}

	@Test
	public void parameterAbstract() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", "src/main/resources/profile/showAll.her");

		String value = xpath("//db:abstract");
		assertTrue(value.length() > 0);
	}

	@Test
	public void testCorporation() {

		javadoc("-corporation", "Ingenieurbüro Michael Fuchs");

		String value = xpath("/db:book/db:info/db:legalnotice/db:simpara");
		assertTrue(value.indexOf("Michael Fuchs") != -1);
	}

	@Test
	public void testDeprecatedList() {

		javadoc("--deprecated-list");

		String value = xpath("/db:book/db:chapter[db:title='Deprecated API']/db:title");
		assertTrue(value.indexOf("Deprecated") != -1);
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

	@Test
	public void testIgnoreAnnotationDocumented() {

		javadoc("--ignore-annotation-documented");

		String value = xpath("//db:chapter[@xml:id='package-org.dbdoclet.music']"
				+ "/db:sect1[@xml:id='class-org.dbdoclet.music.Motiv']"
				+ "/db:sect2[@xml:id='method-org.dbdoclet.music.Motiv.getNotes']"
				+ "/db:methodsynopsis" + "/db:type");

		assertEquals("Note[]", value);
	}

	@Test
	public void testLocale() {

		javadoc("-locale", "de_DE");

		String value = xpath("/db:book/@xml:lang");
		assertEquals("de", value);
	}

	@Test
	public void testLogoPath() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", "src/main/resources/profile/logoPath.her");
		Document doc = XmlServices.parse(new File(destPath, "Reference.xml"));
		Node node = XPathServices.getNode(doc, "d", Sfv.NS_DOCBOOK,
				"/d:book/d:title");
		assertNotNull("//d:title", node);
		assertEquals("Musikeditor", node.getTextContent());
	}

	@Test
	public void testNofqsn() {

		javadoc("-nofqsn");

		String value = xpath("//db:chapter[@xml:id='package-org.dbdoclet.music']"
				+ "/db:sect1[@xml:id='class-org.dbdoclet.music.Motiv']"
				+ "/db:sect2[@xml:id='method-org.dbdoclet.music.Motiv.getNotes']"
				+ "/db:methodsynopsis" + "/db:type");

		assertEquals("Note[]", value);
	}

	@Test
	public void testShowAll() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", "src/main/resources/profile/showAll.her");

		String value = xpath("//db:varlistentry[db:term='Author']/db:listitem");
		assertEquals("Michael Fuchs", value);
		value = xpath("//db:chapter/db:title[.='Constant field values']");
		assertNotNull(value);
		value = xpath("/db:book");
		assertNotNull(value);
	}

	@Test
	public void testShowMinimal() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", "src/main/resources/profile/showMinimal.her");
		String value = xpath("//db:varlistentry[db:term='Author']/db:listitem");
		assertNull(value);
	}
}

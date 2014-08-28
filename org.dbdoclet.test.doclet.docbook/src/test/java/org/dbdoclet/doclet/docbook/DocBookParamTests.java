package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DocBookParamTests extends AbstractTestCase {

	private static final String PROFILE_MAXIMAL = "showAll.her";
	private static final String PROFILE_MINIMAL = "showMinimal.her";
	
	@Test
	public void addIndexEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:index");
		assertNotNull(value);
	}

	@Test
	public void addIndexDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);

		String value = xpath("//db:index");
		assertNull(value);
	}
}

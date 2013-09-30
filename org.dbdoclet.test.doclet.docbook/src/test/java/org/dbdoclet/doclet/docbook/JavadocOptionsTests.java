package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class JavadocOptionsTests extends AbstractTestCase {

	@Test
	public void optionOverview() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-overview", "src/main/resources/overview/overview.html");
		String value = xpath("//db:chapter/db:title[contains(text(), 'Overview')]");
		assertNotNull(value);
	}

}

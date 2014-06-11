package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class JavadocOptionsTests extends AbstractTestCase {

	@Test
	public void optionD() {
		
		DocumentationTool documentationTool = ToolProvider.getSystemDocumentationTool()	;
		
		if (documentationTool == null) {
			fail("Es konnte keine Implementierung von javadoc gefunden werden.");
		}
		
		assertEquals(0, documentationTool.run(null, null, null, "-d", destPath,
				 "-sourcepath", sourcePath, "-subpackages", "org.dbdoclet.music"));
	}
	
	@Test
	public void optionOverview() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-overview", "src/main/resources/overview/overview.html");
		String value = xpath("//db:chapter/db:title[contains(text(), 'Overview')]");
		assertNotNull(value);
	}

}

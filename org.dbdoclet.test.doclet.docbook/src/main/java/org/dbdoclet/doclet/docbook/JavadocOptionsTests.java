package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.sun.tools.javadoc.Main;

public class JavadocOptionsTests extends AbstractTestCase {

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
	public void optionOverview() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-overview", "src/main/resources/overview/overview.html");

		String value = xpath("//db:chapter/db:title[contains(text(), 'Overview')]");
		assertTrue(value.length() > 0);
	}

}

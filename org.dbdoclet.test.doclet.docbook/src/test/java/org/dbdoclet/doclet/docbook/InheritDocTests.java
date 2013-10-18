package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class InheritDocTests extends AbstractTestCase {

	private static final String PROFILE_DIR = "src/main/resources/profile/";
	private static final String PROFILE_MAXIMAL = PROFILE_DIR + "showAll.her";

	@Test
	public void inheritDoc() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		File moduleFile = new File(destPath, "org-dbdoclet-music-Motiv.xml");
		assertFalse(String.format("Die Moduldatei %s darf nicht erzeugt werden!",
				moduleFile.getAbsolutePath()), moduleFile.exists());
	}
}

package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class SpecificParamTests extends AbstractTestCase {

	private static final String PROFILE_DIR = "src/main/resources/profile/";
	private static final String PROFILE_SPECIFIC = PROFILE_DIR + "specific.her";

	@Test
	public void classSpecific() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_SPECIFIC);
		
		File imgFile = new File(destPath + "/img/org/dbdoclet/music/Motiv/ClassDiagram.svg");
		assertFalse(String.format("Die Bilddatei %s darf nicht erzeugt werden!",
				imgFile.getAbsolutePath()), imgFile.exists());
		
		imgFile = new File(destPath + "/img/org/dbdoclet/music/Note/ClassDiagram.svg");
		assertFalse(String.format("Die Bilddatei %s darf nicht erzeugt werden!",
				imgFile.getAbsolutePath()), imgFile.exists());
	}
}

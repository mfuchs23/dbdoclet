package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DocletParamTests extends AbstractTestCase {

	private static final String PROFILE_MAXIMAL = "src/main/resources/profile/showAll.her";
	private static final String PROFILE_MINIMAL = "src/main/resources/profile/showMinimal.her";

	@Test
	public void chunkDocBookEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		File moduleFile = new File(destPath, "org-dbdoclet-music-Motiv.xml");
		assertTrue(String.format("Die Moduldatei %s wurde nicht gefunden!",
				moduleFile.getAbsolutePath()), moduleFile.exists());
	}

	@Test
	public void chunkDocBookDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		File moduleFile = new File(destPath, "org-dbdoclet-music-Motiv.xml");
		assertFalse(String.format("Die Moduldatei %s darf nicht erzeugt werden!",
				moduleFile.getAbsolutePath()), moduleFile.exists());
	}

	@Test
	public void classDiagramFontSizeSetTo6() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		File imgFile = new File(destPath, "figures/org/dbdoclet/musik/Motiv/ClassDiagram.svg");
	}

}

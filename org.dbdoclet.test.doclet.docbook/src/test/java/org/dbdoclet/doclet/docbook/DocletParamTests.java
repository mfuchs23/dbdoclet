package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.dbdoclet.service.FileServices;
import org.junit.Test;
import org.xml.sax.SAXException;

public class DocletParamTests extends AbstractTestCase {

	private static final String PROFILE_CHUNK = "src/main/resources/profile/chunk.her";
	private static final String PROFILE_MAXIMAL = "src/main/resources/profile/showAll.her";
	private static final String PROFILE_MINIMAL = "src/main/resources/profile/showMinimal.her";

	@Test
	public void chunkDocBookDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		File moduleFile = new File(destPath, "org-dbdoclet-music-Motiv.xml");
		assertFalse(String.format("Die Moduldatei %s darf nicht erzeugt werden!",
				moduleFile.getAbsolutePath()), moduleFile.exists());
	}

	@Test
	public void chunkDocBookEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_CHUNK);
		File moduleFile = new File(destPath, "org-dbdoclet-music-Motiv.xml");
		assertTrue(String.format("Die Moduldatei %s wurde nicht gefunden!",
				moduleFile.getAbsolutePath()), moduleFile.exists());
	}

	@Test
	public void classDiagramFontSizeSetTo10() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		File imgFile = new File(destPath, "img/org/dbdoclet/music/Motiv/ClassDiagram.svg");
		String buffer = FileServices.readToString(imgFile);
		assertTrue(buffer.contains("font-size:10"));
	}

	@Test
	public void classDiagramWidthSetTo800() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		File imgFile = new File(destPath, "img/org/dbdoclet/music/Note/ClassDiagram.svg");
		String buffer = FileServices.readToString(imgFile);
		assertTrue(buffer.contains("width=\"809\""));
	}

	@Test
	public void createAppendixDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:appendix");
		assertNull(value);
	}

	@Test
	public void createAppendixEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:appendix");
		assertNotNull(value);
	}

	@Test
	public void createAuthorInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:emphasis[text()='Autor']");
		assertNull(value);
	}

	@Test
	public void createAuthorInfoEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:emphasis[text()='Autor']");
		assertNotNull(value);
	}

	@Test
	public void createClassDiagramDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:imagedata[@fileref='img/org/dbdoclet/music/Motiv/ClassDiagram.svg']");
		assertNull(value);
	}

	@Test
	public void createClassDiagramEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:imagedata[@fileref='img/org/dbdoclet/music/Motiv/ClassDiagram.svg']");
		assertNotNull(value);
	}

	@Test
	public void createDeprecatedInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:warning/db:title[text()='Veraltet (Deprecated)']");
		assertNull(value);
	}

	@Test
	public void createDeprecatedInfoEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:warning/db:title[text()='Veraltet (Deprecated)']");
		assertNotNull(value);
	}

	@Test
	public void createDeprecatedListDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:warning/db:title[text()='Veraltet (Deprecated)']");
		assertNull(value);
	}

	@Test
	public void createDeprecatedListEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:warning/db:title[text()='Veraltet (Deprecated)']");
		assertNotNull(value);
	}

	@Test
	public void createExceptionInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:variablelist/db:title[text()='Exceptions']");
		assertNull(value);
	}

	@Test
	public void createExeptionInfoEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadoc("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:variablelist/db:title[text()='Exceptions']");
		assertNotNull(value);
	}

}

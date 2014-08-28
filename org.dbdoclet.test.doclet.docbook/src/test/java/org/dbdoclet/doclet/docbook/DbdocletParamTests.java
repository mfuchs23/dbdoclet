package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.dbdoclet.service.FileServices;
import org.dbdoclet.xiphias.Hyphenation;
import org.junit.Test;
import org.xml.sax.SAXException;

public class DbdocletParamTests extends AbstractTestCase {

	private static final String PROFILE_CHUNK = "chunk.her";
	private static final String PROFILE_CLASSDIAGRAM_WIDTH_ZERO = "classDiagramWidthZero.her";
	private static final String PROFILE_CLASSDIAGRAM_HEIGHT_50 = "classDiagramHeight50.her";
	private static final String PROFILE_MAXIMAL = "showAll.her";
	private static final String PROFILE_MINIMAL = "showMinimal.her";
	private static final String PROFILE_CREATE_META_INFO_DISABLED = "createMetaInfoDisabled.her";

	@Test
	public void chunkDocBookDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		File moduleFile = new File(destPath, "org-dbdoclet-music-Motiv.xml");
		assertFalse(String.format("Die Moduldatei %s darf nicht erzeugt werden!",
				moduleFile.getAbsolutePath()), moduleFile.exists());
	}

	@Test
	public void chunkDocBookEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_CHUNK);
		File moduleFile = new File(destPath, "org-dbdoclet-music-Motiv.xml");
		assertTrue(String.format("Die Moduldatei %s wurde nicht gefunden!",
				moduleFile.getAbsolutePath()), moduleFile.exists());
	}

	@Test
	public void classDiagramFontSizeSetTo10() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		File imgFile = new File(destPath, "img/org/dbdoclet/music/Motiv/ClassDiagram.svg");
		String buffer = FileServices.readToString(imgFile);
		assertTrue(buffer.contains("font-size:10"));
	}

	@Test
	public void classDiagramWidthSetTo800() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		File imgFile = new File(destPath, "img/org/dbdoclet/music/Note/ClassDiagram.svg");
		String buffer = FileServices.readToString(imgFile);
		assertTrue(buffer.contains("width=\"800\""));
	}

	@Test
	public void classDiagramHeightSetTo50() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_CLASSDIAGRAM_HEIGHT_50);
		File imgFile = new File(destPath, "img/org/dbdoclet/music/Note/ClassDiagram.svg");
		String buffer = FileServices.readToString(imgFile);
		assertTrue(buffer.contains("height=\"50\""));
	}

	@Test
	public void classDiagramWidthSetTo0() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_CLASSDIAGRAM_WIDTH_ZERO);
		File imgFile = new File(destPath, "img/org/dbdoclet/music/Note/ClassDiagram.svg");
		String buffer = FileServices.readToString(imgFile);
		assertTrue(buffer.contains("width=\"885\""));
	}

	@Test
	public void createAppendixDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:appendix");
		assertNull(value);
	}

	@Test
	public void createAppendixEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:appendix");
		assertNotNull(value);
	}

	@Test
	public void createAuthorInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:emphasis[text()='Autor']");
		assertNull(value);
	}

	@Test
	public void createAuthorInfoEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:emphasis[text()='Autor']");
		assertNotNull(value);
	}

	@Test
	public void createClassDiagramDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:imagedata[@fileref='img/org/dbdoclet/music/Motiv/ClassDiagram.svg']");
		assertNull(value);
	}

	@Test
	public void createClassDiagramEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:imagedata[@fileref='img/org/dbdoclet/music/Motiv/ClassDiagram.svg']");
		assertNotNull(value);
	}

	@Test
	public void createDeprecatedInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:warning/db:title[text()='Veraltet (Deprecated)']");
		assertNull(value);
	}

	@Test
	public void createDeprecatedInfoEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:warning/db:title[text()='Veraltet (Deprecated)']");
		assertNotNull(value);
	}

	@Test
	public void createDeprecatedListDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:appendix/db:title[text()='Veraltete APIs']");
		assertNull(value);
	}

	@Test
	public void createDeprecatedListEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:appendix/db:title[text()='Veraltete APIs']");
		assertNotNull(value);
	}

	@Test
	public void createExceptionInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:variablelist/db:title[text()='Exceptions']");
		assertNull(value);
	}

	@Test
	public void createExeptionInfoEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:variablelist/db:title[text()='Exceptions']");
		assertNotNull(value);
	}

	@Test
	public void createFullyQualifiedNamesDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:classsynopsis/db:ooclass[1]/db:classname[text()='Note']");
		assertNotNull("Note", value);
	}

	@Test
	public void createFullyQualifiedNamesEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String classname = "org.dbdoclet.music.Note";
		Hyphenation hyphenation = new Hyphenation();
		classname = hyphenation.hyphenateAfter(classname, "\\.");
		classname = classname.replace("&#x00ad;", "\u00ad");
		String value = xpath("//db:classsynopsis/db:ooclass/db:classname[text()='" + classname + "']");
		assertNotNull("org.dbdoclet.music.Note", value);
	}

	@Test
	public void createInheritedFromInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MINIMAL);
		String value = xpath("//db:para/db:emphasis[contains(text(),'Methoden geerbt von')]");
		assertNull(value);
	}

	@Test
	public void createInheritedFromEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:para/db:emphasis[contains(text(),'Methoden geerbt von')]");
		assertNotNull(value);
	}

	@Test
	public void createMetaInfoDisabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_CREATE_META_INFO_DISABLED);
		String value = xpath("//db:varlistentry/db:term/db:emphasis[text()='Autor']");
		assertNull(value);
		value = xpath("//db:varlistentry/db:term/db:emphasis[text()='Siehe auch']");
		assertNull(value);
	}

	@Test
	public void createMetaInfoEnabled() throws IOException, SAXException,
			ParserConfigurationException {

		javadocTestPackage("-profile", PROFILE_MAXIMAL);
		String value = xpath("//db:varlistentry/db:term/db:emphasis[text()='Autor']");
		assertNotNull(value);
	}
}

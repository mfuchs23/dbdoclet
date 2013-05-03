package org.dbdoclet.doclet.docbook;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import org.dbdoclet.doclet.XmlIdType;
import org.dbdoclet.tag.docbook.DocBookVersion;
import org.dbdoclet.trafo.internal.html.docbook.DbtConstants;
import org.dbdoclet.trafo.script.Script;
import org.dbdoclet.xiphias.XmlServices.HyphenationChar;

public class DbdScript {

	public static final File DEFAULT_DESTINATION_FILE = new File(
			"./dbdoclet/Reference.xml");

	@Inject
	private Script script;
	private File outputFile;

	public String getAbstract() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_ABSTRACT, "");
	}

	public String getAuthorEmail() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_AUTHOR_EMAIL, "");
	}

	public String getAuthorFirstname() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_AUTHOR_FIRSTNAME, "");
	}

	public String getAuthorSurname() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_AUTHOR_SURNAME, "");
	}

	/*
	 * Bestimmt die Größe der Schriftart für die Klassendiagramme in Pixel. Der
	 * minimale Wert ist 4. Als Standardwert ist 12 vorgegeben.
	 */
	public int getClassDiagramFontSize() {
		int fontSize = script.getIntParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CLASS_DIAGRAM_FONT_SIZE, 12);

		if (fontSize < 4) {
			fontSize = 4;
		}

		return fontSize;
	}

	/**
	 * Maximum width of a class diagram.
	 * 
	 * @return width
	 */
	public int getClassDiagramWidth() {
		return script.getIntParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CLASS_DIAGRAM_WIDTH, 700);
	}

	public String getCopyrightHolder() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_COPYRIGHT_HOLDER, "");
	}

	public String getCopyrightYear() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_COPYRIGHT_YEAR, "");
	}

	public String getCorporation() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_CORPORATION, "");
	}

	public File getDestinationDirectory() {

		if (outputFile == null) {
			outputFile = DEFAULT_DESTINATION_FILE;
		}

		return outputFile.getParentFile();
	}

	public String getDestinationEncoding() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_ENCODING, "UTF-8");
	}

	public File getDestinationFile() {

		if (outputFile == null) {
			outputFile = DEFAULT_DESTINATION_FILE;
		}

		return outputFile;
	}

	public DocBookVersion getDocBookVersion() {

		String version = script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_DOCBOOK_VERSION, "5.0");

		if (version != null && version.trim().equals("4.5")) {
			return DocBookVersion.V4_5;
		}

		return DocBookVersion.V5_0;
	}

	public String getDocumentationId() {
		String documentationId = script.getTextParameter(
				DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_DOCUMENTATION_ID, "dbdoclet");
		return documentationId;
	}

	public String getDocumentElement() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_DOCUMENT_ELEMENT, "book");
	}

	public HyphenationChar getHyphenationChar() {

		String hyphenationChar = script.getTextParameter(
				DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_HYPHENATION_CHAR, "soft-hyphen");

		if (hyphenationChar != null && hyphenationChar.equals("soft-hyphen")) {
			return HyphenationChar.SOFT_HYPHEN;
		} else {
			return HyphenationChar.ZERO_WIDTH_SPACE;
		}
	}

	public String getHyphenationCharAsText() {

		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_HYPHENATION_CHAR, "soft-hyphen");
	}

	public XmlIdType getIdStyle() {

		String idStyle = script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_ID_STYLE, "numbered");

		if (idStyle != null && idStyle.equalsIgnoreCase("java")) {
			return XmlIdType.JAVA;
		} else {
			return XmlIdType.NUMBERED;
		}
	}

	public ArrayList<String> getImageDataFormats() {

		ArrayList<String> defaultFormatList = new ArrayList<String>();
		defaultFormatList.add("PNG");

		return script
				.getTextParameterList(DbtConstants.SECTION_DOCBOOK,
						DbtConstants.PARAM_IMAGEDATA_FORMATS,
						defaultFormatList);
	}

	public String getImagePath() {

		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_IMAGE_PATH, "./figures");
	}

	public String getLanguage() {

		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_LANGUAGE, Locale.getDefault()
						.getLanguage());
	}

	public String getLogoPath() {
		return script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_LOGO_PATH, "");
	}

	public String getOverviewTitle() {
		return script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_OVERVIEW_TITLE, "");
	}

	public String getReleaseInfo() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_RELEASE_INFO, "");
	}

	public Script getScript() {
		return script;
	}

	public String getTableStyle() {
		return script.getTextParameter(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_TABLE_STYLE,
				DbtConstants.DEFAULT_TABLE_STYLE);
	}

	public ArrayList<String> getTagList() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		String titleText = script.getTextParameter(
				DbtConstants.SECTION_DOCBOOK, DbtConstants.PARAM_TITLE,
				"JavaDoc Reference");
		return titleText;
	}

	public boolean hasProlog() {
		return script.isParameterOn(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_CREATE_PROLOG, true);
	}

	public boolean isAbsoluteImagePathEnabled() {
		return script.isParameterOn(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_USE_ABSOLUTE_IMAGE_PATH, false);
	}

	public boolean isChunkDocBookEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CHUNK_DOCBOOK_ENABLED, false);
	}

	public boolean isCreateAppendixEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_APPENDIX_ENABLED, true);
	}

	public boolean isCreateAuthorInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_AUTHOR_INFO, true);
	}

	/**
	 * Der Parameter <code>create-class-diagram</code> bestimmt, ob für jede
	 * Klasse ein Klassendiagramm erstellt werden soll.
	 */
	public boolean isCreateClassDiagramEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_CLASS_DIAGRAM, true);
	}

	/**
	 * Der Parameter <code>create-constant-value</code> bestimmt, ob eine
	 * Sektion eingefügt werden soll, die alle gefundenen Konstanten zusammen
	 * mit ihren Werten auflistet.
	 */
	public boolean isCreateConstantValuesEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_SECTION_CONSTANT_VALUES,
				true);
	}

	public boolean isCreateDeprecatedInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_DEPRECATED_INFO, true);
	}

	public boolean isCreateDeprecatedListEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_DEPRECATED_LIST, true);
	}

	public boolean isCreateExceptionInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_EXCEPTION_INFO, true);
	}

	public boolean isCreateFieldInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_FIELD_INFO, true);
	}

	public boolean isCreateFullyQualifiedNamesEnabled() {
		return script
				.isParameterOn(
						DbdConstants.SECTION_DBDOCLET,
						DbdConstants.PARAM_DBDOCLET_CREATE_FULLY_QUALIFIED_NAMES,
						false);
	}

	public boolean isCreateIndexEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_INDEX, true);
	}

	public boolean isCreateInheritanceInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_INHERITANCE_INFO, true);
	}

	public boolean isCreateInheritedFromInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_INHERITED_FROM_INFO, true);
	}

	public boolean isCreateMetaInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_META_INFO, true);
	}

	public boolean isCreateMethodInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_METHOD_INFO, true);
	}

	public boolean isCreateParameterInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_PARAMETER_INFO, true);
	}

	public boolean isCreateSeeAlsoInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_SEE_ALSO_INFO, true);
	}

	public boolean isCreateSerialFieldInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_SERIAL_FIELD_INFO, true);
	}

	public boolean isCreateSinceInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_SINCE_INFO, true);
	}

	public boolean isCreateStatisticsEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_STATISTICS, true);
	}

	public boolean isCreateSynopsisEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_CREATE_SYNOPSIS, true);
	}

	public boolean isCreateVersionInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_VERSION_INFO, true);
	}

	public boolean isForceAnnotationDocumentationEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_FORCE_ANNOTATION_DOCUMENTATION,
				false);
	}

	public boolean isLinkSourceEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DBDOCLET_LINK_SOURCE, false);
	}

	public void setAuthorEmail(String email) {
		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_AUTHOR_EMAIL, email);
	}

	public void setAuthorFirstname(String firstname) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_AUTHOR_FIRSTNAME,
				firstname);
	}

	public void setAuthorSurname(String surname) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_AUTHOR_SURNAME, surname);
	}

	public void setChunkDocBookEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CHUNK_DOCBOOK_ENABLED,
				enabled);
	}

	public void setCopyrightHolder(String copyrightHolder) {
		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_COPYRIGHT_HOLDER,
				copyrightHolder);
	}

	public void setCopyrightYear(String copyrightYear) {
		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_COPYRIGHT_YEAR,
				copyrightYear);
	}

	public void setCorporation(String corporation) {
		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_CORPORATION, corporation);
	}

	public void setCreateAppendixEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_APPENDIX_ENABLED,
				enabled);
	}

	public void setCreateExceptionInfoEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_EXCEPTION_INFO,
				enabled);
	}

	public void setCreateFieldInfoEnabled(boolean createFieldInfoEnabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_FIELD_INFO,
				createFieldInfoEnabled);
	}

	public void setCreateFullyQualifiedNamesEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(
				DbdConstants.PARAM_DBDOCLET_CREATE_FULLY_QUALIFIED_NAMES,
				enabled);
	}

	public void setCreateIndexEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_INDEX, enabled);
	}

	public void setCreateInheritanceInfoEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_INHERITANCE_INFO,
				enabled);
	}

	public void setCreateInheritedFromInfoEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(
				DbdConstants.PARAM_DBDOCLET_CREATE_INHERITED_FROM_INFO, enabled);
	}

	public void setCreateMetaInfoEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_META_INFO,
				enabled);
	}

	public void setCreateMethodInfoEnabled(boolean createMethodInfoEnabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_METHOD_INFO,
				createMethodInfoEnabled);
	}

	public void setCreateParameterInfoEnabled(boolean createParameterInfoEnabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_PARAMETER_INFO,
				createParameterInfoEnabled);
	}

	public void setCreateSeeAlsoInfoEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_SEE_ALSO_INFO,
				enabled);
	}

	public void setCreateSerialFieldInfoEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_SERIAL_FIELD_INFO,
				enabled);
	}

	public void setCreateStatisticsEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_STATISTICS,
				enabled);
	}

	public void setCreateSynopsisEnabled(boolean createSynopsisEnabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(DbdConstants.PARAM_DBDOCLET_CREATE_SYNOPSIS,
				createSynopsisEnabled);
	}

	public boolean setCreateXrefLabelEnabled() {
		return script.isParameterOn(DbtConstants.SECTION_DOCBOOK,
				DbtConstants.PARAM_CREATE_XREF_LABEL, true);
	}

	public void setDestinationEncoding(String encoding) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_ENCODING, encoding);
	}

	public void setDocBookVersion(String docBookVersion) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_DOCBOOK_VERSION, docBookVersion);
	}

	public void setDocumentationId(String documentationId) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_DOCUMENTATION_ID,
				documentationId);
	}

	public void setDocumentElement(String documentElement) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_DOCUMENT_ELEMENT,
				documentElement);
	}

	public void setForceAnnotationDocumentationEnabled(boolean enabled) {
		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setBoolParameter(
				DbdConstants.PARAM_DBDOCLET_FORCE_ANNOTATION_DOCUMENTATION,
				enabled);
	}

	public void setHyphenationChar(String selectedItem) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_HYPHENATION_CHAR,
				"soft-hyphen");
	}

	public void setLanguage(String language) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_LANGUAGE, language);
	}

	public void setLogoPath(String logoPath) {

		script.selectSection(DbdConstants.SECTION_DBDOCLET);
		script.setTextParameter(DbdConstants.PARAM_DBDOCLET_LOGO_PATH, logoPath);
	}

	public void setOutputFile(File destination) {

		if (destination.isDirectory()) {
			outputFile = new File(destination, "Reference.xml");
		} else {
			outputFile = destination;
		}
	}

	public void setReleaseInfo(String releaseInfo) {
		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_RELEASE_INFO,
				releaseInfo);
	}

	public void setScript(Script script) {
		this.script = script;
	}

	public void setTableStyle(String tableStyle) {
		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_TABLE_STYLE, tableStyle);
	}

	public void setTitle(String title) {

		script.selectSection(DbtConstants.SECTION_DOCBOOK);
		script.setTextParameter(DbtConstants.PARAM_TITLE, title);
	}
}

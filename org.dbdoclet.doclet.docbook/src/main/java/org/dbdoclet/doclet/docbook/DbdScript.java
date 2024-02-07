package org.dbdoclet.doclet.docbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.dbdoclet.doclet.doc.XmlIdType;
import org.dbdoclet.tag.docbook.DocBookVersion;
import org.dbdoclet.trafo.TrafoConstants;
import org.dbdoclet.trafo.param.BooleanParam;
import org.dbdoclet.trafo.param.TextParam;
import org.dbdoclet.trafo.script.Script;
import org.dbdoclet.xiphias.Hyphenation.HyphenationChar;

import com.google.inject.Inject;

public class DbdScript {

	public static final File DEFAULT_DESTINATION_FILE = new File(
			"./dbdoclet/Reference.xml");

	private File outputFile;

	@Inject
	private Script script;

	public void addContext(String context) {
		script.addContext(context);
	}

	public String getAbstract() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_ABSTRACT, "");
	}

	public String getAuthorEmail() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_AUTHOR_EMAIL, "");
	}

	public String getAuthorFirstname() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_AUTHOR_FIRSTNAME, "");
	}

	public String getAuthorSurname() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_AUTHOR_SURNAME, "");
	}

	public String getClassDiagramFontFamily() {

		String fontFamily= script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CLASS_DIAGRAM_FONT_FAMILY, "SansSerif");

		return fontFamily;
	}

	/**
	 * Bestimmt die Größe der Schriftart für die Klassendiagramme in Pixel. Der
	 * minimale Wert ist 4. Als Standardwert ist 12 vorgegeben.
	 */
	public int getClassDiagramFontSize() {

		int fontSize = script.getIntParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CLASS_DIAGRAM_FONT_SIZE, 8);

		if (fontSize < 4) {
			fontSize = 4;
		}

		return fontSize;
	}

	public int getClassDiagramHeight() {
		return script.getIntParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CLASS_DIAGRAM_HEIGHT, 0);
	}

	/**
	 * Maximum width of a class diagram.
	 * 
	 * @return width
	 */
	public int getClassDiagramWidth() {
		return script.getIntParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CLASS_DIAGRAM_WIDTH, 0);
	}

	public String getCopyrightHolder() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_COPYRIGHT_HOLDER, "");
	}

	public String getCopyrightYear() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_COPYRIGHT_YEAR, "");
	}

	public String getCorporation() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_CORPORATION, "");
	}

	public File getDestinationDirectory() {

		if (outputFile == null) {
			outputFile = DEFAULT_DESTINATION_FILE;
		}

		return outputFile.getParentFile();
	}

	public String getDestinationEncoding() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_ENCODING, "UTF-8");
	}

	public File getDestinationFile() {

		if (outputFile == null) {
			outputFile = DEFAULT_DESTINATION_FILE;
		}

		return outputFile;
	}

	public DocBookVersion getDocBookVersion() {

		String version = script.getTextParameter(
				TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_DOCBOOK_VERSION, "5.0");

		if (version != null && version.trim().equals("4.5")) {
			return DocBookVersion.V4_5;
		}

		return DocBookVersion.V5_0;
	}

	public String getDocumentationId() {
		String documentationId = script.getTextParameter(
				TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_DOCUMENTATION_ID, "dbdoclet");
		return documentationId;
	}

	public String getDocumentElement() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_DOCUMENT_ELEMENT, "book");
	}

	public String getDocumentStyle() {
		return script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_DOCUMENT_STYLE,
				DbdConstants.DEFAULT_DOCUMENT_STYLE);
	}

	public HyphenationChar getHyphenationChar() {

		String hyphenationChar = script.getTextParameter(
				TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_HYPHENATION_CHAR, "soft-hyphen");

		if (hyphenationChar != null && hyphenationChar.equals("soft-hyphen")) {
			return HyphenationChar.SOFT_HYPHEN;
		} else {
			return HyphenationChar.ZERO_WIDTH_SPACE;
		}
	}

	public String getHyphenationCharAsText() {

		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_HYPHENATION_CHAR, "soft-hyphen");
	}

	public XmlIdType getIdStyle() {

		String idStyle = script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_ID_STYLE, "numbered");

		if (idStyle != null && idStyle.equalsIgnoreCase("java")) {
			return XmlIdType.JAVA;
		} else {
			return XmlIdType.NUMBERED;
		}
	}

	public List<String> getImageDataFormats() {

		ArrayList<String> defaultFormatList = new ArrayList<String>();
		defaultFormatList.add("PNG");

		return script.getTextParameterList(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_IMAGEDATA_FORMATS, defaultFormatList);
	}

	public String getImagePath() {

		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_IMAGE_PATH,
				TrafoConstants.DEFAULT_IMAGE_PATH);
	}

	public String getLanguage() {

		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_LANGUAGE, Locale.getDefault()
						.getLanguage());
	}

	public String getListPresentation() {
		return script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_LIST_PRESENTATION, null);
	}

	public String getLogoPath() {
		return script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_LOGO_PATH, "");
	}

	public String getOverviewTitle() {
		return script.getTextParameter(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_OVERVIEW_TITLE, "");
	}

	public String getReleaseInfo() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_RELEASE_INFO, "");
	}

	public Script getScript() {
		return script;
	}

	public String getTableStyle() {
		return script.getTextParameter(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_TABLE_STYLE,
				TrafoConstants.DEFAULT_TABLE_STYLE);
	}

	public ArrayList<String> getTagList() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		String titleText = script.getTextParameter(
				TrafoConstants.SECTION_DOCBOOK, TrafoConstants.PARAM_TITLE,
				null);
		return titleText;
	}

	public boolean hasProlog() {
		return script.isParameterOn(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_CREATE_PROLOG, true);
	}

	public boolean isAbsoluteImagePathEnabled() {
		return script.isParameterOn(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_USE_ABSOLUTE_IMAGE_PATH, false);
	}

	public boolean isAddIndexEnabled() {
		return script.isParameterOn(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_ADD_INDEX, true);
	}

	public boolean isChunkDocBookEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CHUNK_DOCBOOK_ENABLED, false);
	}

	public boolean isClassDiagramContainsAttributes() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CLASS_DIAGRAM_CONTAINS_ATTRIBUTES, true);
	}

	public boolean isClassDiagramContainsOperations() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CLASS_DIAGRAM_CONTAINS_OPERATIONS, true);
	}

	public boolean isClassDiagramIncludesObject() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CLASS_DIAGRAM_INCLUDES_OBJECT, true);
	}

	public boolean isCreateAppendixEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_APPENDIX, true);
	}

	public boolean isCreateAuthorInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_AUTHOR_INFO, true);
	}

	/**
	 * Der Parameter <code>create-class-diagram</code> bestimmt, ob für jede
	 * Klasse ein Klassendiagramm erstellt werden soll.
	 */
	public boolean isCreateClassDiagramEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_CLASS_DIAGRAM, true);
	}

	/**
	 * Der Parameter <code>create-constant-value</code> bestimmt, ob eine
	 * Sektion eingefügt werden soll, die alle gefundenen Konstanten zusammen
	 * mit ihren Werten auflistet.
	 */
	public boolean isCreateConstantValuesEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_SECTION_CONSTANT_VALUES, true);
	}

	public boolean isCreateDeprecatedInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_DEPRECATED_INFO, true);
	}

	public boolean isCreateDeprecatedListEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_DEPRECATED_LIST, true);
	}

	public boolean isCreateExceptionInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_EXCEPTION_INFO, true);
	}

	public boolean isCreateFieldInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_FIELD_INFO, true);
	}

	public boolean isCreateFullyQualifiedNamesEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_FULLY_QUALIFIED_NAMES, false);
	}

	public boolean isCreateInheritanceInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_INHERITANCE_INFO, true);
	}

	public boolean isCreateInheritedFromInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_INHERITED_FROM_INFO, true);
	}

	public boolean isCreateMetaInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_META_INFO, true);
	}

	public boolean isCreateMethodInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_METHOD_INFO, true);
	}

	public boolean isCreateParameterInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_PARAMETER_INFO, true);
	}

	public boolean isCreateSeeAlsoInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_SEE_ALSO_INFO, true);
	}

	public boolean isCreateSerialFieldInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_SERIAL_FIELD_INFO, true);
	}

	public boolean isCreateSinceInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_SINCE_INFO, true);
	}

	public boolean isCreateStatisticsEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_STATISTICS, false);
	}

	public boolean isCreateSynopsisEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_SYNOPSIS, true);
	}

	public boolean isCreateVersionInfoEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_CREATE_VERSION_INFO, true);
	}

	public boolean isForceAnnotationDocumentationEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_FORCE_ANNOTATION_DOCUMENTATION, false);
	}

	public boolean isLinkSourceEnabled() {
		return script.isParameterOn(DbdConstants.SECTION_DBDOCLET,
				DbdConstants.PARAM_LINK_SOURCE, false);
	}

	public void removeContext(String context) {
		script.removeContext(context);
	}

	public void removeContext(TransformPosition context) {
		script.getTransformPosition();
	}

	public void setAddIndexEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(
						new BooleanParam(TrafoConstants.PARAM_ADD_INDEX,
								enabled));
	}

	public void setAuthorEmail(String email) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_AUTHOR_EMAIL, email));
	}

	public void setAuthorFirstname(String firstname) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_AUTHOR_FIRSTNAME,
				firstname));
	}

	public void setAuthorSurname(String surname) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_AUTHOR_SURNAME, surname));
	}

	public void setChunkDocBookEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CHUNK_DOCBOOK_ENABLED,
				enabled));
	}

	public void setCopyrightHolder(String copyrightHolder) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_COPYRIGHT_HOLDER,
				copyrightHolder));
	}

	public void setCopyrightYear(String copyrightYear) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_COPYRIGHT_YEAR,
				copyrightYear));
	}

	public void setCorporation(String corporation) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_CORPORATION, corporation));
	}

	public void setCreateAppendixEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_APPENDIX, enabled));
	}

	public void setCreateExceptionInfoEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_EXCEPTION_INFO,
				enabled));
	}

	public void setCreateFieldInfoEnabled(boolean createFieldInfoEnabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_FIELD_INFO,
				createFieldInfoEnabled));
	}

	public void setCreateFullyQualifiedNamesEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_FULLY_QUALIFIED_NAMES, enabled));
	}

	public void setCreateInheritanceInfoEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_INHERITANCE_INFO,
				enabled));
	}

	public void setCreateInheritedFromInfoEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_INHERITED_FROM_INFO,
				enabled));
	}

	public void setCreateMetaInfoEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_META_INFO, enabled));
	}

	public void setCreateMethodInfoEnabled(boolean createMethodInfoEnabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_METHOD_INFO,
				createMethodInfoEnabled));
	}

	public void setCreateParameterInfoEnabled(boolean createParameterInfoEnabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_PARAMETER_INFO,
				createParameterInfoEnabled));
	}

	public void setCreateSeeAlsoInfoEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_SEE_ALSO_INFO,
				enabled));
	}

	public void setCreateSerialFieldInfoEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_SERIAL_FIELD_INFO,
				enabled));
	}

	public void setCreateStatisticsEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_STATISTICS, enabled));
	}

	public void setCreateSynopsisEnabled(boolean createSynopsisEnabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_CREATE_SYNOPSIS,
				createSynopsisEnabled));
	}

	public boolean setCreateXrefLabelEnabled() {
		return script.isParameterOn(TrafoConstants.SECTION_DOCBOOK,
				TrafoConstants.PARAM_CREATE_XREF_LABEL, true);
	}

	public void setDestinationEncoding(String encoding) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_ENCODING, encoding));
	}

	public void setDocBookVersion(String docBookVersion) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_DOCBOOK_VERSION,
				docBookVersion));
	}

	public void setDocumentationId(String documentationId) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_DOCUMENTATION_ID,
				documentationId));
	}

	public void setDocumentElement(String documentElement) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_DOCUMENT_ELEMENT,
				documentElement));
	}

	public void setEncoding(String encoding) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_ENCODING, encoding));
	}

	public void setForceAnnotationDocumentationEnabled(boolean enabled) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new BooleanParam(DbdConstants.PARAM_FORCE_ANNOTATION_DOCUMENTATION, enabled));
	}

	public void setHyphenationChar(String selectedItem) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_HYPHENATION_CHAR,
				"soft-hyphen"));
	}

	public void setImagePath(String imagePath) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_IMAGE_PATH, imagePath));
	}

	public void setLanguage(String language) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_LANGUAGE, language));
	}

	public void setLogoPath(String logoPath) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(DbdConstants.PARAM_LOGO_PATH, logoPath));
	}

	public void setOutputFile(File destination) {

		if (destination.isDirectory()) {
			outputFile = new File(destination, "Reference.xml");
		} else {
			outputFile = destination;
		}
	}

	public void setReleaseInfo(String releaseInfo) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_RELEASE_INFO, releaseInfo));
	}

	public void setScript(Script script) {
		this.script = script;
	}

	public void setTableStyle(String tableStyle) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_TABLE_STYLE, tableStyle));
	}

	public void setTitle(String title) {
		script.getNamespace()
				.findOrCreateSection(TrafoConstants.SECTION_DOCBOOK)
				.setParam(new TextParam(TrafoConstants.PARAM_TITLE, title));
	}

	public void setTransformPosition(TransformPosition context) {
		script.setTransformPosition(context);
	}
}

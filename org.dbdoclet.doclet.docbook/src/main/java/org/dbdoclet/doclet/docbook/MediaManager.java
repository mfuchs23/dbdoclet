/*
 * $Id$
 *
 * ### Copyright (C) 2006-2012 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.ClassDiagramManager;
import org.dbdoclet.doclet.DeprecatedManager;
import org.dbdoclet.doclet.DocletContext;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.InstanceFactory;
import org.dbdoclet.doclet.ReferenceManager;
import org.dbdoclet.doclet.StatisticData;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.doclet.statistic.ClassesPerPackage;
import org.dbdoclet.doclet.statistic.DirectKnownSubclasses;
import org.dbdoclet.doclet.statistic.TotalsDiagram;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.XInclude;
import org.dbdoclet.tag.docbook.Abstract;
import org.dbdoclet.tag.docbook.Address;
import org.dbdoclet.tag.docbook.Affiliation;
import org.dbdoclet.tag.docbook.Article;
import org.dbdoclet.tag.docbook.Author;
import org.dbdoclet.tag.docbook.Book;
import org.dbdoclet.tag.docbook.Chapter;
import org.dbdoclet.tag.docbook.Copyright;
import org.dbdoclet.tag.docbook.Date;
import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.DocBookVersion;
import org.dbdoclet.tag.docbook.Email;
import org.dbdoclet.tag.docbook.FirstName;
import org.dbdoclet.tag.docbook.Holder;
import org.dbdoclet.tag.docbook.ImageData;
import org.dbdoclet.tag.docbook.ImageObject;
import org.dbdoclet.tag.docbook.InformalFigure;
import org.dbdoclet.tag.docbook.InformalTable;
import org.dbdoclet.tag.docbook.LegalNotice;
import org.dbdoclet.tag.docbook.ListItem;
import org.dbdoclet.tag.docbook.MediaObject;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Personname;
import org.dbdoclet.tag.docbook.ReleaseInfo;
import org.dbdoclet.tag.docbook.Sect1;
import org.dbdoclet.tag.docbook.Section;
import org.dbdoclet.tag.docbook.SimPara;
import org.dbdoclet.tag.docbook.Surname;
import org.dbdoclet.tag.docbook.Table;
import org.dbdoclet.tag.docbook.Tbody;
import org.dbdoclet.tag.docbook.Term;
import org.dbdoclet.tag.docbook.Tgroup;
import org.dbdoclet.tag.docbook.Title;
import org.dbdoclet.tag.docbook.VarListEntry;
import org.dbdoclet.tag.docbook.VariableList;
import org.dbdoclet.tag.docbook.XRef;
import org.dbdoclet.tag.docbook.Year;
import org.dbdoclet.tag.html.Img;
import org.dbdoclet.xiphias.ImageServices;
import org.dbdoclet.xiphias.NodeSerializer;
import org.dbdoclet.xiphias.XmlServices;
import org.w3c.dom.Element;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public abstract class MediaManager {

	private static Log logger = LogFactory.getLog(MediaManager.class.getName());

	protected DocletContext context;
	protected DbdTransformer htmlDocBookTrafo;
	protected TreeMap<String, TreeMap<String, ClassDoc>> pkgMap;
	protected ReferenceManager referenceManager;
	protected StatisticData statisticData;
	protected ResourceBundle res;
	protected DbdScript script;
	protected Style style;
	protected DocBookTagFactory tagFactory;
	protected TagManager tagManager;

	protected void createAdditionalSections(DocBookElement parent)
			throws IOException, DocletException {

		if (script.isCreateConstantValuesEnabled()) {
			writeConstantFieldValues(pkgMap, parent);
		}

		if (script.isCreateDeprecatedListEnabled()) {
			writeDeprecatedList(pkgMap, parent);
		}

		if (script.isCreateStatisticsEnabled()) {

			logger.info(ResourceServices.getString(res,
					"C_CONSTRUCTING_STATISTICS"));
			writeStatistics(pkgMap, parent);
		}

		if (script.isAddIndexEnabled()) {
			parent.appendChild(tagFactory.createIndex());
		}
	}

	private TreeMap<String, TreeMap<String, ClassDoc>> createClassMap(
			PackageDoc[] pkgDocs, ClassDoc[] classDocs) {

		if (pkgDocs == null) {
			throw new IllegalArgumentException(
					"The argument pkgDocs must not be null!");
		}

		if (classDocs == null) {
			throw new IllegalArgumentException(
					"The argument classDocs must not be null!");
		}

		TreeMap<String, TreeMap<String, ClassDoc>> pkgMap = new TreeMap<String, TreeMap<String, ClassDoc>>();
		TreeMap<String, ClassDoc> classMap;

		String pkgName;

		for (int i = 0; i < pkgDocs.length; i++) {

			pkgName = pkgDocs[i].name();
			classMap = pkgMap.get(pkgName);

			if (classMap == null) {

				classMap = new TreeMap<String, ClassDoc>();
				pkgMap.put(pkgName, classMap);
			}

			ClassDoc[] docs = pkgDocs[i].allClasses();

			for (int j = 0; j < docs.length; j++) {
				classMap.put(docs[j].qualifiedName(), docs[j]);
			}
		}

		for (int i = 0; i < classDocs.length; i++) {

			pkgName = classDocs[i].containingPackage().name();
			classMap = pkgMap.get(pkgName);

			if (classMap == null) {

				classMap = new TreeMap<String, ClassDoc>();
				pkgMap.put(pkgName, classMap);
			}

			classMap.put(classDocs[i].qualifiedName(), classDocs[i]);
		}

		return pkgMap;
	}

	protected void createInfoSection(DocBookElement parent, Para summary)
			throws IOException {

		File destPath = script.getDestinationDirectory();
		String language = script.getLanguage();
		String authorFirstname = script.getAuthorFirstname();
		String authorSurname = script.getAuthorSurname();
		String authorEmail = script.getAuthorEmail();
		String copyrightYear = script.getCopyrightYear();
		String copyrightHolder = script.getCopyrightHolder();
		String corporation = script.getCorporation();
		String releaseInfo = script.getReleaseInfo();
		String logoPath = script.getLogoPath();

		if ((isDefined(authorFirstname) || isDefined(authorSurname) || isDefined(authorEmail))) {

			Author author = tagFactory.createAuthor();
			parent.appendChild(author);

			if (isDocBook5()) {

				if (isDefined(authorFirstname) || isDefined(authorSurname)) {
					
					Personname personname = tagFactory.createPersonname();
					author.appendChild(personname);

					if (isDefined(authorFirstname)) {
						FirstName firstName = tagFactory
							.createFirstName(authorFirstname);
						personname.appendChild(firstName);
					}
					
					if (isDefined(authorSurname)) {
						Surname surname = tagFactory.createSurname(authorSurname);
						personname.appendChild(surname);
					}
				}

			} else {

				if (isDefined(authorFirstname)) {
					FirstName firstName = tagFactory
							.createFirstName(authorFirstname);
					author.appendChild(firstName);
				}

				if (isDefined(authorSurname)) {
					Surname surname = tagFactory.createSurname(authorSurname);
					author.appendChild(surname);
				}
			}

			if (isDefined(authorEmail)) {

				Affiliation affiliation = tagFactory.createAffiliation();
				author.appendChild(affiliation);

				Address address = tagFactory.createAddress();
				affiliation.appendChild(address);

				Email email = tagFactory.createEmail(authorEmail);
				address.appendChild(email);
			}
		}

		if (copyrightYear != null && copyrightYear.length() > 0
				&& copyrightHolder != null && copyrightHolder.length() > 0) {

			Copyright copyright = tagFactory.createCopyright();
			parent.appendChild(copyright);

			Year year = tagFactory.createYear(copyrightYear);
			copyright.appendChild(year);

			Holder holder = tagFactory.createHolder(copyrightHolder);
			copyright.appendChild(holder);
		}

		if (corporation != null && corporation.length() > 0) {

			LegalNotice legalNotice = tagFactory.createLegalNotice();
			parent.appendChild(legalNotice);

			SimPara para = tagFactory.createSimPara(corporation + ". "
					+ ResourceServices.getString(res, "C_ALL_RIGHTS_RESERVED"));
			legalNotice.appendChild(para);
		}

		if (releaseInfo != null && releaseInfo.length() > 0) {
			ReleaseInfo child = tagFactory.createReleaseInfo(releaseInfo);
			parent.appendChild(child);
		}

		logger.trace(String
				.format("(logoPath) script: logoPath = %s", logoPath));

		if (logoPath != null && logoPath.length() > 0) {

			String extension = FileServices.getExtension(logoPath);

			if (extension == null) {
				extension = "";
			}

			extension = extension.toUpperCase();

			if (FileServices.isAbsolutePath(logoPath) == false) {
				logoPath = FileServices.appendFileName(destPath, logoPath);
			}

			boolean isValidLogo = true;

			File logoFile = new File(logoPath);

			if (logoFile.exists() == false) {

				logger.warn(String.format(
						"(logoPath) Logo file %s doesn't exist.",
						logoFile.getAbsolutePath()));
				isValidLogo = false;
			}

			if (isValidLogo == true && logoFile.isFile() == false) {
				logger.warn("(logoPath) Logo file "
						+ logoFile.getAbsolutePath() + " is not a normal file.");
				isValidLogo = false;
			}

			if (isValidLogo == true) {

				logger.trace(String.format(
						"(logoPath) The logo file %s is valid.", logoPath));

				List<String> formatList = script.getImageDataFormats();

				if (formatList.contains("BASE64")) {

					logoPath = FileServices.getFileBase(logoFile) + ".base64";
					FileServices.writeFromString(new File(logoPath),
							ImageServices.toXml(logoFile));
					extension = "base64";

					if (FileServices.isAbsolutePath(logoPath)) {
						logoPath = new File(logoPath).toURI().toURL()
								.toString();
					}
				}

				MediaObject mediaObject = tagFactory.createMediaObject();
				ImageObject imageObject = tagFactory.createImageObject();
				ImageData imageData = tagFactory.createImageData();

				imageData.setFileRef(logoPath);
				// imageData.setFormat(extension);
				imageData.setWidth(ImageServices.getWidth(logoFile) + "px");
				imageData.setDepth(ImageServices.getHeight(logoFile) + "px");
				imageObject.appendChild(imageData);
				mediaObject.appendChild(imageObject);
				parent.appendChild(mediaObject);
			}
		}

		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, new Locale(
				language, ""));

		Date date = tagFactory.createDate(df.format(new java.util.Date()));
		parent.appendChild(date);

		Abstract abstractElement = tagFactory.createAbstract();
		parent.appendChild(abstractElement);
		abstractElement.appendChild(summary);

	}

	private boolean isDefined(String value) {

		if (value == null || value.trim().length() == 0) {
			return false;
		}
		
		return true;
	}

	protected void createInheritanceDiagram(ClassDoc classDoc,
			DocBookElement parent) throws DocletException {

		DocBookElement media;

		ClassDiagramManager classDiagramManager = InstanceFactory
				.getInstance(ClassDiagramManager.class);

		String filebase = classDiagramManager.createClassDiagram(classDoc,
				script.getDestinationDirectory());

		logger.debug("filebase ='" + filebase + "'");

		InformalFigure figure = tagFactory.createInformalFigure();
		media = tagFactory.createMediaObject();
		media.setParentNode(figure);
		figure.appendChild(media);


		try {

			String fileName = filebase + ".png";
			File file = new File(fileName);

			Img img = new Img();
			img.setAttribute("align", "center");
			img.setAttribute("src", fileName);

			tagFactory.createHtmlImageData(media, tagFactory, img, file);

			fileName = filebase + ".svg";
			file = new File(fileName);

			img = new Img();
			img.setAttribute("align", "center");
			img.setAttribute("src", fileName);

			tagFactory.createFoImageData(media, tagFactory, img, file);

		} catch (IOException oops) {
			throw new DocletException(oops);
		}

		parent.appendChild(figure);
	}

	protected void createModuleFile(ClassDoc classDoc, DocBookElement parent,
			DocBookElement moduleElement) throws IOException {

		File destPath = script.getDestinationDirectory();
		String fileName = StringServices.replace(classDoc.qualifiedName(), ".",
				"-") + ".xml";
		String fqfn = FileServices.appendFileName(destPath, fileName);

		File file = new File(fqfn);
		FileServices.createPath(file.getParentFile());

		DocBookDocument doc = new DocBookDocument();
		String rootTagName = moduleElement.getTagName();

		if (isDocBook5() == false) {
			DbdServices.appendDoctype(doc, rootTagName);
		} else {
			DbdServices.addNamespace(moduleElement);
		}

		doc.setDocumentElement(moduleElement);

		writeFile(doc, new File(fqfn));

		XInclude xinclude = new XInclude(fileName);
		parent.appendChild(xinclude);

	}

	protected void createSynopsisSection(ClassDoc classDoc,
			DocBookElement parent) throws DocletException {

		if (script.isCreateSynopsisEnabled() == true) {
			style.addClassSynopsis(classDoc, parent);
		}

		style.addMetaInfo(classDoc, parent);

		if (script.isCreateClassDiagramEnabled()) {
			createInheritanceDiagram(classDoc, parent);
		}
	}

	protected String getClassTypeAsText(ClassDoc doc) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		String typeName = ResourceServices.getString(res, "C_CLASS");

		if (doc.isAnnotationType()) {

			typeName = ResourceServices.getString(res, "C_ANNOTATION_TYPE");

		} else if (doc.isInterface()) {

			typeName = ResourceServices.getString(res, "C_INTERFACE");

		} else if (doc.isException()) {

			typeName = ResourceServices.getString(res, "C_EXCEPTION");

		} else if (doc.isError()) {

			typeName = ResourceServices.getString(res, "C_ERROR");
		}

		return typeName;
	}

	protected DocletContext getContext() {
		return context;
	}

	protected String getIndexCategory(ClassDoc doc) {

		String indexCategory = "Classes";

		if (doc.isAnnotationType()) {

			indexCategory = "Annotations";

		} else if (doc.isInterface()) {

			indexCategory = "Interfaces";

		} else if (doc.isException()) {

			indexCategory = "Exceptions";

		} else if (doc.isError()) {

			indexCategory = "Errors";
		}

		return indexCategory;
	}

	protected TreeMap<String, TreeMap<String, ClassDoc>> getPackageMap() {
		return pkgMap;
	}

	protected String getReference(Doc doc) {

		PackageDoc pdoc;
		ClassDoc cdoc;
		ExecutableMemberDoc mdoc;
		FieldDoc fdoc;

		String key;

		if (doc instanceof PackageDoc) {

			pdoc = (PackageDoc) doc;
			key = pdoc.name();

			return referenceManager.getId(key);
		}

		if (doc instanceof ClassDoc) {

			cdoc = (ClassDoc) doc;
			key = cdoc.qualifiedName();

			return referenceManager.getId(key);
		}

		if (doc instanceof ExecutableMemberDoc) {

			mdoc = (ExecutableMemberDoc) doc;
			key = referenceManager.createMethodKey(mdoc);

			return referenceManager.getId(key);
		}

		if (doc instanceof FieldDoc) {

			fdoc = (FieldDoc) doc;
			key = fdoc.qualifiedName();

			return referenceManager.getId(key);
		}

		return null;
	}

	public ReferenceManager getReferenceManager() {
		return referenceManager;
	}

	protected ResourceBundle getResourceBundle() {
		return res;
	}

	public StatisticData getStatisticData() {
		return statisticData;
	}

	public Style getStyle() {
		return style;
	}

	public DocBookTagFactory getTagFactory() {
		return tagFactory;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	protected Title getTitle() {
		return tagFactory.createTitle(script.getTitle());
	}

	protected String getVisibilityAsText(ProgramElementDoc doc) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (doc.isPrivate()) {
			return "private";
		}

		if (doc.isPackagePrivate() || doc.isProtected()) {
			return "protected";
		}

		if (doc.isPublic()) {
			return "public";
		}

		return "unknown";
	}

	protected boolean isDocBook5() {

		DocBookVersion version = script.getDocBookVersion();
		logger.debug("DocBookVersion = " + version);

		if (version != null && version == DocBookVersion.V5_0) {
			return true;
		} else {
			return false;
		}
	}

	protected abstract void process(RootDoc rootDoc) throws DocletException;

	public void setDocletContext(DocletContext context) {
		this.context = context;
	}

	public void setHtmlDocBookTrafo(DbdTransformer transformer) {
		this.htmlDocBookTrafo = transformer;
	}

	public void setReferenceManager(ReferenceManager referenceManager) {
		this.referenceManager = referenceManager;
	}

	public void setResourceBundle(ResourceBundle res) {
		this.res = res;
	}

	public void setScript(DbdScript script) {
		this.script = script;
	}

	public void setStatisticData(StatisticData statisticData) {
		this.statisticData = statisticData;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public void setTagFactory(DocBookTagFactory tagFactory) {
		this.tagFactory = tagFactory;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	protected void writeConstantFieldValues(
			TreeMap<String, TreeMap<String, ClassDoc>> pkgMap,
			DocBookElement parent) throws IOException {

		TreeMap<String, TreeMap<String, TreeMap<String, FieldDoc>>> pkgFieldMap = tagManager
				.getConstantFieldMap();

		if (pkgFieldMap != null && pkgFieldMap.size() > 0) {

			DocBookElement component;

			if (parent instanceof Article) {

				component = tagFactory.createSection();

			} else {

				if (script.isCreateAppendixEnabled() == false) {
					component = tagFactory.createChapter();
				} else {
					component = tagFactory.createAppendix();
				}
			}

			component.appendChild(tagFactory.createTitle(ResourceServices
					.getString(res, "C_CONSTANT_FIELD_VALUES")));

			boolean hasChild = false;

			FieldDoc fieldDoc;
			Object value;
			Section section;
			String className;
			String fieldName;
			String fieldValue;
			String pkgName;
			String qualifiedTypeName;
			TreeMap<String, FieldDoc> fieldMap;
			TreeMap<String, TreeMap<String, FieldDoc>> classMap;
			Type type;

			for (Iterator<String> pkgIterator = pkgFieldMap.keySet().iterator(); pkgIterator
					.hasNext();) {

				pkgName = pkgIterator.next();
				section = tagFactory.createSection(ResourceServices.getString(
						res, "C_PACKAGE") + " " + pkgName + ".*");

				classMap = pkgFieldMap.get(pkgName);

				if (classMap.size() > 0) {
					component.appendChild(section);
					hasChild = true;
				}

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();

					Table table = tagFactory.createTable();
					table.setFrame("all");
					table.appendChild(tagFactory.createTitle(className));
					section.appendChild(table);

					Tgroup tgroup = tagFactory.createTgroup(2);
					table.appendChild(tgroup);

					Tbody tbody = tagFactory.createTbody();
					tgroup.appendChild(tbody);

					fieldMap = classMap.get(className);

					for (Iterator<String> fieldIterator = fieldMap.keySet()
							.iterator(); fieldIterator.hasNext();) {

						fieldName = fieldIterator.next();
						fieldDoc = fieldMap.get(fieldName);

						value = fieldDoc.constantValue();
						type = fieldDoc.type();
						qualifiedTypeName = type.qualifiedTypeName();

						if (value != null) {

							fieldValue = value.toString();
							fieldValue = XmlServices.textToXml(fieldValue);

							if (qualifiedTypeName.equals("java.lang.String")) {
								fieldValue = "\"" + fieldValue + "\"";
							}

							tbody.addRow(fieldDoc.name(), fieldValue);

						} else {
							tbody.addRow(fieldDoc.name(), "null");
						}
					}
				}
			}

			if (hasChild == true) {
				parent.appendChild(component);
			}
		}
	}

	public void writeContents(RootDoc rootDoc) throws DocletException {

		try {

			pkgMap = createClassMap(rootDoc.specifiedPackages(),
					rootDoc.specifiedClasses());

			logger.info(ResourceServices.getString(res,
					"C_CONSTRUCTING_REFERENCE_MAP") + "...");

			referenceManager.init(script.getDocumentationId(), pkgMap,
					script.getIdStyle());

			statisticData.init(pkgMap);

			logger.info(ResourceServices.getString(res,
					"C_CONSTRUCTING_TAG_MAP") + "...");

			tagManager.createTagMap(pkgMap);
			process(rootDoc);

		} catch (Exception oops) {

			if (oops instanceof DocletException) {

				throw (DocletException) oops;

			} else {

				throw new DocletException(oops);
			}
		}
	}

	protected void writeDeprecatedList(
			TreeMap<String, TreeMap<String, ClassDoc>> pkgMap,
			DocBookElement parent) throws IOException, DocletException {

		boolean hasChild = true;

		DocBookElement component;
		ArrayList<Doc> docList = DbdServices.createDocList(pkgMap);

		DeprecatedManager deprecatedManager = new DeprecatedManager();

		for (Doc doc : docList) {
			deprecatedManager.addDoc(doc);
		}

		if (parent instanceof Article) {

			component = tagFactory.createSection();

		} else {

			if (script.isCreateAppendixEnabled() == false) {
				component = tagFactory.createChapter();
			} else {
				component = tagFactory.createAppendix();
			}
		}

		component.appendChild(tagFactory.createTitle(ResourceServices
				.getString(res, "C_DEPRECATED_API")));

		TreeMap<String, ArrayList<Doc>> deprecatedMap = deprecatedManager
				.getDeprecatedMap();

		if (deprecatedMap.size() == 0) {
			return;
		}

		for (String title : deprecatedMap.keySet()) {

			Section section = tagFactory.createSection(ResourceServices
					.getString(res, title));
			component.appendChild(section);

			docList = deprecatedMap.get(title);

			VariableList list = tagFactory.createVariableList();
			section.appendChild(list);

			for (Doc doc : docList) {

				VarListEntry varListEntry = tagFactory.createVarListEntry();
				list.appendChild(varListEntry);

				Term term = tagFactory.createTerm();
				varListEntry.appendChild(term);

				ListItem listItem = tagFactory.createListItem();
				varListEntry.appendChild(listItem);

				Para para = tagFactory.createPara();
				listItem.appendChild(para);

				if (doc instanceof ProgramElementDoc) {

					XRef xref = tagFactory.createXRef(referenceManager
							.findReference((ProgramElementDoc) doc));
					term.appendChild(xref);

				} else {

					term.appendChild(doc.name());
				}

				Tag comment = DbdServices
						.findComment("@deprecated", doc.tags());

				if (comment != null) {
					htmlDocBookTrafo.transform(comment, para);
				}
			}
		}

		if (hasChild == true) {
			parent.appendChild(component);
		}
	}

	protected void writeFile(DocBookDocument doc, File fileName)
			throws IOException {

		PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(fileName),
						script.getDestinationEncoding()));
		doc.setXmlEncoding(script.getDestinationEncoding());
		writer.println(new NodeSerializer().toXML(doc));
		writer.close();
	}

	protected void writeOverview(RootDoc doc, DocBookElement parent)
			throws DocletException {

		if (doc.getRawCommentText() != null) {

			String overviewTitle = script.getOverviewTitle();
			logger.debug("Overview title='" + overviewTitle + "'.");

			DocBookElement section = null;

			if (this instanceof ArticleManager) {
				section = tagFactory.createArticle();
			} else {
				section = tagFactory.createBook();
			}

			context.setComment("Overview");
			context.setContextName("Overview");

			context.isOverview(true);
			htmlDocBookTrafo.transform(doc, section);
			context.isOverview(false);

			for (Element element : section.getChildElementList()) {

				DocBookElement dbElement = (DocBookElement) element;

				if (parent instanceof Book
						&& dbElement.isValidParent(parent) == false) {

					Chapter chapter = tagFactory.createChapter("???");
					parent.appendChild(chapter);
					chapter.appendChild(element);

				} else {
					parent.appendChild(element);
				}
			}

			Chapter lastChapter = (Chapter) parent.getLastChild(Chapter.class);

			if (lastChapter != null) {

				Sect1 lastSect1 = (Sect1) lastChapter.getLastChild(Sect1.class);
				logger.debug("Last sect1: " + lastSect1);

				if (lastSect1 == null) {

					style.addMetaInfo(doc, lastChapter);

				} else {

					Sect1 sect1 = tagFactory.createSect1(ResourceServices
							.getString(res, "C_ADDITIONAL_INFORMATION"));
					lastChapter.appendChild(sect1);

					style.addMetaInfo(doc, sect1);
				}

			}

			if (section instanceof Chapter) {

				Chapter chapter = (Chapter) section;

				if (chapter.hasContentChildren() == false) {

					logger.debug("Last chapter has children = false.");
					parent.removeChild(chapter);

				} else {

					logger.debug("Last chapter has children = true.");
					chapter.setTitle(overviewTitle);
				}
			}
		}

		context.isOverview(false);
	}

	protected void writeStatistics(
			TreeMap<String, TreeMap<String, ClassDoc>> pkgMap,
			DocBookElement parent) throws IOException {

		DocBookElement component;

		if (parent instanceof Article) {

			component = tagFactory.createSection();

		} else {

			if (script.isCreateAppendixEnabled() == false) {
				component = tagFactory.createChapter();
			} else {
				component = tagFactory.createAppendix();
			}
		}

		component.appendChild(tagFactory.createTitle(ResourceServices
				.getString(res, "C_STATISTICS")));
		parent.appendChild(component);

		Section section;
		InformalTable table;

		section = tagFactory.createSection(ResourceServices.getString(res,
				"C_TOTALS"));
		component.appendChild(section);

		TotalsDiagram totals = statisticData.getTotalsDiagram();

		totals.createDiagram();
		section.appendChild(tagFactory.createImage(totals.getImageHref(),
				totals.getImageWidth(), totals.getImageHeight()));

		table = totals.createTable(ResourceServices.getString(res, "C_TOTALS"),
				totals.getItemList(), tagFactory);
		section.appendChild(table);

		section = tagFactory.createSection(ResourceServices.getString(res,
				"C_CLASSES_PER_PACKAGE"));
		component.appendChild(section);

		ClassesPerPackage classesPerPackage = statisticData
				.getClassesPerPackageDiagram();

		classesPerPackage.createDiagram();
		section.appendChild(tagFactory.createImage(
				classesPerPackage.getImageHref(),
				classesPerPackage.getImageWidth(),
				classesPerPackage.getImageHeight()));

		table = totals.createTable(
				ResourceServices.getString(res, "C_CLASSES_PER_PACKAGE"),
				classesPerPackage.getItemList(), tagFactory);
		section.appendChild(table);

		DirectKnownSubclasses directKnownSubclasses = statisticData
				.getDirectKnownSubclassesDiagram();

		if (directKnownSubclasses.isEmpty() == false) {

			section = tagFactory.createSection(ResourceServices.getString(res,
					"C_TOP_TEN")
					+ " - "
					+ ResourceServices.getString(res,
							"C_DIRECT_KNOWN_SUBCLASSES"));
			component.appendChild(section);

			directKnownSubclasses.createDiagram();
			section.appendChild(tagFactory.createImage(
					directKnownSubclasses.getImageHref(),
					directKnownSubclasses.getImageWidth(),
					directKnownSubclasses.getImageWidth()));

			table = totals.createTable(ResourceServices.getString(res,
					"C_DIRECT_KNOWN_SUBCLASSES"), directKnownSubclasses
					.getItemList(), tagFactory);
			section.appendChild(table);
		}
	}
}

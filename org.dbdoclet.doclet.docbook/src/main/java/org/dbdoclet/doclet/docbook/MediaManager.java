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

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.CDI;
import org.dbdoclet.doclet.ClassDiagramManager;
import org.dbdoclet.doclet.DeprecatedManager;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.ExecutableMemberInfo;
import org.dbdoclet.doclet.ReferenceManager;
import org.dbdoclet.doclet.StatisticData;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.doclet.doc.DocManager;
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
import org.dbdoclet.tag.docbook.Chapter;
import org.dbdoclet.tag.docbook.Copyright;
import org.dbdoclet.tag.docbook.Date;
import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.DocBookVersion;
import org.dbdoclet.tag.docbook.Email;
import org.dbdoclet.tag.docbook.Firstname;
import org.dbdoclet.tag.docbook.Holder;
import org.dbdoclet.tag.docbook.Imagedata;
import org.dbdoclet.tag.docbook.Imageobject;
import org.dbdoclet.tag.docbook.Informalfigure;
import org.dbdoclet.tag.docbook.Informaltable;
import org.dbdoclet.tag.docbook.Legalnotice;
import org.dbdoclet.tag.docbook.Link;
import org.dbdoclet.tag.docbook.Listitem;
import org.dbdoclet.tag.docbook.Mediaobject;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Personname;
import org.dbdoclet.tag.docbook.Releaseinfo;
import org.dbdoclet.tag.docbook.Sect1;
import org.dbdoclet.tag.docbook.Section;
import org.dbdoclet.tag.docbook.Simpara;
import org.dbdoclet.tag.docbook.Surname;
import org.dbdoclet.tag.docbook.Table;
import org.dbdoclet.tag.docbook.Tbody;
import org.dbdoclet.tag.docbook.Term;
import org.dbdoclet.tag.docbook.Tgroup;
import org.dbdoclet.tag.docbook.Title;
import org.dbdoclet.tag.docbook.Variablelist;
import org.dbdoclet.tag.docbook.Varlistentry;
import org.dbdoclet.tag.docbook.Year;
import org.dbdoclet.tag.html.Img;
import org.dbdoclet.xiphias.Hyphenation;
import org.dbdoclet.xiphias.ImageServices;
import org.dbdoclet.xiphias.NodeSerializer;
import org.dbdoclet.xiphias.XmlServices;
import org.dbdoclet.xiphias.dom.ProcessingInstructionImpl;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.source.doctree.DocTree;

public abstract class MediaManager {

	private static Log logger = LogFactory.getLog(MediaManager.class.getName());

	protected DbdScript script;
	protected DbdTransformer htmlDocBookTrafo;
	protected DocBookTagFactory tagFactory;
	protected Hyphenation hyphenation;
	protected ReferenceManager referenceManager;
	protected ResourceBundle res;
	protected StatisticData statisticData;
	protected Style style;
	protected ClassDiagramManager classDiagramManager;
	protected TagManager tagManager;
	protected DocManager docManager;

	protected void createAdditionalSections(DocBookElement parent)
			throws IOException, DocletException {

		if (script.isCreateConstantValuesEnabled()) {
			writeConstantFieldValues(docManager.getSpecifiedElements(), parent);
		}

		if (script.isCreateDeprecatedListEnabled()) {
			// TODO Migration
			// writeDeprecatedList(docManager.getSpecifiedElements(), parent);
		}

		if (script.isCreateStatisticsEnabled()) {

			logger.info(ResourceServices.getString(res,
					"C_CONSTRUCTING_STATISTICS"));
			writeStatistics(docManager.getSpecifiedElements(), parent);
		}

		if (script.isAddIndexEnabled()) {
			parent.appendChild(tagFactory.createIndex());
		}
	}

	private TreeMap<PackageElement, TreeMap<String, TypeElement>> createClassMap(
			Set<PackageElement> pkgDocs, Set<TypeElement> classDocs) {

		if (pkgDocs == null) {
			throw new IllegalArgumentException(
					"The argument pkgDocs must not be null!");
		}

		if (classDocs == null) {
			throw new IllegalArgumentException(
					"The argument classDocs must not be null!");
		}

		TreeMap<PackageElement, TreeMap<String, TypeElement>> pkgMap = new TreeMap<PackageElement, TreeMap<String, TypeElement>>(
				new Comparator<PackageElement>() {
					@Override
					public int compare(PackageElement o1, PackageElement o2) {
						return o1.getQualifiedName().toString().compareTo(o2.getQualifiedName().toString());
					}
				});

		TreeMap<String, TypeElement> classMap;


		for (PackageElement pkgDoc : pkgDocs) {

			classMap = pkgMap.get(pkgDoc);

			if (classMap == null) {
				classMap = new TreeMap<String, TypeElement>();
				pkgMap.put(pkgDoc, classMap);
			}

			Set<TypeElement> docs = docManager.getTypeElements(pkgDoc);

			for (TypeElement elem : docs) {
				classMap.put(elem.getQualifiedName().toString(), elem);
			}
		}

		for (TypeElement classDoc : classDocs) {

			PackageElement pkgDoc = docManager.containingPackage(classDoc);
			classMap = pkgMap.get(pkgDoc);

			if (classMap == null) {
				classMap = new TreeMap<String, TypeElement>();
				pkgMap.put(pkgDoc, classMap);
			}

			classMap.put(classDoc.getQualifiedName().toString(), classDoc);
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
						Firstname firstName = tagFactory
								.createFirstname(authorFirstname);
						personname.appendChild(firstName);
					}

					if (isDefined(authorSurname)) {
						Surname surname = tagFactory
								.createSurname(authorSurname);
						personname.appendChild(surname);
					}
				}

			} else {

				if (isDefined(authorFirstname)) {
					Firstname firstName = tagFactory
							.createFirstname(authorFirstname);
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

			Legalnotice legalNotice = tagFactory.createLegalnotice();
			parent.appendChild(legalNotice);

			Simpara para = tagFactory.createSimpara(corporation + ". "
					+ ResourceServices.getString(res, "C_ALL_RIGHTS_RESERVED"));
			legalNotice.appendChild(para);
		}

		if (releaseInfo != null && releaseInfo.length() > 0) {
			Releaseinfo child = tagFactory.createReleaseinfo(releaseInfo);
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

				Mediaobject mediaObject = tagFactory.createMediaobject();
				Imageobject imageObject = tagFactory.createImageobject();
				Imagedata imageData = tagFactory.createImagedata();

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

		if (summary.getTextContent().trim().length() > 0) {
			Abstract abstractElement = tagFactory.createAbstract();
			parent.appendChild(abstractElement);
			abstractElement.appendChild(summary);
		}
	}

	protected void createInheritanceDiagram(ClassDoc classDoc,
			DocBookElement parent) throws DocletException {

		DocBookElement media;

		String filebase = classDiagramManager.createClassDiagram(classDoc,
				script.getDestinationDirectory());

		logger.debug("filebase ='" + filebase + "'");

		Informalfigure figure = tagFactory.createInformalfigure();
		media = tagFactory.createMediaobject();
		media.setParentNode(figure);
		figure.appendChild(media);

		try {

			String fileName = filebase + ".png";
			File file = new File(fileName);

			Img img = new Img();
			img.setAttribute("align", "center");
			img.setAttribute("src", fileName);

			tagFactory.createHtmlImageData(media, tagFactory, img, file, null);

			fileName = filebase + ".svg";
			file = new File(fileName);

			img = new Img();
			img.setAttribute("align", "center");
			img.setAttribute("src", fileName);

			tagFactory.createFoImageData(media, tagFactory, img, file, null);

		} catch (IOException oops) {
			throw new DocletException(oops);
		}

		parent.appendChild(figure);
	}

	protected void createModuleFile(TypeElement classDoc, DocBookElement parent,
			DocBookElement moduleElement) throws IOException {

		File destPath = script.getDestinationDirectory();
		String fileName = StringServices.replace(classDoc.getQualifiedName().toString(), ".",
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

	protected void createSynopsisSection(TypeElement typeElem,
			DocBookElement parent) throws DocletException {

		if (script.isCreateSynopsisEnabled() == true) {
			style.addClassSynopsis(typeElem, parent);
		}

		/*
		style.addMetaInfo(typeElem, parent);

		if (script.isCreateClassDiagramEnabled()) {
			createInheritanceDiagram(typeElem, parent);
		}
		*/
	}

	protected String getClassTypeAsText(TypeElement doc) {

		if (doc == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		String typeName = ResourceServices.getString(res, "C_CLASS");

		if (docManager.isAnnotationType(doc)) {

			typeName = ResourceServices.getString(res, "C_ANNOTATION_TYPE");

		} else if (docManager.isInterface(doc)) {

			typeName = ResourceServices.getString(res, "C_INTERFACE");

		} else if (docManager.isException(doc)) {

			typeName = ResourceServices.getString(res, "C_EXCEPTION");

		} else if (docManager.isError(doc)) {

			typeName = ResourceServices.getString(res, "C_ERROR");
		}

		return typeName;
	}

	public Hyphenation getHyphenation() {
		return hyphenation;
	}

	protected String getIndexCategory(Element doc) {

		String indexCategory = "Classes";

		if (doc.getKind() == ElementKind.ANNOTATION_TYPE) {
			indexCategory = "Annotations";
		} else if (doc.getKind() == ElementKind.INTERFACE) {
			indexCategory = "Interfaces";
		}

		return indexCategory;
	}

	protected String getReference(Element elem) {

		String key;

		if (elem.getKind() == ElementKind.PACKAGE ) {

			key = elem.getSimpleName().toString();
			return referenceManager.getId(key);
		}

		if (elem.getKind() == ElementKind.CLASS) {

			TypeElement typeElem = (TypeElement) elem;
			key = typeElem.getQualifiedName().toString();
			return referenceManager.getId(key);
		}

		// TODO
		/*
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
		*/
		
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

		String title = script.getTitle();

		if (title == null) {
			title = docManager.getSpecifiedElements().stream()
					.filter(PackageElement.class::isInstance)
					.map(PackageElement.class::cast)
					.map(PackageElement::getQualifiedName)
					.map(Name::toString)
					.findFirst().orElse("JavaDoc");
		}

		return tagFactory.createTitle(title);
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

	private boolean isDefined(String value) {

		if (value == null || value.trim().length() == 0) {
			return false;
		}

		return true;
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

	protected abstract void process() throws DocletException;

	public void setHtmlDocBookTrafo(DbdTransformer transformer) {
		this.htmlDocBookTrafo = transformer;
	}

	public void setHyphenation(Hyphenation hyphenation) {
		this.hyphenation = hyphenation;
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
			Set<? extends Element> specifiedElements,
			DocBookElement parent) throws IOException {

		TreeMap<String, TreeMap<String, TreeMap<String, VariableElement>>> pkgFieldMap = tagManager
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

			VariableElement fieldDoc;
			Object value;
			Section section;
			String fieldValue;
			String pkgName;
			String qualifiedTypeName;
			TreeMap<String, VariableElement> fieldMap;
			TreeMap<String, TreeMap<String, VariableElement>> classMap;
			TypeMirror type;

			for (Iterator<String> pkgIterator = pkgFieldMap.keySet().iterator(); pkgIterator
					.hasNext();) {

				pkgName = pkgIterator.next();
				section = tagFactory.createSection(
						ResourceServices.getString(res, "C_PACKAGE") + " " +
						pkgName + ".*");

				classMap = pkgFieldMap.get(pkgName);

				if (classMap.size() > 0) {
					component.appendChild(section);
					hasChild = true;
				}

				for (var className : classMap.keySet()) {

					Table table = tagFactory.createTable();
					table.setFrame("all");
					table.appendChild(tagFactory.createTitle(className));
					section.appendChild(table);

					Tgroup tgroup = tagFactory.createTgroup(2);
					table.appendChild(tgroup);

					Tbody tbody = tagFactory.createTbody();
					tgroup.appendChild(tbody);

					fieldMap = classMap.get(className);

					for (var fieldName : fieldMap.keySet()) {

						fieldDoc = fieldMap.get(fieldName);

						value = fieldDoc.getConstantValue();
						type = fieldDoc.asType();
						qualifiedTypeName = docManager.getQualifiedName(type);
						
						if (value != null) {

							fieldValue = value.toString();
							fieldValue = XmlServices.textToXml(fieldValue);

							if (qualifiedTypeName.equals("java.lang.String")) {
								fieldValue = "\"" + fieldValue + "\"";
							}

							tbody.addRow(docManager.getName(fieldDoc), fieldValue);

						} else {
							tbody.addRow(docManager.getName(fieldDoc), "null");
						}
					}
				}
			}

			if (hasChild == true) {
				parent.appendChild(component);
			}
		}
	}

	public void writeContents() throws DocletException {

		try {
			
			logger.info(ResourceServices.getString(res,
					"C_CONSTRUCTING_REFERENCE_MAP") + "...");

			/*
			referenceManager.init(script.getDocumentationId(), pkgMap,
					script.getIdStyle());

			statisticData.init(pkgMap);

			logger.info(ResourceServices.getString(res,
					"C_CONSTRUCTING_TAG_MAP") + "...");

			tagManager.createTagMap(pkgMap);
			*/
			
			process();

		} catch (Exception oops) {

			if (oops instanceof DocletException) {
				throw (DocletException) oops;
			} else {
				throw new DocletException(oops);
			}
		}
	}

	/* @formatter:off
	protected void writeDeprecatedList(
			Set<? extends Element> set,
			DocBookElement parent) throws IOException, DocletException {

		boolean hasChild = true;

		DocBookElement component;
		ArrayList<DocTree> docList = docManager.createDeprecatedList();

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

			Variablelist list = tagFactory.createVariablelist();
			list.appendChild(new ProcessingInstructionImpl("dbfo",
					"list-presentation=\"block\""));
			section.appendChild(list);

			for (Doc doc : docList) {

				Varlistentry varListEntry = tagFactory.createVarlistentry();
				list.appendChild(varListEntry);

				Term term = tagFactory.createTerm();
				varListEntry.appendChild(term);

				Listitem listItem = tagFactory.createListitem();
				varListEntry.appendChild(listItem);

				Para para = tagFactory.createPara();
				listItem.appendChild(para);

				if (doc instanceof ExecutableElement) {

					Link link = tagFactory.createLink(doc.toString(),
							referenceManager
									.findReference(doc));
					term.appendChild(link);

				} else {

					term.appendChild(doc.name());
				}

				Tag comment = DbdServices
						.findComment(doc.tags(), "@deprecated");

				if (comment != null) {
					// TODO Migration
					// htmlDocBookTrafo.transform(comment, para);
				}
			}
		}

		if (hasChild == true) {
			parent.appendChild(component);
		}
	}
	@formatter:on
	*/
	
	protected void writeFile(DocBookDocument doc, File fileName)
			throws IOException {

		File parentDir = fileName.getParentFile();

		if (parentDir != null && parentDir.exists() == false) {
			FileServices.createParentDir(fileName);
		}

		PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(fileName),
						script.getDestinationEncoding()));
		doc.setXmlEncoding(script.getDestinationEncoding());
		writer.println(new NodeSerializer().toXML(doc));
		writer.close();
	}

	protected void writeOverview(DocBookElement parent)
			throws DocletException {

		List<? extends DocTree> overviewDocTrees = docManager.getOverviewComment();
		if (nonNull(overviewDocTrees) && !overviewDocTrees.isEmpty()) {

			String overviewTitle = script.getOverviewTitle();
			logger.debug("Overview title='" + overviewTitle + "'.");

			DocBookElement section = null;

			if (this instanceof ArticleManager) {
				section = tagFactory.createArticle();
			} else {
				section = tagFactory.createBook();
			}

			htmlDocBookTrafo.transform(overviewDocTrees, section);

			for (org.w3c.dom.Element element : section.getChildElementList()) {

				DocBookElement dbElement = (DocBookElement) element;

				// TODO
				/*
				if (parent instanceof Book
						&& dbElement.isValidParent(new TransformPosition(doc), parent) == false) {

					Chapter chapter = tagFactory.createChapter("???");
					parent.appendChild(chapter);
					chapter.appendChild(element);

				} else {
					parent.appendChild(element);
				}
				*/
			}

			Chapter lastChapter = (Chapter) parent.getLastChild(Chapter.class);

			if (lastChapter != null) {

				Sect1 lastSect1 = (Sect1) lastChapter.getLastChild(Sect1.class);
				logger.debug("Last sect1: " + lastSect1);

				if (lastSect1 == null) {

					// TODO
					// style.addMetaInfo(doc, lastChapter);

				} else {

					Sect1 sect1 = tagFactory.createSect1(ResourceServices
							.getString(res, "C_ADDITIONAL_INFORMATION"));
					lastChapter.appendChild(sect1);

					// TODO
					// style.addMetaInfo(doc, sect1);
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
	}

	protected void writeStatistics(
			Set<? extends Element> set,
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
		Informaltable table;

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

	protected boolean hasVisibleContent(ExecutableMemberInfo memberInfo) {

		ExecutableElement memberDoc = memberInfo.getExecutableMember();
		ExecutableElement implementedDoc = memberInfo.getImplemented();

		if (memberDoc != null) {

			String memberComment = docManager.getCommentText(memberDoc);
			String implementedComment = docManager.getCommentText(memberDoc);
			if (memberComment != null
					&& memberComment.trim().length() > 0) {
				return true;
			} else if (implementedDoc != null
					&& implementedComment != null
					&& implementedComment.trim().length() > 0) {
				return true;
			}

			if (script.isCreateParameterInfoEnabled()
					&& docManager.getParamTags(memberDoc).size() > 0) {
				return true;
			}

			if (script.isCreateDeprecatedInfoEnabled()
					&& tagManager.findDeprecatedTag(memberDoc) != null) {
				return true;
			}

			// TODO
			/*
			if (script.isCreateMetaInfoEnabled()
					&& script.isCreateAuthorInfoEnabled()
					&& DbdServices.findComment(memberDoc.tags(), "@author") != null) {
				return true;
			}

			if (script.isCreateExceptionInfoEnabled()
					&& DbdServices.findComment(memberDoc.tags(), "@exception",
							"@throws") != null) {
				return true;
			}

			if (script.isCreateSeeAlsoInfoEnabled()
					&& DbdServices.findComment(memberDoc.tags(), "@see") != null) {
				return true;
			}

			if (script.isCreateSerialFieldInfoEnabled()
					&& DbdServices.findComment(memberDoc.tags(), "@serial",
							"@serialField", "@serialData") != null) {
				return true;
			}

			if (script.isCreateMetaInfoEnabled()
					&& script.isCreateSinceInfoEnabled()
					&& DbdServices.findComment(memberDoc.tags(), "@since") != null) {
				return true;
			}

			if (script.isCreateMetaInfoEnabled()
					&& script.isCreateVersionInfoEnabled()
					&& DbdServices.findComment(memberDoc.tags(), "@version") != null) {
				return true;
			}
			*/
		}

		return false;
	}

	public DocManager getDocManager() {
		return docManager;
	}

	public void setDocManager(DocManager docManager) {
		this.docManager = docManager;
	}

	public void setClassDiagramManager(ClassDiagramManager classDiagramManager) {
		this.classDiagramManager = classDiagramManager;
	}
}

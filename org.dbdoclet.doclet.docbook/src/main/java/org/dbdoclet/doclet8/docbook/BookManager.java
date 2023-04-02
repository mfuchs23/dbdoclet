/*
 * ### Copyright (C) 2006, 2012 Michael Fuchs ###
 * ### All Rights Reserved.                  ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet8.docbook;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet8.DocletException;
import org.dbdoclet.doclet8.ExecutableMemberInfo;
import org.dbdoclet.doclet8.util.MethodServices;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.docbook.Book;
import org.dbdoclet.tag.docbook.BookInfo;
import org.dbdoclet.tag.docbook.Bridgehead;
import org.dbdoclet.tag.docbook.Chapter;
import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Info;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Part;
import org.dbdoclet.tag.docbook.Partinfo;
import org.dbdoclet.tag.docbook.Sect1;
import org.dbdoclet.tag.docbook.Sect2;
import org.dbdoclet.tag.docbook.Title;
import org.dbdoclet.xiphias.XmlServices;
import org.dbdoclet.xiphias.dom.NodeImpl;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class BookManager extends MediaManager {

	private static Log logger = LogFactory.getLog(BookManager.class);

	public BookManager() {
		super();
	}

	@Override
	protected void process(RootDoc rootDoc) throws DocletException {

		try {

			logger.debug("process");

			DocBookElement parent;
			Chapter chapter;

			DocBookDocument doc = new DocBookDocument();

			String rootTagName = script.getDocumentElement();

			if (script.hasProlog()) {

				if (isDocBook5() == false) {
					DbdServices.appendDoctype(doc, rootTagName);
				}
			}

			String buffer = script.getAbstract();
			Para summary = tagFactory.createPara();

			if (buffer == null || buffer.length() == 0) {
				summary.appendChild("");
			} else {
				summary.appendChild(buffer);
			}

			if (rootTagName.equalsIgnoreCase("part")) {

				Part part = tagFactory.createPart();
				part.setLang(script.getLanguage());

				part.appendChild(getTitle());

				if (isDocBook5()) {

					Info info = tagFactory.createInfo();
					part.appendChild(info);
					createInfoSection(info, summary);

				} else {

					Partinfo partInfo = tagFactory.createPartinfo();
					part.appendChild(partInfo);
					createInfoSection(partInfo, summary);
				}

				doc.setDocumentElement(part);
				parent = part;

			} else {

				Book book = tagFactory.createBook();
				book.setLang(script.getLanguage());

				book.appendChild(getTitle());

				if (isDocBook5()) {

					Info info = tagFactory.createInfo();
					book.appendChild(info);
					createInfoSection(info, summary);

				} else {

					BookInfo bookInfo = tagFactory.createBookInfo();
					book.appendChild(bookInfo);
					createInfoSection(bookInfo, summary);
				}

				doc.setDocumentElement(book);
				parent = book;
			}

			if (isDocBook5()) {
				DbdServices.addNamespace(parent);
			}

			writeOverview(rootDoc, parent);

			String pkgName;
			String className;
			String title;
			
			TreeMap<String, ClassDoc> classMap;

			PackageDoc pkgDoc;
			ClassDoc classDoc;

			for (Iterator<String> pkgIterator = pkgMap.keySet().iterator(); pkgIterator
					.hasNext();) {

				pkgName = pkgIterator.next();

				/* Default package */
				if (pkgName.length() == 0) {
				
					title = "Default Package";
					pkgDoc = null;
					
				} else {

					pkgDoc = rootDoc.packageNamed(pkgName);

					if (pkgDoc == null) {
						continue;
					}
					
					title = pkgDoc.name();
				}
				
				logger.info(MessageFormat.format(
						ResourceServices.getString(res, "C_PROCESSING_PACKAGE"),
						title));


				chapter = tagFactory.createChapter();
				chapter.setId(getReference(pkgDoc));

				if (script.setCreateXrefLabelEnabled()) {
					chapter.setXrefLabel(XmlServices.textToXml(title));
				}

				chapter.appendChild(tagFactory.createTitle(
						// ResourceServices.getString(res, "C_PACKAGE") + " " + 
						hyphenation.hyphenateAfter(title, "\\.")));

				if (pkgDoc != null) {
					htmlDocBookTrafo.transform(pkgDoc, chapter);
				}
				
				Sect1 section = tagFactory.createSect1(ResourceServices
						.getString(res, "C_ADDITIONAL_INFORMATION"));

				if (pkgDoc != null && style.addMetaInfo(pkgDoc, section)) {
					chapter.appendChild(section);
				}

				classMap = pkgMap.get(pkgName);

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					classDoc = classMap.get(className);

					script.addContext(classDoc.qualifiedTypeName());
					writeClass(chapter, classDoc);
					script.removeContext(classDoc.qualifiedTypeName());
				}

				parent.appendChild(chapter);
			}

			createAdditionalSections(parent);
			writeFile(doc, script.getDestinationFile());

		} catch (DocletException oops) {

			throw oops;

		} catch (Exception oops) {

			throw new DocletException(oops);
		}
	}

	private void writeClass(Chapter chapter, ClassDoc classDoc)
			throws DocletException {

		try {

			Sect1 sect1;
			Sect2 sect2;

			// String prefix = getClassTypeAsText(classDoc);
			String indexCategory = getIndexCategory(classDoc);

			sect1 = tagFactory.createSect1();
			sect1.setId(getReference(classDoc));

			if (script.setCreateXrefLabelEnabled()) {
				sect1.setXrefLabel(XmlServices.textToXml(classDoc
						.qualifiedName()));
			}

			sect1.appendChild(
					tagFactory.createTitle(hyphenation.hyphenateCamelCase(classDoc.name())))
					.appendChild(
							tagFactory.createIndexterm().appendChild(
									tagFactory.createPrimary(classDoc.name())))
					.appendChild(
							tagFactory
									.createIndexterm()
									.appendChild(
											tagFactory
													.createPrimary(indexCategory))
									.appendChild(
											tagFactory.createSecondary(classDoc
													.name())));

			htmlDocBookTrafo.transform(classDoc, sect1);

			if (sect1.hasContentChildren() == true) {

				sect2 = tagFactory.createSect2();
				sect2.appendChild(tagFactory.createTitle(ResourceServices
						.getString(res, "C_SYNOPSIS")));

				createSynopsisSection(classDoc, sect2);

				if (sect2.hasContentChildren() == true) {
					sect1.appendChild(sect2);
				}

			} else {

				createSynopsisSection(classDoc, sect1);
			}

			if (script.isCreateMethodInfoEnabled()) {

				writeExecutableMembers(classDoc, classDoc.constructors(),
						sect1, ResourceServices.getString(res, "C_CONSTRUCTOR")
								+ " ");
			}

			if (script.isCreateFieldInfoEnabled()) {

				FieldDoc[] fields;

				if (classDoc.isEnum()) {
					fields = classDoc.enumConstants();
				} else {
					fields = classDoc.fields();
				}

				writeFields(classDoc, fields, sect1,
						ResourceServices.getString(res, "C_FIELD") + " ");
			}

			if (script.isCreateMethodInfoEnabled()) {

				writeExecutableMembers(classDoc, classDoc.methods(), sect1,
						ResourceServices.getString(res, "C_METHOD") + " ");
			}

			if (script.isCreateMethodInfoEnabled()
					&& classDoc.isAnnotationType()) {

				AnnotationTypeDoc atdoc = (AnnotationTypeDoc) classDoc;
				writeExecutableMembers(atdoc, atdoc.elements(), sect1,
						ResourceServices.getString(res, "C_ELEMENT") + " ");
			}

			if (script.isChunkDocBookEnabled() == false) {
				chapter.appendChild(sect1);
			} else {
				createModuleFile(classDoc, chapter, sect1);
			}

		} catch (DocletException oops) {

			oops.printStackTrace();
			throw oops;

		} catch (Exception oops) {

			throw new DocletException(oops);
		}
	}

	private boolean writeExecutableMembers(ClassDoc classDoc,
			ExecutableMemberDoc[] members, NodeImpl parent, String prefix)
			throws DocletException {

		Arrays.sort(members);

		boolean hasCommentedMembers = false;

		Sect2 section;
		ArrayList<ExecutableMemberInfo> commentedMembers = new ArrayList<ExecutableMemberInfo>();
		Iterator<ExecutableMemberInfo> iterator;
		MethodDoc implementedDoc = null;
		ExecutableMemberDoc memberDoc;
		ExecutableMemberInfo memberInfo;

		String indexCategory = "Methods";

		if ((members != null) && (members.length > 0)) {

			for (int i = 0; i < members.length; i++) {

				memberInfo = new ExecutableMemberInfo(members[i]);

				if (members[i] instanceof MethodDoc) {

					implementedDoc = MethodServices
							.implementedMethod((MethodDoc) members[i]);
					memberInfo.setImplemented(implementedDoc);
				}

				if (hasVisibleContent(memberInfo)) {
					commentedMembers.add(memberInfo);
				}
			}

			iterator = commentedMembers.iterator();

			while (iterator.hasNext()) {

				hasCommentedMembers = true;

				memberInfo = iterator.next();
				memberDoc = memberInfo.getExecutableMember();
				implementedDoc = memberInfo.getImplemented();

				section = tagFactory.createSect2();
				section.setId(getReference(memberDoc));

				Title title = tagFactory.createTitle();
				section.appendChild(title);

				if (memberDoc.isAnnotationTypeElement()) {

					indexCategory = "Elements";

					if (script.setCreateXrefLabelEnabled()) {
						section.setXrefLabel(XmlServices.textToXml(memberDoc
								.name()));
					}

					title.appendChild(memberDoc.name());

				} else {

					indexCategory = "Methods";

					if (script.setCreateXrefLabelEnabled()) {
						section.setXrefLabel(XmlServices.textToXml(memberDoc
								.name() + memberDoc.signature()));
					}

					title.appendChild(memberDoc.name()
							+ memberDoc.flatSignature());
				}

				title.appendChild(tagFactory.createIndexterm().appendChild(
						tagFactory.createPrimary(XmlServices
								.textToXml(memberDoc.name()))));

				title.appendChild(tagFactory
						.createIndexterm()
						.appendChild(tagFactory.createPrimary(indexCategory))
						.appendChild(
								tagFactory.createSecondary(XmlServices
										.textToXml(memberDoc.name()))));

				ExecutableMemberDoc commentDoc = memberInfo.getCommentDoc();

				if (script.isCreateSynopsisEnabled() == true) {

					style.addMemberSynopsis(memberDoc, section);

					if (implementedDoc != null) {
						style.addMethodSpecifiedBy(implementedDoc, section);
					}
				}

				htmlDocBookTrafo.transform(commentDoc, section);

				if (script.isCreateParameterInfoEnabled() == true) {
					style.addParamInfo(commentDoc, section);
				}

				if (script.isCreateExceptionInfoEnabled() == true) {
					style.addThrowsInfo(commentDoc, section);
				}

				if (script.isCreateMetaInfoEnabled()) {
					style.addMetaInfo(memberDoc, section);
				}

				if ((implementedDoc != null) && (commentDoc == implementedDoc)) {

					Bridgehead head = tagFactory.createBridgehead();
					head.setRenderAs("sect5");

					section.appendChild(head);

					String ref = referenceManager.findReference(implementedDoc);
					String headerTitle = ResourceServices.getString(res,
							"C_DESCRIPTION_COPIED_FROM_INTERFACE");

					if (ref == null) {

						head.appendChild(headerTitle + ": "
								+ implementedDoc.name());

					} else {

						head.appendChild(headerTitle + ": ");
						head.appendChild(tagFactory.createLink(
								implementedDoc.name(), ref));
					}
				}

				if (section.hasContentChildren()) {
					parent.appendChild(section);
				}
			}
		}

		return hasCommentedMembers;
	}

	private boolean writeFields(ClassDoc classDoc, FieldDoc[] fields,
			DocBookElement parent, String prefix) throws DocletException {

		Arrays.sort(fields);

		boolean hasCommentedFields = false;

		Sect2 section;
		String str;

		ArrayList<FieldDoc> commentedFields = new ArrayList<FieldDoc>();
		Iterator<FieldDoc> iterator;
		FieldDoc fieldDoc;

		if ((fields != null) && (fields.length > 0)) {

			for (int i = 0; i < fields.length; i++) {

				str = fields[i].getRawCommentText();

				if ((str != null) && !str.equals("")) {

					commentedFields.add(fields[i]);
				}
			}

			iterator = commentedFields.iterator();

			while (iterator.hasNext()) {

				hasCommentedFields = true;

				fieldDoc = iterator.next();

				section = tagFactory.createSect2();
				section.setId(getReference(fieldDoc));

				if (script.setCreateXrefLabelEnabled()) {
					section.setXrefLabel(XmlServices.textToXml(fieldDoc.name()));
				}

				section.appendChild(tagFactory.createTitle(fieldDoc.name()));
				section.appendChild(tagFactory.createIndexterm().appendChild(
						tagFactory.createPrimary(XmlServices.textToXml(fieldDoc
								.name()))));

				section.appendChild(tagFactory
						.createIndexterm()
						.appendChild(tagFactory.createPrimary("Fields"))
						.appendChild(
								tagFactory.createSecondary(XmlServices
										.textToXml(fieldDoc.name()))));

				if (script.isCreateSynopsisEnabled() == true) {

					style.addFieldSynopsis(fieldDoc, section);
				}

				if (script.isCreateSerialFieldInfoEnabled() == true) {

					style.addSerialFieldsInfo(fieldDoc, section);
				}

				if (script.isCreateMetaInfoEnabled() == true) {

					style.addMetaInfo(fieldDoc, section);
				}

				String comment = fieldDoc.commentText();

				if ((comment != null) && !comment.trim().equals("")) {

					htmlDocBookTrafo.transform(fieldDoc, section);
				}

				if (section.hasContentChildren()) {
					parent.appendChild(section);
				}
			}
		}

		return hasCommentedFields;
	}
}

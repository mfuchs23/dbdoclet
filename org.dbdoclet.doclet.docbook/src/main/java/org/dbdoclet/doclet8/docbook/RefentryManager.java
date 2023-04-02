/*
 * ### Copyright (C) 2005-2012 Michael Fuchs ###
 * ### All Rights Reserved.                  ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
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
import org.dbdoclet.tag.docbook.Date;
import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Info;
import org.dbdoclet.tag.docbook.Manvolnum;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Part;
import org.dbdoclet.tag.docbook.Partinfo;
import org.dbdoclet.tag.docbook.Partintro;
import org.dbdoclet.tag.docbook.Refentry;
import org.dbdoclet.tag.docbook.Refentryinfo;
import org.dbdoclet.tag.docbook.Reference;
import org.dbdoclet.tag.docbook.Refmeta;
import org.dbdoclet.tag.docbook.Refnamediv;
import org.dbdoclet.tag.docbook.Refpurpose;
import org.dbdoclet.tag.docbook.Refsect1;
import org.dbdoclet.tag.docbook.Refsect2;
import org.dbdoclet.tag.docbook.Refsynopsisdiv;
import org.dbdoclet.tag.docbook.Sect1;
import org.dbdoclet.xiphias.XmlServices;
import org.dbdoclet.xiphias.dom.NodeImpl;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class RefentryManager extends MediaManager {

	private static Log logger = LogFactory.getLog(RefentryManager.class
			.getName());

	public RefentryManager() {
		super();
	}

	@Override
	protected void process(RootDoc rootDoc) throws DocletException {

		try {

			DocBookElement parent;

			DocBookDocument doc = new DocBookDocument();
			String rootTagName = script.getDocumentElement();

			if (script.hasProlog()) {

				if (isDocBook5() == false) {
					DbdServices.appendDoctype(doc, "article");
				}
			}

			String buffer = script.getAbstract();
			Para summary = tagFactory.createPara();

			if (buffer == null || buffer.length() == 0) {
				summary.appendChild("");
				// DbdTransformer.transform(rootDoc.firstSentenceTags(),
				// summary, context);
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

			TreeMap<String, ClassDoc> classMap;
			String className;

			PackageDoc pkgDoc;
			ClassDoc classDoc;

			Reference reference;

			for (Iterator<String> pkgIterator = pkgMap.keySet().iterator(); pkgIterator
					.hasNext();) {

				pkgName = pkgIterator.next();

				logger.info(MessageFormat.format(
						ResourceServices.getString(res, "C_PROCESSING_PACKAGE"),
						pkgName));

				pkgDoc = rootDoc.packageNamed(pkgName);

				if (pkgDoc == null) {
					continue;
				}

				reference = tagFactory.createReference();
				parent.appendChild(reference);

				reference.setId(getReference(pkgDoc));

				if (script.setCreateXrefLabelEnabled()) {
					reference
							.setXrefLabel(XmlServices.textToXml(pkgDoc.name()));
				}

				reference.appendChild(tagFactory.createTitle(
						// ResourceServices.getString(res, "C_PACKAGE") + " " + 
						hyphenation.hyphenateAfter(pkgDoc.name(), "\\.")));

				Partintro partIntro = tagFactory.createPartintro();

				htmlDocBookTrafo.transform(pkgDoc, partIntro);

				Sect1 section = tagFactory.createSect1(ResourceServices
						.getString(res, "C_ADDITIONAL_INFORMATION"));

				if (style.addMetaInfo(pkgDoc, section)) {
					partIntro.appendChild(section);
				}

				if (partIntro.hasElementChildren()) {
					reference.appendChild(partIntro);
				}

				classMap = pkgMap.get(pkgName);

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					classDoc = classMap.get(className);

					script.addContext(classDoc.qualifiedTypeName());
					writeClass(reference, classDoc);
					script.removeContext(classDoc.qualifiedTypeName());
				}
			}

			createAdditionalSections(parent);
			writeFile(doc, script.getDestinationFile());

		} catch (DocletException oops) {

			throw oops;

		} catch (Exception oops) {

			throw new DocletException(oops);
		}
	}

	private void writeClass(Reference reference, ClassDoc classDoc)
			throws DocletException {

		if (reference == null) {
			throw new IllegalArgumentException(
					"The argument reference must not be null!");
		}

		if (classDoc == null) {
			throw new IllegalArgumentException(
					"The argument classDoc must not be null!");
		}

		try {

			String indexCategory = getIndexCategory(classDoc);
			// String prefix = getClassTypeAsText(classDoc);

			Refentry refEntry = tagFactory.createRefentry();

			refEntry.setId(getReference(classDoc));

			if (script.setCreateXrefLabelEnabled()) {
				refEntry.setXrefLabel(XmlServices.textToXml(classDoc
						.qualifiedName()));
			}

			refEntry.appendChild(tagFactory.createIndexterm()
					.appendChild(tagFactory.createPrimary(indexCategory))
					.appendChild(tagFactory.createSecondary(classDoc.name())));

			refEntry.appendChild(tagFactory.createIndexterm().appendChild(
					tagFactory.createPrimary(classDoc.name())));

			Refentryinfo refEntryInfo = tagFactory.createRefentryinfo();
			refEntry.appendChild(refEntryInfo);

			Date date = tagFactory.createDate();
			refEntryInfo.appendChild(date);

			Refmeta refMeta = tagFactory.createRefmeta();
			refEntry.appendChild(refMeta);

			Manvolnum manVolNum = tagFactory.createManvolnum("3");
			refMeta.appendChild(manVolNum);

			refMeta.appendChild(tagFactory.createRefmiscinfo("source",
					XmlServices.textToXml(classDoc.qualifiedName())));
			refMeta.appendChild(tagFactory.createRefmiscinfo("version",
					XmlServices.textToXml("1.0")));
			refMeta.appendChild(tagFactory.createRefmiscinfo("manual",
					XmlServices.textToXml("dbdoclet reference handbook")));

			Refnamediv refNameDiv = tagFactory.createRefnamediv();
			refEntry.appendChild(refNameDiv);

			refNameDiv.appendChild(tagFactory.createRefname(classDoc.name()));

			Refpurpose purpose = tagFactory.createRefpurpose();
			htmlDocBookTrafo.transform(classDoc.firstSentenceTags(), purpose);
			refNameDiv.appendChild(purpose);

			Refsynopsisdiv refSynopsisDiv = tagFactory.createRefsynopsisdiv();
			refEntry.appendChild(refSynopsisDiv);

			if (script.isCreateSynopsisEnabled() == true) {
				style.addClassSynopsis(classDoc, refSynopsisDiv);
			}

			Refsect1 refSect1;

			if (script.isCreateClassDiagramEnabled()) {

				refSect1 = tagFactory.createRefsect1(ResourceServices
						.getString(res, "C_INHERITANCE_PATH"));
				refEntry.appendChild(refSect1);

				createInheritanceDiagram(classDoc, refSect1);
			}

			refSect1 = tagFactory.createRefsect1(ResourceServices.getString(
					res, "C_DESCRIPTION"));
			htmlDocBookTrafo.transform(classDoc, refSect1);
			style.addMetaInfo(classDoc, refSect1);

			if (refSect1.getNumberOfChildNodes() > 1) {
				refEntry.appendChild(refSect1);
			}

			boolean wroteConstructors = false;
			boolean wroteMethods = false;
			boolean wroteFields = false;
			boolean wroteElements = false;

			if (script.isCreateMethodInfoEnabled()) {

				refSect1 = tagFactory.createRefsect1(ResourceServices
						.getString(res, "C_CONSTRUCTORS"));

				wroteConstructors = writeExecutableMembers(classDoc,
						classDoc.constructors(), refSect1,
						res.getString("C_CONSTRUCTOR") + " ");

				if (wroteConstructors) {
					refEntry.appendChild(refSect1);
				}

			}

			if (script.isCreateFieldInfoEnabled()) {

				refSect1 = tagFactory.createRefsect1(ResourceServices
						.getString(res, "C_FIELDS"));

				FieldDoc[] fields;

				if (classDoc.isEnum()) {
					fields = classDoc.enumConstants();
				} else {
					fields = classDoc.fields();
				}

				wroteFields = writeFields(classDoc, fields, refSect1,
						ResourceServices.getString(res, "C_FIELD") + " ");

				if (wroteFields) {
					refEntry.appendChild(refSect1);
				}

			}

			if (script.isCreateMethodInfoEnabled()) {

				refSect1 = tagFactory.createRefsect1(ResourceServices
						.getString(res, "C_METHODS"));

				wroteMethods = writeExecutableMembers(classDoc,
						classDoc.methods(), refSect1,
						ResourceServices.getString(res, "C_METHOD") + " ");

				if (wroteMethods) {
					refEntry.appendChild(refSect1);
				}
			}

			if (script.isCreateMethodInfoEnabled()
					&& classDoc.isAnnotationType()) {

				refSect1 = tagFactory.createRefsect1(ResourceServices
						.getString(res, "C_ELEMENTS"));

				AnnotationTypeDoc atdoc = (AnnotationTypeDoc) classDoc;

				wroteElements = writeExecutableMembers(atdoc, atdoc.elements(),
						refSect1, ResourceServices.getString(res, "C_ELEMENT")
								+ " ");
				if (wroteElements) {
					refEntry.appendChild(refSect1);
				}
			}

			if (script.isChunkDocBookEnabled() == false) {
				reference.appendChild(refEntry);
			} else {
				createModuleFile(classDoc, reference, refEntry);
			}

		} catch (DocletException oops) {
			throw (DocletException) oops.fillInStackTrace();
		} catch (Exception oops) {
			throw new DocletException(oops);
		}
	}

	private boolean writeExecutableMembers(ClassDoc classDoc,
			ExecutableMemberDoc[] members, NodeImpl parent, String prefix)
			throws DocletException {

		Arrays.sort(members);

		boolean hasCommentedMembers = false;

		Refsect2 section;

		ArrayList<ExecutableMemberInfo> commentedMembers = new ArrayList<ExecutableMemberInfo>();
		Iterator<ExecutableMemberInfo> iterator;
		MethodDoc implementedDoc = null;
		ExecutableMemberDoc memberDoc;
		ExecutableMemberInfo memberInfo;

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

			String indexCategory;

			while (iterator.hasNext()) {

				hasCommentedMembers = true;

				memberInfo = iterator.next();
				memberDoc = memberInfo.getExecutableMember();
				implementedDoc = memberInfo.getImplemented();

				section = tagFactory.createRefsect2();
				section.setId(getReference(memberDoc));

				if (memberDoc.isAnnotationTypeElement()) {

					indexCategory = "Elements";

					if (script.setCreateXrefLabelEnabled()) {
						section.setXrefLabel(XmlServices.textToXml(memberDoc
								.name()));
					}

					section.appendChild(tagFactory.createTitle(XmlServices
							.textToXml(memberDoc.name())));

				} else {

					indexCategory = "Methods";

					if (script.setCreateXrefLabelEnabled()) {
						section.setXrefLabel(XmlServices.textToXml(memberDoc
								.name() + memberDoc.signature()));
					}

					section.appendChild(tagFactory.createTitle(XmlServices
							.textToXml(memberDoc.name()
									+ memberDoc.flatSignature())));
				}

				section.appendChild(tagFactory.createIndexterm().appendChild(
						tagFactory.createPrimary(XmlServices
								.textToXml(memberDoc.name()))));

				section.appendChild(tagFactory
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

				logger.debug("Transforming executable member description.");
				htmlDocBookTrafo.transform(commentDoc, section);

				parent.appendChild(section);
			}
		}

		return hasCommentedMembers;
	}

	private boolean writeFields(ClassDoc classDoc, FieldDoc[] fields,
			DocBookElement parent, String prefix) throws DocletException {

		Arrays.sort(fields);

		boolean hasCommentedFields = false;

		Refsect2 section;
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

				section = tagFactory.createRefsect2();
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

				parent.appendChild(section);
			}
		}

		return hasCommentedFields;
	}
}

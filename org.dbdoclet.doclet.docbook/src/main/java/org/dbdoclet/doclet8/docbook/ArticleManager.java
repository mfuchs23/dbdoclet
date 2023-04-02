/*
 * $Id$
 *
 * ### Copyright (C) 2006 Michael Fuchs ###
 * ### All Rights Reserved.             ###
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
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.docbook.Article;
import org.dbdoclet.tag.docbook.ArticleInfo;
import org.dbdoclet.tag.docbook.Bridgehead;
import org.dbdoclet.tag.docbook.DocBookDocument;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Info;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Section;
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

public class ArticleManager extends MediaManager {

	static Log logger = LogFactory.getLog(ArticleManager.class);

	public ArticleManager() {
		super();
	}

	@Override
	protected void process(RootDoc rootDoc) throws DocletException {

		try {

			DocBookElement parent;
			Section sect1;

			DocBookDocument doc = new DocBookDocument();

			if (script.hasProlog()) {

				if (isDocBook5() == false) {
					DbdServices.appendDoctype(doc, "article");
				}
			}

			String buffer = script.getAbstract();
			Para summary = tagFactory.createPara();

			if (buffer == null || buffer.length() == 0) {
				summary.appendChild("");
			} else {
				summary.appendChild(buffer);
			}

			Article article = tagFactory.createArticle();
			article.setLang(script.getLanguage());

			article.appendChild(getTitle());

			if (isDocBook5()) {

				DbdServices.addNamespace(article);

				Info info = tagFactory.createInfo();
				article.appendChild(info);
				createInfoSection(info, summary);

			} else {

				ArticleInfo articleInfo = tagFactory.createArticleInfo();
				article.appendChild(articleInfo);
				createInfoSection(articleInfo, summary);
			}

			doc.setDocumentElement(article);
			parent = article;

			writeOverview(rootDoc, parent);

			String pkgName;

			TreeMap<String, ClassDoc> classMap;
			String className;

			PackageDoc pkgDoc;
			ClassDoc classDoc;

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

				sect1 = tagFactory.createSection();
				sect1.setId(getReference(pkgDoc));

				if (script.setCreateXrefLabelEnabled()) {
					sect1.setXrefLabel(XmlServices.textToXml(pkgDoc.name()));
				}

				sect1.appendChild(tagFactory.createTitle(
						// ResourceServices.getString(res, "C_PACKAGE") + " " + 
						hyphenation.hyphenateAfter(pkgDoc.name(), "\\.")));

				htmlDocBookTrafo.transform(pkgDoc, sect1);

				Section section = tagFactory.createSection(ResourceServices
						.getString(res, "C_ADDITIONAL_INFORMATION"));

				if (style.addMetaInfo(pkgDoc, section)) {
					sect1.appendChild(section);
				}

				classMap = pkgMap.get(pkgName);

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					classDoc = classMap.get(className);

					script.addContext(classDoc.qualifiedTypeName());
					writeClass(sect1, classDoc);
					script.removeContext(classDoc.qualifiedTypeName());
				}

				parent.appendChild(sect1);
			}

			createAdditionalSections(parent);
			writeFile(doc, script.getDestinationFile());

		} catch (DocletException oops) {

			throw oops;

		} catch (Exception oops) {

			throw new DocletException(oops);
		}
	}

	private void writeClass(Section sect1, ClassDoc classDoc)
			throws DocletException {

		try {

			Section sect2;
			Section sect3;

			DocBookElement parent;

			// String prefix = getClassTypeAsText(classDoc);
			String indexCategory = getIndexCategory(classDoc);

			sect2 = tagFactory.createSection();
			sect2.setId(getReference(classDoc));

			if (script.setCreateXrefLabelEnabled()) {
				sect2.setXrefLabel(XmlServices.textToXml(classDoc
						.qualifiedName()));
			}

			sect2.appendChild(
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

			htmlDocBookTrafo.transform(classDoc, sect2);

			if (sect2.hasContentChildren() == true) {

				sect3 = tagFactory.createSection();
				sect3.appendChild(tagFactory.createTitle(ResourceServices
						.getString(res, "C_SYNOPSIS")));
				sect2.appendChild(sect3);
				parent = sect3;

			} else {

				parent = sect2;
			}

			createSynopsisSection(classDoc, parent);

			parent = sect2;

			if (script.isCreateMethodInfoEnabled()) {

				writeExecutableMembers(classDoc, classDoc.constructors(),
						parent,
						ResourceServices.getString(res, "C_CONSTRUCTOR") + " ");
			}

			if (script.isCreateFieldInfoEnabled()) {

				FieldDoc[] fields;

				if (classDoc.isEnum()) {
					fields = classDoc.enumConstants();
				} else {
					fields = classDoc.fields();
				}

				writeFields(classDoc, fields, parent,
						ResourceServices.getString(res, "C_FIELD") + " ");
			}

			if (script.isCreateMethodInfoEnabled()) {

				writeExecutableMembers(classDoc, classDoc.methods(), parent,
						ResourceServices.getString(res, "C_METHOD") + " ");
			}

			if (script.isCreateMethodInfoEnabled()
					&& classDoc.isAnnotationType()) {

				AnnotationTypeDoc atdoc = (AnnotationTypeDoc) classDoc;
				writeExecutableMembers(atdoc, atdoc.elements(), parent,
						ResourceServices.getString(res, "C_ELEMENT") + " ");
			}

			if (script.isChunkDocBookEnabled() == false) {
				sect1.appendChild(sect2);
			} else {
				createModuleFile(classDoc, sect1, sect2);
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

		Section section;
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

					implementedDoc = ((MethodDoc)members[i]).overriddenMethod();
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

				section = tagFactory.createSection();
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

		Section section;
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

				section = tagFactory.createSection();
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

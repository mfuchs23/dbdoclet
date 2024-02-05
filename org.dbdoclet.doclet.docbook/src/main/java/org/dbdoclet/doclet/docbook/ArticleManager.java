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
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.ExecutableMemberInfo;
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

import com.sun.source.doctree.DocCommentTree;

public class ArticleManager extends MediaManager {

	static Log logger = LogFactory.getLog(ArticleManager.class);

	public ArticleManager() {
		super();
	}

	@Override
	protected void process() throws DocletException {

		try {

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

			writeOverview(article);

			for (Element specified : docManager.getSpecifiedElements()) {

				if (specified.getKind() == ElementKind.PACKAGE) {
					writePackage(article, (PackageElement) specified);
				}

				if (specified.getKind().isClass() || specified.getKind().isInterface()) {

					PackageElement pkgElem = docManager.containingPackage((TypeElement) specified);
					String pkgName = pkgElem.getQualifiedName().toString();

					Section sect1 = tagFactory.createSection();
					sect1.setId(getReference(pkgElem));

					if (script.setCreateXrefLabelEnabled()) {
						sect1.setXrefLabel(XmlServices.textToXml(pkgName));
					}

					sect1.appendChild(tagFactory.createTitle(ResourceServices.getString(res, "C_PACKAGE") + " "
							+ hyphenation.hyphenateAfter(pkgName, "\\.")));

					writeClass(sect1, pkgElem, (TypeElement) specified);
					article.appendChild(sect1);
				}
			}

			createAdditionalSections(article);
			writeFile(doc, script.getDestinationFile());

		} catch (DocletException oops) {
			throw oops;
		} catch (Exception oops) {
			throw new DocletException(oops);
		}
	}

	private void writePackage(DocBookElement parent, PackageElement pkgElem) throws DocletException {

		String pkgName = pkgElem.getQualifiedName().toString();

		logger.info(MessageFormat.format(ResourceServices.getString(res, "C_PROCESSING_PACKAGE"), pkgName));

		Section sect1 = tagFactory.createSection();
		sect1.setId(getReference(pkgElem));

		if (script.setCreateXrefLabelEnabled()) {
			sect1.setXrefLabel(XmlServices.textToXml(pkgName));
		}

		sect1.appendChild(tagFactory.createTitle(
				ResourceServices.getString(res, "C_PACKAGE") + " " + hyphenation.hyphenateAfter(pkgName, "\\.")));

		htmlDocBookTrafo.transform(pkgElem, sect1);

		Section section = tagFactory.createSection(ResourceServices.getString(res, "C_ADDITIONAL_INFORMATION"));

		if (style.addMetaInfo(pkgElem, section)) {
			sect1.appendChild(section);
		}

		for (Element elem : pkgElem.getEnclosedElements()) {

			if (elem.getKind().isClass() || elem.getKind().isInterface()) {
				TypeElement typeElem = (TypeElement) elem;
				script.addContext(typeElem.getQualifiedName().toString());
				writeClass(sect1, pkgElem, typeElem);
				script.removeContext(typeElem.getQualifiedName().toString());
			} else {
				docManager.getReporter().print(Diagnostic.Kind.ERROR,
						String.format("Unknown Element %s inside of package %s!", elem, pkgElem));
			}
		}

		parent.appendChild(sect1);
	}

	private void writeClass(Section sect1, PackageElement pkgElem, TypeElement classElem) throws DocletException {

		try {

			Section sect2;
			Section sect3;

			DocBookElement parent;

			String prefix = getClassTypeAsText(classElem);
			String indexCategory = getIndexCategory(classElem);

			sect2 = tagFactory.createSection();
			sect2.setId(getReference(classElem));

			if (script.setCreateXrefLabelEnabled()) {
				sect2.setXrefLabel(XmlServices.textToXml(classElem.getQualifiedName().toString()));
			}

			String className = classElem.getSimpleName().toString();

			sect2.appendChild(tagFactory.createTitle(hyphenation.hyphenateCamelCase(className)))
					.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary(className)))
					.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary(indexCategory))
							.appendChild(tagFactory.createSecondary(className)));

			htmlDocBookTrafo.transform(pkgElem, classElem, sect2);

			if (sect2.hasContentChildren() == true) {

				sect3 = tagFactory.createSection();
				sect3.appendChild(tagFactory.createTitle(ResourceServices.getString(res, "C_SYNOPSIS")));
				sect2.appendChild(sect3);
				parent = sect3;

			} else {

				parent = sect2;
			}

			createSynopsisSection(classElem, parent);

			parent = sect2;

			if (script.isCreateMethodInfoEnabled()) {
				writeExecutableMembers(classElem, docManager.getConstructorElements(classElem), parent,
						ResourceServices.getString(res, "C_CONSTRUCTOR") + " ");
			}

			if (script.isCreateFieldInfoEnabled()) {

				Set<VariableElement> fields;

				if (docManager.isEnum(classElem)) {
					fields = docManager.getFieldElements(classElem);
				} else {
					fields = docManager.getFieldElements(classElem);
				}

				writeFields(classElem, fields, parent, ResourceServices.getString(res, "C_FIELD") + " ");
			}

			if (script.isCreateMethodInfoEnabled()) {
				writeExecutableMembers(classElem, docManager.getMethodElements(classElem), parent,
						ResourceServices.getString(res, "C_METHOD") + " ");
			}

			if (script.isCreateMethodInfoEnabled() && docManager.isAnnotationType(classElem)) {

				Set<ExecutableElement> annotationElements = docManager.getAnnotationElements((TypeElement) classElem);
				writeExecutableMembers(classElem, annotationElements, parent,
						ResourceServices.getString(res, "C_ELEMENT") + " ");
			}

			if (script.isChunkDocBookEnabled() == false) {
				sect1.appendChild(sect2);
			} else {
				createModuleFile(classElem, sect1, sect2);
			}

		} catch (DocletException oops) {

			oops.printStackTrace();
			throw oops;

		} catch (Exception oops) {

			throw new DocletException(oops);
		}
	}

	private boolean writeExecutableMembers(TypeElement classDoc, Set<ExecutableElement> memberSet, NodeImpl parent,
			String prefix) throws DocletException {

		List<ExecutableElement> members = memberSet.stream()
				.sorted((o1, o2) -> o1.getSimpleName().toString().compareTo(o2.getSimpleName().toString()))
				.collect(Collectors.toList());

		boolean hasCommentedMembers = false;

		Section section;

		var commentedMembers = new ArrayList<ExecutableMemberInfo>();
		String indexCategory = "Methods";

		for (ExecutableElement member : members) {

			ExecutableMemberInfo memberInfo = new ExecutableMemberInfo(member);

			ExecutableElement implementedElem = docManager.implementedMethod(classDoc, member);
			memberInfo.setImplemented(implementedElem);

			ExecutableElement overriddenElem = docManager.overriddenMethod(member);
			memberInfo.setImplemented(overriddenElem);

			if (hasJavadocContent(memberInfo)) {
				commentedMembers.add(memberInfo);
			}
		}

		for (ExecutableMemberInfo memberInfo : commentedMembers) {

			hasCommentedMembers = true;

			ExecutableElement memberElem = memberInfo.getExecutableMember();
			ExecutableElement implementedDoc = memberInfo.getImplemented();

			section = tagFactory.createSection();
			section.setId(getReference(memberElem));

			Title title = tagFactory.createTitle();
			section.appendChild(title);

			String memberName = memberElem.getSimpleName().toString();
			if (docManager.isAnnotationType(memberElem)) {

				indexCategory = "Elements";

				if (script.setCreateXrefLabelEnabled()) {
					section.setXrefLabel(XmlServices.textToXml(memberName));
				}

				title.appendChild(memberName);

			} else {

				indexCategory = "Methods";

				if (script.setCreateXrefLabelEnabled()) {
					section.setXrefLabel(
							XmlServices.textToXml(memberName + docFormatter.createMethodSignature(memberElem)));
				}

				title.appendChild(memberName + docFormatter.createMethodFlatSignature(memberElem));
			}

			title.appendChild(tagFactory.createIndexterm()
					.appendChild(tagFactory.createPrimary(XmlServices.textToXml(memberName))));

			title.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary(indexCategory))
					.appendChild(tagFactory.createSecondary(XmlServices.textToXml(memberName))));

			ExecutableElement commentDoc = memberInfo.getCommentDoc();

			if (script.isCreateSynopsisEnabled() == true) {

				style.addMemberSynopsis(memberElem, section);

				if (implementedDoc != null) {
					style.addMethodSpecifiedBy(implementedDoc, section);
				}
			}

			DocCommentTree commentTree = docManager.getDocCommentTree(commentDoc);
			htmlDocBookTrafo.transform(docManager.getDocTreePath(commentDoc), section);

			if (script.isCreateParameterInfoEnabled() == true) {
				style.addParamInfo(commentDoc, section);
			}

			if (script.isCreateExceptionInfoEnabled() == true) {
				style.addThrowsInfo(commentDoc, section);
			}

			if (script.isCreateMetaInfoEnabled()) {
				style.addMetaInfo(memberElem, section);
			}

			if ((implementedDoc != null) && (commentDoc == implementedDoc)) {

				Bridgehead head = tagFactory.createBridgehead();
				head.setRenderAs("sect5");

				section.appendChild(head);

				String ref = referenceManager.findReference(implementedDoc);
				String headerTitle = ResourceServices.getString(res, "C_DESCRIPTION_COPIED_FROM_INTERFACE");

				if (ref == null) {

					head.appendChild(headerTitle + ": " + docManager.getName(implementedDoc));

				} else {

					head.appendChild(headerTitle + ": ");
					head.appendChild(tagFactory.createLink(docManager.getName(implementedDoc), ref));
				}
			}

			if (section.hasContentChildren()) {
				parent.appendChild(section);
			}
		}

		return hasCommentedMembers;
	}

	private boolean writeFields(TypeElement classDoc, Set<VariableElement> fields, DocBookElement parent, String prefix)
			throws DocletException {

		if (isNull(fields)) {
			return true;
		}

		boolean hasCommentedFields = false;

		ArrayList<VariableElement> commentedFields = new ArrayList<VariableElement>();

		for (VariableElement field : fields) {
			String comment = docManager.getCommentText(field);
			if (nonNull(comment) && !comment.isBlank()) {
				commentedFields.add(field);
			}
		}

		for (VariableElement field : commentedFields) {

			hasCommentedFields = true;

			Section section = tagFactory.createSection();
			section.setId(getReference(field));

			if (script.setCreateXrefLabelEnabled()) {
				section.setXrefLabel(XmlServices.textToXml(docManager.getName(field)));
			}

			String name = docManager.getName(field);
			section.appendChild(tagFactory.createTitle(name));
			section.appendChild(
					tagFactory.createIndexterm().appendChild(tagFactory.createPrimary(XmlServices.textToXml(name))));

			section.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary("Fields"))
					.appendChild(tagFactory.createSecondary(XmlServices.textToXml(name))));

			if (script.isCreateSynopsisEnabled() == true) {
				style.addFieldSynopsis(field, section);
			}

			if (script.isCreateSerialFieldInfoEnabled() == true) {
				style.addSerialFieldsInfo(field, section);
			}

			if (script.isCreateMetaInfoEnabled() == true) {
				style.addMetaInfo(field, section);
			}

			String comment = docManager.getCommentText(field);
			if (nonNull(comment) && !comment.isBlank()) {
				DocCommentTree commentTree = docManager.getDocCommentTree(field);
				htmlDocBookTrafo.transform(docManager.getDocTreePath(field), commentTree.getFullBody(), section);
			}

			if (section.hasContentChildren()) {
				parent.appendChild(section);
			}
		}

		return hasCommentedFields;
	}

}

/*
 * ### Copyright (C) 2006, 2012 Michael Fuchs ###
 * ### All Rights Reserved.                  ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

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
import javax.tools.Diagnostic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.doclet.ExecutableMemberInfo;
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
import org.dbdoclet.tag.docbook.Section;
import org.dbdoclet.tag.docbook.Title;
import org.dbdoclet.xiphias.XmlServices;
import org.dbdoclet.xiphias.dom.NodeImpl;

import com.sun.source.doctree.DocCommentTree;

public class BookManager extends MediaManager {

	private static Log logger = LogFactory.getLog(BookManager.class);

	public BookManager() {
		super();
	}

	@Override
	protected void process() throws DocletException {

		try {

			logger.debug("process");

			DocBookElement parent;
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

			writeOverview(parent);

			for (Element specified : docManager.getSpecifiedElements()) {

				if (specified.getKind() == ElementKind.PACKAGE) {
					writePackage(parent, (PackageElement) specified);
				}

				if (specified.getKind().isClass() || specified.getKind().isInterface()) {

					TypeElement classElement = (TypeElement) specified;
					PackageElement pkgElem = docManager.containingPackage((TypeElement) specified);
					String pkgName = pkgElem.getQualifiedName().toString();

					Chapter chapter = tagFactory.createChapter();
					chapter.setId(getReference(pkgElem));

					if (script.setCreateXrefLabelEnabled()) {
						chapter.setXrefLabel(XmlServices.textToXml(classElement.getQualifiedName().toString()));
					}

					chapter.appendChild(tagFactory.createTitle(ResourceServices.getString(res, "C_PACKAGE") + " "
							+ hyphenation.hyphenateAfter(pkgName, "\\.")));

					writeClass(chapter, pkgElem, (TypeElement) specified);
					parent.appendChild(chapter);
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

	private void writePackage(DocBookElement parent, PackageElement pkgElem) throws DocletException {

		String pkgName = pkgElem.getQualifiedName().toString();

		logger.info(MessageFormat.format(ResourceServices.getString(res, "C_PROCESSING_PACKAGE"), pkgName));

		Chapter chapter = tagFactory.createChapter();
		chapter.setId(getReference(pkgElem));

		if (script.setCreateXrefLabelEnabled()) {
			chapter.setXrefLabel(XmlServices.textToXml(pkgName));
		}

		chapter.appendChild(tagFactory.createTitle(
				ResourceServices.getString(res, "C_PACKAGE") + " " + hyphenation.hyphenateAfter(pkgName, "\\.")));

		htmlDocBookTrafo.transform(pkgElem, chapter);

		Section section = tagFactory.createSection(ResourceServices.getString(res, "C_ADDITIONAL_INFORMATION"));

		if (style.addMetaInfo(pkgElem, section)) {
			chapter.appendChild(section);
		}

		for (Element elem : pkgElem.getEnclosedElements()) {

			if (elem.getKind().isClass() || elem.getKind().isInterface()) {
				TypeElement typeElem = (TypeElement) elem;
				script.addContext(typeElem.getQualifiedName().toString());
				writeClass(chapter, pkgElem, typeElem);
				script.removeContext(typeElem.getQualifiedName().toString());
			} else {
				docManager.getReporter().print(Diagnostic.Kind.ERROR,
						String.format("Unknown Element %s inside of package %s!", elem, pkgElem));
			}
		}

		parent.appendChild(chapter);
	}

	private void writeClass(Chapter chapter, PackageElement pkgElem, TypeElement classElem) throws DocletException {

		try {

			Sect1 sect1;
			Sect2 sect2;

			String indexCategory = getIndexCategory(classElem);

			sect1 = tagFactory.createSect1();
			sect1.setId(getReference(classElem));

			if (script.setCreateXrefLabelEnabled()) {
				sect1.setXrefLabel(XmlServices.textToXml(classElem.getQualifiedName().toString()));
			}

			String className = classElem.getSimpleName().toString();
			sect1.appendChild(tagFactory.createTitle(hyphenation.hyphenateCamelCase(className)))
					.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary(className)))
					.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary(indexCategory))
							.appendChild(tagFactory.createSecondary(className)));

			htmlDocBookTrafo.transform(classElem, sect1);

			if (sect1.hasContentChildren() == true) {

				sect2 = tagFactory.createSect2();
				sect2.appendChild(tagFactory.createTitle(ResourceServices.getString(res, "C_SYNOPSIS")));

				createSynopsisSection(classElem, sect2);

				if (sect2.hasContentChildren() == true) {
					sect1.appendChild(sect2);
				}

			} else {

				createSynopsisSection(classElem, sect1);
			}

			if (script.isCreateMethodInfoEnabled()) {

				writeExecutableMembers(classElem, docManager.getConstructorElements(classElem), sect1,
						ResourceServices.getString(res, "C_CONSTRUCTOR") + " ");
			}

			if (script.isCreateFieldInfoEnabled()) {

				Set<VariableElement> fields = docManager.getFieldElements(classElem);
				writeFields(classElem, fields, sect1, ResourceServices.getString(res, "C_FIELD") + " ");
			}

			if (script.isCreateMethodInfoEnabled()) {

				writeExecutableMembers(classElem, docManager.getMethodElements(classElem), sect1,
						ResourceServices.getString(res, "C_METHOD") + " ");
			}

			if (script.isCreateMethodInfoEnabled() && docManager.isAnnotationType(classElem)) {

				writeExecutableMembers(classElem, docManager.getMethodElements(classElem), sect1,
						ResourceServices.getString(res, "C_ELEMENT") + " ");
			}

			if (script.isChunkDocBookEnabled() == false) {
				chapter.appendChild(sect1);
			} else {
				createModuleFile(classElem, chapter, sect1);
			}

		} catch (DocletException oops) {

			oops.printStackTrace();
			throw oops;

		} catch (Exception oops) {

			throw new DocletException(oops);
		}
	}

	private boolean writeExecutableMembers(TypeElement classDoc, Set<ExecutableElement> members, NodeImpl parent,
			String prefix) throws DocletException {

		boolean hasCommentedMembers = false;

		Sect2 section;
		ArrayList<ExecutableMemberInfo> commentedMembers = new ArrayList<ExecutableMemberInfo>();

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

			ExecutableElement memberDoc = memberInfo.getExecutableMember();
			ExecutableElement implementedDoc = memberInfo.getImplemented();

			section = tagFactory.createSect2();
			section.setId(getReference(memberDoc));

			Title title = tagFactory.createTitle();
			section.appendChild(title);

			String memberName = docManager.getName(memberDoc);
			String flatSignature = docManager.createMethodFlatSignature(memberDoc);
			String signature = docManager.createMethodSignature(memberDoc);

			if (docManager.isAnnotationType(memberDoc)) {

				indexCategory = "Elements";

				if (script.setCreateXrefLabelEnabled()) {
					section.setXrefLabel(XmlServices.textToXml(memberName));
				}

				title.appendChild(memberName);

			} else {

				indexCategory = "Methods";

				if (script.setCreateXrefLabelEnabled()) {
					section.setXrefLabel(XmlServices.textToXml(memberName + signature));
				}

				title.appendChild(memberName + flatSignature);
			}

			title.appendChild(tagFactory.createIndexterm()
					.appendChild(tagFactory.createPrimary(XmlServices.textToXml(memberName))));

			title.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary(indexCategory))
					.appendChild(tagFactory.createSecondary(XmlServices.textToXml(memberName))));

			ExecutableElement commentDoc = memberInfo.getCommentDoc();

			if (script.isCreateSynopsisEnabled() == true) {

				style.addMemberSynopsis(memberDoc, section);

				if (implementedDoc != null) {
					style.addMethodSpecifiedBy(implementedDoc, section);
				}
			}

			DocCommentTree docCommentTree = docManager.getDocCommentTree(implementedDoc);
			if (nonNull(docCommentTree)) {
				htmlDocBookTrafo.transform(docCommentTree.getFullBody(), section);
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
				String headerTitle = ResourceServices.getString(res, "C_DESCRIPTION_COPIED_FROM_INTERFACE");
				String name = docManager.getName(implementedDoc);
				
				if (ref == null) {

					head.appendChild(headerTitle + ": " + name);

				} else {

					head.appendChild(headerTitle + ": ");
					head.appendChild(tagFactory.createLink(name, ref));
				}
			}

			if (section.hasContentChildren()) {
				parent.appendChild(section);
			}
		}
		return hasCommentedMembers;
	}

	private boolean writeFields(TypeElement classElem, Set<VariableElement> fieldSet, DocBookElement parent,
			String prefix) throws DocletException {

		List<VariableElement> fields = fieldSet.stream()
				.sorted((o1, o2) -> o1.getSimpleName().toString().compareTo(o2.getSimpleName().toString()))
				.collect(Collectors.toList());

		boolean hasCommentedFields = false;

		Sect2 section;
		String str;

		var commentedFields = new ArrayList<VariableElement>();

		for (var field : fields) {

			str = docManager.getCommentText(field);

			if ((str != null) && !str.equals("")) {
				commentedFields.add(field);
			}
		}

		for (var fieldDoc : commentedFields) {

			hasCommentedFields = true;

			section = tagFactory.createSect2();
			section.setId(getReference(fieldDoc));

			String fieldName = docManager.getName(fieldDoc);
			if (script.setCreateXrefLabelEnabled()) {
				section.setXrefLabel(XmlServices.textToXml(fieldName));
			}

			section.appendChild(tagFactory.createTitle(fieldName));
			section.appendChild(tagFactory.createIndexterm()
					.appendChild(tagFactory.createPrimary(XmlServices.textToXml(fieldName))));

			section.appendChild(tagFactory.createIndexterm().appendChild(tagFactory.createPrimary("Fields"))
					.appendChild(tagFactory.createSecondary(XmlServices.textToXml(fieldName))));

			if (script.isCreateSynopsisEnabled() == true) {
				style.addFieldSynopsis(fieldDoc, section);
			}

			if (script.isCreateSerialFieldInfoEnabled() == true) {
				style.addSerialFieldsInfo(fieldDoc, section);
			}

			if (script.isCreateMetaInfoEnabled() == true) {
				style.addMetaInfo(fieldDoc, section);
			}

			String comment = docManager.getCommentText(fieldDoc);
			if ((comment != null) && !comment.trim().equals("")) {
				htmlDocBookTrafo.transform(docManager.getDocCommentTree(fieldDoc).getFullBody(), section);
			}

			if (section.hasContentChildren()) {
				parent.appendChild(section);
			}
		}

		return hasCommentedFields;
	}
}

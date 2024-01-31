/*
 * $Id$
 *
 * ### Copyright (C) 2005 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 *
 * RCS Information
 * Author..........: $Author$
 * Date............: $Date$
 * Revision........: $Revision$
 * State...........: $State$
 */
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Formalpara;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Simplelist;
import org.dbdoclet.tag.docbook.Warning;
import org.dbdoclet.xiphias.XmlServices;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocCommentTree;

/**
 * The class <code>StyleCoded</code> is the super class for the coded styles.
 * 
 * @author <a href="mailto:mfuchs@unico-consulting.com">Michael Fuchs</a>
 * @version 1.0
 */
public abstract class StyleCoded extends StyleBase implements Style {

	@Override
	public boolean addClassSynopsis(TypeElement typeElem, DocBookElement parent)
			throws DocletException {

		if (isNull(typeElem)) {
			throw new IllegalArgumentException(
					"The argument typeElem must not be null!");
		}

		if (isNull(parent)) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		String ref;
		String name;
		Para para;

		boolean rc = synopsis.process(typeElem, parent);

		if (script.isCreateInheritedFromInfoEnabled()) {

			ArrayList<TypeElement> subclasses = statisticData.getSubclasses(docManager.getQualifiedName(typeElem));

			if (subclasses.size() > 0) {

				para = dbfactory.createPara();
				para.setRole("direct-known-subclasses");
				parent.appendChild(para);

				para.appendChild(dbfactory.createEmphasis(ResourceServices
						.getString(res, "C_DIRECT_KNOWN_SUBCLASSES"),
						getEmphasisBoldRole()));
				para.appendChild(": ");

				Simplelist list = dbfactory
						.createSimplelist(Simplelist.FORMAT_INLINE);
				para.appendChild(list);

				for (TypeElement cdoc : subclasses) {

					ref = referenceManager.findReference(cdoc);

					name = XmlServices.textToXml(docManager.getQualifiedName(cdoc));
					name = hyphenation.hyphenateAfter(name, "\\.");

					if ((ref != null) && (ref.length() > 0)) {
						list.appendChild(dbfactory.createMember().appendChild(
								dbfactory.createLink(
										dbfactory.createVarname(name), ref)));
					} else {
						list.appendChild(dbfactory.createMember().appendChild(
								dbfactory.createVarname(name)));
					}
				}
			}

			addMethodsInheritedFrom(parent, docManager.getSuperclass(typeElem));
			addFieldsInheritedFrom(parent, docManager.getSuperclass(typeElem));
		}

		return rc;
	}

	protected boolean addDeprecatedInfo(Element modelElem, DocBookElement parent)
			throws DocletException {

		if (script.isCreateDeprecatedInfoEnabled() == false) {
			return false;
		}

		DocCommentTree docCommentTree = docManager.getDocCommentTree(modelElem);
		if (isNull(docCommentTree)) {
			return false;
		}
		
		DocTree tag = tagManager.findDeprecatedTag(modelElem);

		if (tag != null) {

			Warning warning = dbfactory.createWarning(ResourceServices
					.getString(res, "C_DEPRECATED"));
			parent.appendChild(warning);

			if (nonNull(tag)) {
				dbdTrafo.transform(tag, warning);
			} else {
				warning.appendChild(dbfactory.createPara(ResourceServices
						.getString(res, "C_DEPRECATED")));
			}

			return true;
		}

		return false;
	}

	protected void addFieldsInheritedFrom(DocBookElement parent, TypeElement superDoc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (superDoc == null) {
			return;
		}

		String ref;
		String name;
		Para para;

		HashMap<String, VariableElement> fieldMap = new HashMap<>();

		while (superDoc != null) {

			Set<VariableElement> fields = docManager.getFieldElements(superDoc);
			int fieldCount = 0;

			Simplelist fieldList = dbfactory
					.createSimplelist(Simplelist.FORMAT_INLINE);

			for (var field : fields) {

				name = docManager.getName(field);

				if (fieldMap.get(name) != null) {
					continue;
				} else {
					fieldMap.put(name, field);
				}

				ref = referenceManager.findReference(field);
				name = XmlServices.textToXml(name);

				if ((ref != null) && (ref.length() > 0)
						&& script.isCreateFieldInfoEnabled()) {
					fieldList.appendChild(dbfactory.createMember().appendChild(
							dbfactory.createLink(dbfactory.createLiteral(name),
									ref)));

				} else {
					fieldList.appendChild(dbfactory.createMember().appendChild(
							dbfactory.createVarname(name)));
				}

				fieldCount++;
			}

			if (fieldCount > 0) {

				para = dbfactory.createPara();
				para.setRole("fields-inherited-from");
				parent.appendChild(para);

				para.appendChild(dbfactory.createEmphasis(
						ResourceServices.getString(res,
								"C_FIELDS_INHERITED_FROM")
								+ " "
								+ docManager.getQualifiedName(superDoc),
						getEmphasisBoldRole()));

				para.appendChild(": ");
				para.appendChild(fieldList);
			}

			superDoc = (TypeElement) docManager.getTypeUtils().asElement(superDoc.getSuperclass());
		}
	}

	@Override
	public boolean addFieldSynopsis(VariableElement doc, DocBookElement parent)
			throws DocletException {

		synopsis.addFieldSynopsis(doc, parent);
		return true;
	}

	@Override
	public boolean addInheritancePath(TypeElement classDoc, DocBookElement parent)
			throws DocletException {

		if (classDoc == null) {
			throw new IllegalArgumentException(
					"The argument classDoc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (script.isCreateInheritanceInfoEnabled() == false) {
			return false;
		}

		Formalpara fpara = dbfactory.createFormalpara();
		fpara.appendChild(dbfactory.createTitle(ResourceServices.getString(res,
				"C_INHERITANCE_PATH")));

		ArrayList<TypeElement> list = classDiagramManager.getInheritancePath(classDoc);

		String name;
		String id;
		Para para = dbfactory.createPara();

		int index = 0;
		for (TypeElement doc : list) {

			name = docManager.getQualifiedName(doc);
			id = referenceManager.getId(name);

			if (id != null) {
				para.appendChild(dbfactory.createXref(id));
			} else {
				para.appendChild(name);
			}

			index++;
			if (index < list.size()) {
				para.appendChild("-&gt; ");
			}
		}

		fpara.appendChild(para);
		parent.appendChild(fpara);

		return true;
	}

	@Override
	public boolean addMemberSynopsis(ExecutableElement doc,
			DocBookElement parent) throws DocletException {

		if (docManager.isMethod(doc)) {
			synopsis.addMethodSynopsis(doc, parent);
		}

		if (docManager.isConstructor(doc)) {
			synopsis.addConstructorSynopsis(doc, parent);
		}

		return true;
	}

	protected void addMethodsInheritedFrom(DocBookElement parent, TypeElement superDoc) {

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (superDoc == null) {
			return;
		}

		String ref;
		String name;
		Para para;

		HashMap<String, ExecutableElement> methodMap = new HashMap<String, ExecutableElement>();

		while (superDoc != null) {

			Set<ExecutableElement> methods = docManager.getMethodElements(superDoc);

			int methodCount = 0;

			Simplelist methodList = dbfactory
					.createSimplelist(Simplelist.FORMAT_INLINE);

			for (var method : methods) {

				name = docManager.getName(method);

				if (methodMap.get(name) != null) {
					continue;
				} else {
					methodMap.put(name, method);
				}

				if (docManager.isConstructor(method)) {
					continue;
				}

				if (name.equals("<clinit>")) {
					continue;
				}

				ref = referenceManager.findReference(method);
				name = XmlServices.textToXml(name);

				if ((ref != null) && (ref.length() > 0)
						&& script.isCreateMethodInfoEnabled()) {
					methodList
							.appendChild(dbfactory.createMember()
									.appendChild(
											dbfactory.createLink(dbfactory
													.createLiteral(name), ref)));

				} else {
					methodList.appendChild(dbfactory.createMember()
							.appendChild(dbfactory.createVarname(name)));
				}

				methodCount++;
			}

			if (methodCount > 0) {

				para = dbfactory.createPara();
				para.setRole("methods-inherited-from");
				parent.appendChild(para);

				para.appendChild(dbfactory.createEmphasis(
						ResourceServices.getString(res,
								"C_METHODS_INHERITED_FROM")
								+ " "
								+ docManager.getQualifiedName(superDoc),
						getEmphasisBoldRole()));

				para.appendChild(": ");
				para.appendChild(methodList);
			}

			superDoc = docManager.getSuperclass(superDoc);
		}
	}

	@Override
	public boolean addMethodSpecifiedBy(ExecutableElement doc, DocBookElement parent)
			throws DocletException {

		Para para = dbfactory.createPara();
		para.setRole("method-specified-by");
		parent.appendChild(para);

		para.appendChild(dbfactory.createEmphasis(
				ResourceServices.getString(res, "C_SPECIFIED_BY") + ": ",
				getEmphasisBoldRole()));

		String methodRef = referenceManager.findReference(doc);

		TypeElement classDoc = docManager.getContainingClass(doc);
		String classRef = referenceManager.findReference(classDoc);

		para.appendChild(ResourceServices.getString(res, "C_METHOD") + " ");

		if ((methodRef != null) && (methodRef.length() > 0)
				&& (script.isCreateMethodInfoEnabled() == true)) {

			para.appendChild(dbfactory.createLink(docManager.getName(doc), methodRef));
		} else {

			para.appendChild(dbfactory.createLiteral(docManager.getName(doc)));
		}

		para.appendChild(" "
				+ ResourceServices.getString(res, "C_IN_INTERFACE") + " ");

		if ((classRef != null) && (classRef.length() > 0)) {

			para.appendChild(dbfactory.createLink(docManager.getName(classDoc), classRef));
		} else {

			para.appendChild(dbfactory.createLiteral(docManager.getName(classDoc)));
		}

		return true;
	}

	public abstract boolean addParamInfo(ExecutableElement memberDoc,
			DocBookElement parent) throws DocletException;

	public abstract boolean addSerialFieldsInfo(VariableElement fieldDoc,
			DocBookElement parent) throws DocletException;

	public abstract boolean addThrowsInfo(ExecutableElement memberDoc,
			DocBookElement parent) throws DocletException;

	private String getEmphasisBoldRole() {
		return "bold";
	}
}

/*
 * ### Copyright (C) 2006-2008 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.Sfv;
import org.dbdoclet.doclet.doc.DocFormatter;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.doc.DocletException;
import org.dbdoclet.doclet.doc.ReferenceManager;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.Modifier;
import org.dbdoclet.tag.docbook.Ooclass;
import org.dbdoclet.xiphias.Hyphenation;

import com.google.inject.Inject;

public abstract class Synopsis {

	private static Log logger = LogFactory.getLog(Synopsis.class);

	@Inject
	protected DocManager docManager;
	@Inject
	protected DocFormatter docFormatter;
	@Inject
	protected DocBookTagFactory dbfactory;
	@Inject
	protected Hyphenation hyphenation;
	@Inject
	protected ReferenceManager referenceManager;
	@Inject
	protected DbdScript script;
	
	protected final void createAnnotationElements(AnnotationMirror annotation,
			DocBookElement parent) {

		if (annotation == null) {
			throw new IllegalArgumentException(
					"The argument desc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		Map<? extends ExecutableElement, ? extends AnnotationValue> elements = annotation.getElementValues();
		StringBuilder buffer = new StringBuilder();

		if (elements.size() > 0) {
			parent.appendChild("(");
		}

		for (var entry : elements.entrySet()) {
			buffer.append(entry.getValue().toString());
			buffer.append(", ");
		}
		
		parent.appendChild(StringServices.cutSuffix(buffer.toString().trim(), ","));

		if (elements.size() > 0) {
			parent.appendChild(")");
		}

	}

	protected final void createAnnotations(Element elem,
			DocBookElement parent) {

		if (elem == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		List<? extends AnnotationMirror> annotationList = elem.getAnnotationMirrors();
		for (AnnotationMirror annotation : annotationList) {

			Element annotationType = annotation.getAnnotationType().asElement();
			
			String buffer = annotation.toString();
			if (buffer.startsWith("@java.lang.")) {
				buffer = "@" + annotationType.getSimpleName().toString();
			}
			
			Modifier modifier = dbfactory.createModifier(buffer);
			modifier.setRole("annotation");
			
			createAnnotationElements(annotation, modifier);
			
			parent.appendChild(modifier);
		}
	}

	protected final void createClassModifier(TypeElement typeElem, Ooclass parent) {

		if (isNull(typeElem)) {
			throw new IllegalArgumentException(
					"The argument typeElem must not be null!");
		}

		if (isNull(parent)) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		createAnnotations(typeElem, parent);
		createVisibility(typeElem, parent);

		if (docManager.isAnnotationType(typeElem)) {
			parent.appendChild(dbfactory.createModifier("@interface"));
		} else if (docManager.isInterface(typeElem)) {
			parent.appendChild(dbfactory.createModifier("interface"));
		} else {
			parent.appendChild(dbfactory.createModifier("class"));
		}
	}

	protected final String createClassName(TypeElement typeElem,
			boolean showFullQualifiedName) {

		if (typeElem == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		StringBuilder buffer = new StringBuilder();

		if (showFullQualifiedName == true) {
			
			hyphenation.setHyphenationChar(script.getHyphenationChar());
			String qname = hyphenation.hyphenateAfter(typeElem.getQualifiedName().toString(), "\\.");
			buffer.append(qname);
		
		} else {
			buffer.append(typeElem.getSimpleName().toString());
		}

		createTypeParameters(buffer, typeElem.getTypeParameters(),
				showFullQualifiedName);
		return buffer.toString();
	}

	public final void createMemberModifier(Element elem, DocBookElement parent) {

		if (elem == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent# must not be null!");
		}

		createAnnotations(elem, parent);
		createVisibility(elem, parent);
	}

	protected final String createSuperClassName(TypeElement superElem) {

		if (superElem == null) {
			throw new IllegalArgumentException(
					"The argument superDdoc must not be null!");
		}

		StringBuilder buffer = new StringBuilder();

		buffer.append(createTypeName(superElem));
		return buffer.toString();
	}

	public final void createReturnType(TypeMirror type, DocBookElement parent) {

		if (type == null) {
			throw new IllegalArgumentException(
					"The argument type must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		String buffer = docFormatter.typeToString(type, script.isCreateFullyQualifiedNamesEnabled(), false);
		buffer = buffer.replace(".", ".&#x200b;");
		parent.appendChild(buffer);
	}
	
	public final void createTypeParameter(TypeMirror type, DocBookElement parent) {

		if (type == null) {
			throw new IllegalArgumentException(
					"The argument type must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (TypeKind.TYPEVAR != type.getKind()) {
			throw new IllegalArgumentException(String.format("Argument type must be of kind TypeKind.TYPEVAR not %s", type.getKind()));
		}
		
		String buffer = docFormatter.typeToString(type, script.isCreateFullyQualifiedNamesEnabled());
		buffer = buffer.replace(".", ".&#x200b;");
		parent.appendChild("<" + buffer + ">");
	}
	
	public final void createType(TypeMirror type, DocBookElement parent) {

		if (type == null) {
			throw new IllegalArgumentException(
					"The argument type must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		String buffer = docFormatter.typeToString(type, script.isCreateFullyQualifiedNamesEnabled());
		buffer = buffer.replace(".", ".&#x200b;");
		parent.appendChild(buffer);
	}

	private void createTypeParameters(StringBuilder buffer,
			List<? extends TypeParameterElement> list, boolean showFullQualifiedName) {

		if (!list.isEmpty()) {

			buffer.append("<");

			for (TypeParameterElement tpe : list) {

				buffer.append(createTypeName(tpe));
				buffer.append(",");
			}

			buffer.deleteCharAt(buffer.length() - 1);
			buffer.append(">");
		}
	}

	public final void createVisibility(Element elem,
			DocBookElement parent) {

		if (elem == null) {
			throw new IllegalArgumentException(
					"The argument doc must not be null!");
		}

		if (parent == null) {
			throw new IllegalArgumentException(
					"The argument parent must not be null!");
		}

		if (docManager.isPublic(elem)) {
			parent.appendChild(dbfactory.createModifier("public"));
		}

		if (docManager.isProtected(elem)) {
			parent.appendChild(dbfactory.createModifier("protected"));
		}

		if (docManager.isPrivate(elem)) {
			parent.appendChild(dbfactory.createModifier("private"));
		}

		if (docManager.isStatic(elem)) {
			parent.appendChild(dbfactory.createModifier("static"));
		}

		if (docManager.isAbstract(elem)) {
			parent.appendChild(dbfactory.createModifier("abstract"));
		}

		if (docManager.isSynchronized(elem)) {
			parent.appendChild(dbfactory.createModifier("synchronized"));
		}

		if (docManager.isFinal(elem)) {
			parent.appendChild(dbfactory.createModifier("final"));
		}

		if (docManager.isTransient(elem)) {
			parent.appendChild(dbfactory.createModifier("transient"));
		}

		if (docManager.isVolatile(elem)) {
			parent.appendChild(dbfactory.createModifier("volatile"));
		}

		if (docManager.isNative(elem)) {
			parent.appendChild(dbfactory.createModifier("native"));
		}
	}


	public String getTypeNameWithDimension(Element tpe) {

		StringBuilder buffer = new StringBuilder();

		if (docManager.isClassOrInterface(tpe)) {
			buffer.append(((TypeElement) tpe).getQualifiedName().toString());
		} else {
			buffer.append(tpe.toString());
		}

		// buffer.append(tpe.dimension());

		return buffer.toString();
	}

	public void printType(TypeMirror type) {

		StringBuilder buffer = new StringBuilder();

		buffer.append("============================================" + Sfv.LSEP);

		buffer.append("Type=");
		buffer.append(type);
		buffer.append(Sfv.LSEP);

		Element classDoc = docManager.getElement(type);
		buffer.append("ClassDoc=");
		buffer.append(classDoc);
		buffer.append(Sfv.LSEP);

		if (classDoc != null) {

			buffer.append("isAbstract=");
			buffer.append(docManager.isAbstract(classDoc));
			buffer.append(Sfv.LSEP);
		}

		/*
		buffer.append("TypeVariable=");
		buffer.append(docManager.type.asTypeVariable());
		buffer.append(Sfv.LSEP);

		buffer.append("ParameterizedType=");
		buffer.append(type.asParameterizedType());
		buffer.append(Sfv.LSEP);

		buffer.append("WildcardType=");
		buffer.append(type.asWildcardType());
		buffer.append(Sfv.LSEP);

		buffer.append("AnnotationTypeDoc=");
		buffer.append(type.asAnnotationTypeDoc());
		buffer.append(Sfv.LSEP);
		*/
		System.out.println(buffer.toString());
	}

	public abstract boolean process(TypeElement typeElem, DocBookElement parent)
			throws DocletException;

	private void processBounds(StringBuilder buffer, List<? extends TypeMirror> bounds, int level) {

		if (bounds != null && bounds.size() > 0) {
			String delim = " extends ";
			for (TypeMirror bound : bounds) {
				buffer.append(delim);
				buffer.append(bound.toString());
				delim = " & ";
			}
		}
	}

	public String createTypeName(Element elem) {
		return createTypeName(elem, 0);
	}

	public String createTypeName(Element elem, int level) {

		if (elem == null) {
			return "";
		}

		StringBuilder buffer = new StringBuilder();

		buffer.append(getTypeNameWithDimension(elem));

		if (elem.getKind() == ElementKind.TYPE_PARAMETER) {
			TypeParameterElement tpe = (TypeParameterElement) elem;
			if (!tpe.getBounds().isEmpty()) {
				processBounds(buffer, tpe.getBounds(), level);
			}
		}

		String typeName = buffer.toString();
		hyphenation.setHyphenationChar(script.getHyphenationChar());
		typeName = hyphenation.hyphenateAfter(typeName, " ", "\\.");
		
		return typeName;
	}
}

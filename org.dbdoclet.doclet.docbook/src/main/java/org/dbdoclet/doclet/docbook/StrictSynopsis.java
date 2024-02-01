/*
 * ### Copyright (C) 2006-2012 Michael Fuchs ###
 * ### All Rights Reserved.                 ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@dbdoclet.org
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.dbdoclet.doclet.DocletException;
import org.dbdoclet.tag.docbook.Classname;
import org.dbdoclet.tag.docbook.Classsynopsis;
import org.dbdoclet.tag.docbook.Classsynopsisinfo;
import org.dbdoclet.tag.docbook.Constructorsynopsis;
import org.dbdoclet.tag.docbook.DocBookElement;
import org.dbdoclet.tag.docbook.Exceptionname;
import org.dbdoclet.tag.docbook.Fieldsynopsis;
import org.dbdoclet.tag.docbook.Methodparam;
import org.dbdoclet.tag.docbook.Methodsynopsis;
import org.dbdoclet.tag.docbook.Olink;
import org.dbdoclet.tag.docbook.Ooclass;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Type;


public class StrictSynopsis extends Synopsis {

	private void addConstructors(DocBookElement parent, TypeElement typeElem) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (typeElem == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		addExecutableMembers(parent, docManager.getConstructorElements(typeElem), "Constructors");
	}

	public void addConstructorSynopsis(ExecutableElement elem, DocBookElement parent) {

		Constructorsynopsis synopsis = dbfactory.createConstructorsynopsis();
		parent.appendChild(synopsis);

		createMemberModifier(elem, synopsis);

		synopsis.appendChild(dbfactory.createMethodname(elem.getEnclosingElement().getSimpleName().toString()));

		addParameters(synopsis, elem);
		addExceptions(synopsis, elem);
	}
	
	private void addExceptions(DocBookElement parent, ExecutableElement doc) {

		List<? extends TypeMirror> exceptions = doc.getThrownTypes();

		Exceptionname name;

		for (var type : exceptions) {

			name = dbfactory.createExceptionname(type.toString());
			parent.appendChild(name);
		}
	}

	private void addExecutableMembers(DocBookElement parent, Set<ExecutableElement> members, String title) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (members == null) {
			throw new IllegalArgumentException("The argument members must not be null!");
		}

		if (title == null) {
			throw new IllegalArgumentException("The argument title must not be null!");
		}

		String qualifiedName;

		TreeMap<String, ExecutableElement> publicMembers = new TreeMap<String, ExecutableElement>();
		TreeMap<String, ExecutableElement> publicStaticMembers = new TreeMap<String, ExecutableElement>();
		TreeMap<String, ExecutableElement> protectedMembers = new TreeMap<String, ExecutableElement>();
		TreeMap<String, ExecutableElement> packagePrivateMembers = new TreeMap<String, ExecutableElement>();
		TreeMap<String, ExecutableElement> privateMembers = new TreeMap<String, ExecutableElement>();

		if (nonNull(members) && members.size() > 0) {

			for (ExecutableElement member : members) {

				if (docManager.isAnnotationType(member)) {
					qualifiedName = member.getSimpleName().toString();
				} else {
					qualifiedName = member.getSimpleName().toString() + docManager.createMethodPrettySignature(member);
				}

				if (docManager.isPublic(member)) {

					if (docManager.isStatic(member)) {

						publicStaticMembers.put(qualifiedName, member);

					} else {

						publicMembers.put(qualifiedName, member);
					}

				} else if (docManager.isProtected(member)) {

					protectedMembers.put(qualifiedName, member);

				} else if (docManager.isPackagePrivate(member)) {

					packagePrivateMembers.put(qualifiedName, member);

				} else if (docManager.isPrivate(member)) {

					privateMembers.put(qualifiedName, member);
				}
			}

			if (publicStaticMembers.size() > 0) {
				addExecutableMemberSection(parent, publicStaticMembers, "Public Static " + title);
			}

			if (publicMembers.size() > 0) {

				addExecutableMemberSection(parent, publicMembers, "Public " + title);
			}

			if (protectedMembers.size() > 0) {

				addExecutableMemberSection(parent, protectedMembers, "Protected " + title);
			}

			if (packagePrivateMembers.size() > 0) {

				addExecutableMemberSection(parent, packagePrivateMembers, "Package Private " + title);
			}

			if (privateMembers.size() > 0) {

				addExecutableMemberSection(parent, privateMembers, "Private " + title);
			}
		}
	}

	private void addExecutableMemberSection(DocBookElement parent, TreeMap<String, ExecutableElement> publicStaticMembers,
			String title) {

		if (publicStaticMembers.size() == 0) {
			return;
		}

		Classsynopsisinfo comment = dbfactory.createClasssynopsisinfo("// " + title);
		comment.setRole("comment");
		parent.appendChild(comment);

		for (ExecutableElement doc : publicStaticMembers.values()) {

			if (docManager.isConstructor(doc)) {
				addConstructorSynopsis(doc, parent);
			} else {
				addMethodSynopsis(doc, parent);
			}
		}
	}

	private void addFields(DocBookElement parent, TypeElement typeElem) {

		Set<VariableElement> fieldElements = docManager.getFieldElements(typeElem);

		/*
		 * if (docManager.isEnum(typeElem)) { fields = typeElem.enumConstants(); } else
		 * { fields = typeElem.fields(); }
		 * 
		 * Arrays.sort(fields);
		 */

		String title = "Fields";

		TreeMap<String, VariableElement> publicFields = new TreeMap<>();
		TreeMap<String, VariableElement> publicStaticFields = new TreeMap<>();
		TreeMap<String, VariableElement> protectedFields = new TreeMap<>();
		TreeMap<String, VariableElement> privateFields = new TreeMap<>();
		TreeMap<String, VariableElement> defaultFields = new TreeMap<>();

		for (VariableElement elem : fieldElements) {

			String fieldName = elem.getSimpleName().toString();
			if (docManager.isPublic(elem)) {
				if (docManager.isStatic(elem)) {
					publicStaticFields.put(fieldName, elem);
				} else {
					publicFields.put(fieldName, elem);
				}
			} else if (docManager.isProtected(elem)) {
				protectedFields.put(fieldName, elem);
			} else if (docManager.isPrivate(elem)) {
				privateFields.put(fieldName, elem);
			} else {
				defaultFields.put(fieldName, elem);
			}
		}

		addFieldSection(parent, publicStaticFields, "Public Static " + title);
		addFieldSection(parent, publicFields, "Public " + title);
		addFieldSection(parent, protectedFields, "Protected " + title);
		addFieldSection(parent, defaultFields, "Package " + title);
		addFieldSection(parent, privateFields, "Private " + title);
	}

	private void addFieldSection(DocBookElement parent, TreeMap<String, VariableElement> publicStaticFields,
			String title) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (publicStaticFields.size() == 0) {
			return;
		}

		Classsynopsisinfo comment = dbfactory.createClasssynopsisinfo("// " + title);
		comment.setRole("comment");
		parent.appendChild(comment);

		for (VariableElement fdoc : publicStaticFields.values()) {
			addFieldSynopsis(fdoc, parent);
		}
	}

	public void addFieldSynopsis(VariableElement varElem, DocBookElement parent) {

		Fieldsynopsis synopsis = dbfactory.createFieldsynopsis();
		parent.appendChild(synopsis);

		createMemberModifier(varElem, synopsis);

		Type type = dbfactory.createType();
		synopsis.appendChild(type);

		String typeName = docManager.typeToString(varElem.asType(), script.isCreateFullyQualifiedNamesEnabled());
		type.appendChild(typeName);

		synopsis.appendChild(dbfactory.createVarname(varElem.getSimpleName().toString()));

		if (nonNull(varElem.getConstantValue())) {
			String value = varElem.getConstantValue().toString();
			synopsis.appendChild(dbfactory.createInitializer(value));
		}
	}

	private void addInterfaces(DocBookElement parent, TypeElement typeElem) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (typeElem == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		List<? extends TypeMirror> interfaces = typeElem.getInterfaces();

		DocBookElement ooelem;
		Types typeUtils = docManager.getTypeUtils();
		for (TypeMirror mirror : interfaces) {

			TypeElement te = (TypeElement) typeUtils.asElement(mirror);
			if (docManager.isInterface(te) == false) {

				ooelem = dbfactory.createOoclass();
				ooelem.appendChild(
						dbfactory.createClassName(createTypeName(te, script.isCreateFullyQualifiedNamesEnabled())));

			} else {

				ooelem = dbfactory.createOointerface();
				ooelem.appendChild(
						dbfactory.createInterfacename(createTypeName(te, script.isCreateFullyQualifiedNamesEnabled())));
			}

			parent.appendChild(ooelem);
		}

	}

	private void addMethods(DocBookElement parent, TypeElement doc) {

		if (parent == null) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		if (doc == null) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		addExecutableMembers(parent, docManager.getMethodElements(doc), "Methods");
	}

	public void addMethodSynopsis(ExecutableElement doc, DocBookElement parent) {

		Methodsynopsis synopsis = dbfactory.createMethodsynopsis();
		parent.appendChild(synopsis);

		createMemberModifier(doc, synopsis);

		Type type = dbfactory.createType();
		synopsis.appendChild(type);

		createType(doc.getReturnType(), type, script.isCreateFullyQualifiedNamesEnabled());

		String name = doc.getSimpleName().toString();
		synopsis.appendChild(dbfactory.createMethodname(name));

		addParameters(synopsis, doc);
		addExceptions(synopsis, doc);
	}

	private void addParameters(DocBookElement parent, ExecutableElement elem) {

		List<? extends VariableElement> parameters = elem.getParameters();

		if (parameters.size() == 0) {

			parent.appendChild(dbfactory.createVoid());
			return;
		}

		Methodparam param;

		int index = 0;
		for (VariableElement ve : parameters) {

			param = dbfactory.createMethodparam();

			Type type = dbfactory.createType();
			param.appendChild(type);

			String typeName = docManager.typeToString(ve.asType(), script.isCreateFullyQualifiedNamesEnabled());
			if (elem.isVarArgs() && index == parameters.size() - 1) {
				typeName = docManager.varArgsTypeToString(ve.asType(), script.isCreateFullyQualifiedNamesEnabled());
			}
			
			type.appendChild(typeName);
			
			String name = ve.getSimpleName().toString();
			param.appendChild(dbfactory.createParameter(name));

			parent.appendChild(param);
			 
			index++;
		}
	}

	public boolean process(TypeElement typeElem, DocBookElement parent) throws DocletException {

		if (isNull(typeElem)) {
			throw new IllegalArgumentException("The argument doc must not be null!");
		}

		if (isNull(parent)) {
			throw new IllegalArgumentException("The argument parent must not be null!");
		}

		try {

			Classsynopsis synopsis = dbfactory.createClasssynopsis();
			if (docManager.isInterface(typeElem)) {
				synopsis.setInterface(true);
			}

			Ooclass ooclass = dbfactory.createOoclass();
			synopsis.appendChild(ooclass);

			createClassModifier(typeElem, ooclass);

			ooclass.appendChild(
					dbfactory.createClassName(createClassName(typeElem, script.isCreateFullyQualifiedNamesEnabled())));

			TypeMirror superType = typeElem.getSuperclass();

			if (superType instanceof NoType == false) {

				TypeElement superElem = (TypeElement) docManager.getTypeUtils().asElement(superType);
				if (superElem.getQualifiedName().toString().equals("java.lang.Object") == false) {
					Ooclass extend = dbfactory.createOoclass();
					synopsis.appendChild(extend);

					Classname className = dbfactory.createClassname();
					String classNameText = createSuperClassName(superElem, script.isCreateFullyQualifiedNamesEnabled());

					/*
					 * TODO Migration String ref = referenceManager.findReference(superElem);
					 * 
					 * if (ref != null) { className.appendChild(dbfactory.createLink( classNameText,
					 * ref)); } else { className.appendChild(classNameText); }
					 */
					className.appendChild(classNameText);
					extend.appendChild(className);
				}
			}

			addInterfaces(synopsis, typeElem);
			addFields(synopsis, typeElem);
			addConstructors(synopsis, typeElem);
			addMethods(synopsis, typeElem);

			if (docManager.isAnnotationType(typeElem)) {
				// TODO addElements(synopsis, (AnnotationTypeDoc) typeElem);
			}

			parent.appendChild(synopsis);

			if (script.isLinkSourceEnabled()) {

				Para para = dbfactory.createPara();
				Olink olink = dbfactory.createOlink("Source", typeElem.getQualifiedName().toString(), "listing");
				para.appendChild(olink);

				parent.appendChild(para);
			}

			return true;

		} catch (Exception oops) {
			throw new DocletException(oops);
		}
	}
}

package org.dbdoclet.doclet;

import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.service.StringServices;

import com.sun.source.doctree.SeeTree;

public class ReferenceManager {

	private static Log logger = LogFactory.getLog(ReferenceManager.class);
	private static Pattern nonXmlNameCharPattern = Pattern
			.compile("[^\\w\\._-]+");

	private HashMap<String, String> idMap;
	private String documentationId;
	private DocManager docManager;

	public void init(String documentationId,
			TreeMap<String, TreeMap<String, TypeElement>> pkgMap, 
			XmlIdType type) {

		if (pkgMap == null) {
			throw new IllegalArgumentException("Parameter pkgMap is null");
		}

		if (idMap != null) {
			throw new IllegalStateException("Id map was already created!");
		}

		this.documentationId = documentationId;

		String id;

		int packageCounter = 1;
		int classCounter = 1;

		HashMap<String, String> map = new HashMap<String, String>();
		String className;
		TreeMap<String, TypeElement> classMap;
		TypeElement cdoc;

		for (String pkgName : pkgMap.keySet()) {

			if (type == XmlIdType.JAVA) {

				id = "package-" + pkgName;
				id = createJavaId(id);
				map.put(pkgName, id);

			} else {

				id = "PACKAGE-" + packageCounter++;
				id = createNumberedId(id);
				map.put(pkgName, id);
			}

			classMap = pkgMap.get(pkgName);

			if (classMap != null) {

				for (Iterator<String> classIterator = classMap.keySet()
						.iterator(); classIterator.hasNext();) {

					className = classIterator.next();
					cdoc = classMap.get(className);
					createClassIds(map, cdoc, classCounter, type);
					classCounter++;
				}
			}
		}

		idMap = map;
	}

	public String createReferenceLabel(SeeTree tag) throws DocletException {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		String label = docManager.getCommentText(tag.getReference());
		logger.debug("Label for reference " + tag.toString() + " is " + label
				+ ".");

		return label;
	}

	public String findReference(SeeTree tag) {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		if (idMap == null) {
			throw new IllegalStateException("Variable idMap is null!");
		}

		logger.debug("Find reference for tag " + tag.toString());

		String key = docManager.getCommentText(tag.getReference());
		String reference = getId(key);
		logger.debug("Reference = " + reference + ".");

		return reference;
	}

	public String findReference(Element element) {

		if (element == null) {
			throw new IllegalArgumentException(
					"The argument element must not be null!");
		}

		if (ElementKind.METHOD.equals(element.getKind())) {
			return findReference((ExecutableElement) element);
		}

		if (ElementKind.FIELD.equals(element.getKind())) {
			return findReference((VariableElement) element);
		}

		String key = docManager.getQualifiedName(element);
		String reference = getId(key);

		return reference;
	}

	public String findReference(VariableElement fieldDoc) {

		if (fieldDoc == null) {
			throw new IllegalArgumentException(
					"The argument fieldDoc must not be null!");
		}

		String key = createFieldKey(fieldDoc);
		String reference = getId(key);

		if (reference == null) {

			TypeElement cdoc = docManager.getContainingClass(fieldDoc);
			key = docManager.getQualifiedName(cdoc);
			reference = idMap.get(key);
		}

		return reference;
	}

	public String findReference(ExecutableElement methodDoc) {

		if (methodDoc == null) {
			throw new IllegalArgumentException(
					"The argument methodDoc must not be null!");
		}

		String key = createMethodKey(methodDoc);
		String reference = getId(key);

		if (reference == null) {

			TypeElement cdoc = docManager.getContainingClass(methodDoc);
			key = docManager.getQualifiedName(cdoc);
			reference = idMap.get(key);
		}

		return reference;
	}

	public String createMethodKey(ExecutableElement doc) {

		String qualifiedName = docManager.getQualifiedName(doc);
		StringBuilder id = new StringBuilder(qualifiedName);
		id.append('(');

		for (VariableElement param : doc.getParameters()) {

			if (param == null) {
				continue;
			}

			TypeMirror type = param.asType();

			if (type != null) {
				id.append(docManager.getQualifiedName(type));
				id.append(",");
			}
		}

		return StringServices.cutSuffix(id.toString(), ",") + ")";
	}

	public String createFieldKey(VariableElement variableElement) {
		String id = docManager.getQualifiedName(variableElement);
		return id;
	}

	public String createJavaId(String id) {

		if (id == null) {
			throw new IllegalArgumentException(
					"The argument id must not be null!");
		}

		String nid = StringServices.replace(id, "<", "_");
		nid = StringServices.replace(nid, ">", "_");

		Matcher matcher = nonXmlNameCharPattern.matcher(nid);
		nid = matcher.replaceAll("-");

		nid = StringServices.cutPrefix(nid, "-");
		nid = StringServices.cutSuffix(nid, "-");

		if (documentationId != null && documentationId.trim().length() > 0) {
			nid = documentationId + "-" + nid;
		}

		return nid;
	}

	public String createNumberedId(String id) {

		String nid = id;

		if (documentationId != null && documentationId.trim().length() > 0) {
			nid = documentationId + "-" + nid;
		}

		return nid;
	}

	public String getId(String key) {

		if (key == null) {
			throw new IllegalArgumentException("Parameter key is null!");
		}

		if (idMap == null) {
			throw new IllegalStateException("Variable idMap is null!");
		}

		Object obj = idMap.get(key);

		if (obj == null) {

			return null;
		}

		if (!(obj instanceof String)) {

			throw new IllegalStateException("Value for key '" + key
					+ "' is not of type String but of type '"
					+ obj.getClass().getName() + "'!");
		}

		return (String) obj;
	}

	private void createClassIds(HashMap<String, String> map, TypeElement classElement,
			int index, XmlIdType type) {

		String id;

		int constructorCounter = 1;
		int methodCounter = 1;
		int fieldCounter = 1;

		String qname = docManager.getQualifiedName(classElement);
		if (type == XmlIdType.JAVA) {

			String name = "class-" + qname;
			id = createJavaId(name);
			map.put(name, id);

		} else {

			id = "CLASS-" + index;
			id = createNumberedId(id);
			map.put(qname, id);
		}

		Set<ExecutableElement> constructors = docManager.getConstructorElements(classElement);

		for (ExecutableElement constructor : constructors) {

			String comment = docManager.getCommentText(constructor);

			if (nonNull(comment) && !comment.isBlank()) {

				if (type == XmlIdType.JAVA) {

					id = "constructor-" + createMethodKey(constructor);
					id = createJavaId(id);
					map.put(createMethodKey(constructor), id);

				} else {

					id = "CONSTRUCTOR-" + index + "-" + constructorCounter++;
					id = createNumberedId(id);
					map.put(createMethodKey(constructor), id);
				}
			}
		}

		Set<ExecutableElement> methods = docManager.getMethodElements(classElement);

		for (ExecutableElement method : methods) {

			String comment = docManager.getCommentText(method);

			if (nonNull(comment) && !comment.isBlank()) {

				if (type == XmlIdType.JAVA) {

					id = "method-" + createMethodKey(method);
					id = createJavaId(id);
					map.put(createMethodKey(method), id);

				} else {

					id = "METHOD-" + index + "-" + methodCounter++;
					id = createNumberedId(id);
					map.put(createMethodKey(method), id);
				}
			}
		}

		Set<VariableElement> fields = docManager.getFieldElements(classElement);

		for (VariableElement field : fields) {

			String comment = docManager.getCommentText(field);

			if ((comment != null) && !comment.equals("")) {

				if (type == XmlIdType.JAVA) {

					id = "field-" + docManager.getQualifiedName(field);
					id = createJavaId(id);
					map.put(docManager.getQualifiedName(field), id);

				} else {

					id = "FIELD-" + index + "-" + fieldCounter++;
					id = createNumberedId(id);
					map.put(docManager.getQualifiedName(field), id);
				}
			}
		}

		if (docManager.isAnnotationType(classElement)) {

			TypeElement atdoc = (TypeElement) classElement;
			List<? extends Element> elements = atdoc.getEnclosedElements();

			for (Element element : elements) {

				String comment = docManager.getCommentText(element);

				if (nonNull(comment) && !comment.isBlank()) {

					if (type == XmlIdType.JAVA) {

						id = "method-" + createMethodKey((ExecutableElement) element);
						id = createJavaId(id);
						map.put(createMethodKey((ExecutableElement) element), id);

					} else {

						id = "METHOD-" + index + "-" + methodCounter++;
						id = createNumberedId(id);
						map.put(createMethodKey((ExecutableElement) element), id);
					}
				}
			}
		}
	}

	public void setDocManager(DocManager docManager) {
		this.docManager = docManager;
	}
}

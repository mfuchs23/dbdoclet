package org.dbdoclet.doclet;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.dbdoclet.doclet.doc.DocFormatter;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.service.StringServices;

import com.google.inject.Inject;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.util.DocTreePath;

public class ReferenceManager {

	private static Logger logger = Logger.getLogger(ReferenceManager.class.getName());
	private static Pattern nonXmlNameCharPattern = Pattern
			.compile("[^\\w\\._-]+");

	private HashMap<String, String> idMap;
	private String documentationId;
	
	@Inject
	private DocManager docManager;
	@Inject
	private DocFormatter docFormatter;

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

	public String createReferenceLabel(LinkTree tag) throws DocletException {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		String label = docManager.getCommentText(tag.getLabel());
		if (isNull(label) || label.isBlank()) {
			label = docManager.getCommentText(tag.getReference());			
		}
		
		logger.fine("Label for link reference " + tag.toString() + " is " + label
				+ ".");

		return label;
	}

	public String createReferenceLabel(SeeTree tag) throws DocletException {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		String label = docManager.getCommentText(tag.getReference());
		logger.fine("Label for is reference " + tag.toString() + " is " + label
				+ ".");

		return label;
	}

	public String findReference(DocTreePath path, LinkTree tag) {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		if (idMap == null) {
			throw new IllegalStateException("Variable idMap is null!");
		}

		logger.fine("Find reference for tag " + tag.toString());

		ReferenceTree rtree = tag.getReference();
		String key= rtree.getSignature();
		if (key.startsWith("#")) {
			key = key.replaceFirst("#", ".");
			Element element = docManager.findTypeElement(path.getTreePath());
			key = docManager.getQualifiedName(element) + key;
		}
		
		logger.fine("Reference = " + key + ".");

		key = normalizeKey(key);
		return idMap.get(key);
	}

	private String normalizeKey(String key) {
		return key;
	}

	public String findReference(SeeTree tag) {

		if (tag == null) {
			throw new IllegalArgumentException("Parameter tag is null!");
		}

		if (idMap == null) {
			throw new IllegalStateException("Variable idMap is null!");
		}

		logger.fine("Find reference for tag " + tag.toString());

		String key = docManager.getCommentText(tag.getReference());
		String reference = getId(key);
		logger.fine("Reference = " + reference + ".");

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
		return docManager.getQualifiedName(doc) + docFormatter.createMethodFlatSignature(doc);
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

			if (docManager.hasContent(constructor)) {

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

			if (docManager.hasContent(method)) {

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

			if (docManager.hasContent(field)) {

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

				if (docManager.hasContent(element)) {

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
}

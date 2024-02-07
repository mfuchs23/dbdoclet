package org.dbdoclet.doclet.doc;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.dbdoclet.doclet.CDI;
import org.dbdoclet.doclet.scanner.DeprecatedScanner;

import com.google.inject.Inject;

public class DeprecatedManager {

    private static final String DEPRECATED_ANNOTATIONS = "C_DEPRECATED_ANNOTATIONS";
    private static final String DEPRECATED_ANNOTATION_ELEMENTS = "C_DEPRECATED_ANNOTATION_ELEMENTS";
    private static final String DEPRECATED_CLASSES = "C_DEPRECATED_CLASSES";
    private static final String DEPRECATED_CONSTRUCTORS = "C_DEPRECATED_CONSTRUCTORS";
    private static final String DEPRECATED_ENUMS = "C_DEPRECATED_ENUMS";
    private static final String DEPRECATED_ERRORS = "C_DEPRECATED_ERRORS";
    private static final String DEPRECATED_EXCEPTIONS = "C_DEPRECATED_EXCEPTIONS";
    private static final String DEPRECATED_FIELDS = "C_DEPRECATED_FIELDS";
    private static final String DEPRECATED_INTERFACES = "C_DEPRECATED_INTERFACES";
    private static final String DEPRECATED_METHODS = "C_DEPRECATED_METHODS";
    
    private TreeMap<String,ArrayList<Element>> deprecatedMap;

	@Inject
	private DocManager docManager;
	
    public DeprecatedManager() {
		deprecatedMap = new TreeMap<String, ArrayList<Element>>();
    }

    public void setSpecifiedElements(Set<TypeElement> specifiedList) {
        DeprecatedScanner scanner = CDI.getInstance(DeprecatedScanner.class);
        scanner.setElements(specifiedList);
        scanner.getDeprecatedElements().forEach(e -> addDoc(e));
    }
    
    
    private void addDoc(Element elem) {
        
        if (isNull(elem)) {
        	return;
        }
        
        String key = "C_UNKNOWN_TYPE";
        
        if (docManager.isAnnotationType(elem)) {
            key = DEPRECATED_ANNOTATIONS;
        }
        
        if (docManager.isAnnotationTypeElement(elem)) {
            key = DEPRECATED_ANNOTATION_ELEMENTS;
        }
        
        if (docManager.isClass(elem)) {
            key = DEPRECATED_CLASSES;
        }
        
        if (docManager.isConstructor(elem)) {
        		key = DEPRECATED_CONSTRUCTORS;
        }
        
        if (docManager.isEnum(elem)) {
            key = DEPRECATED_ENUMS;
        }
        
        if (docManager.isError(elem)) {
            key = DEPRECATED_ERRORS;
        }
        
        if (docManager.isException(elem)) {
            key = DEPRECATED_EXCEPTIONS;
        }
        
        if (docManager.isField(elem)) {
            key = DEPRECATED_FIELDS;
        }
        
        if (docManager.isInterface(elem)) {
            key = DEPRECATED_INTERFACES;
        }
        
        if (docManager.isMethod(elem)) {
            key = DEPRECATED_METHODS;
        }
        
        if (deprecatedMap.get(key) == null) {
            deprecatedMap.put(key, new ArrayList<Element>());
        }
        
        ArrayList<Element> docList = deprecatedMap.get(key);
        docList.add(elem);
    }
    
    public TreeMap<String,ArrayList<Element>> getDeprecatedMap() {
        return deprecatedMap;
    }
}

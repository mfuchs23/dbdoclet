package org.dbdoclet.doclet8;

import java.util.ArrayList;
import java.util.TreeMap;

import org.dbdoclet.doclet8.docbook.DbdServices;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;

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
    
    private TreeMap<String,ArrayList<Doc>> deprecatedMap;

    
    public DeprecatedManager() {
        
        deprecatedMap = new TreeMap<String, ArrayList<Doc>>();
    }

    public void addDoc(Doc doc) {
        
        if (doc == null) {
            return;
        }
        
        boolean isDeprecated = false;
        
        if (DbdServices.findComment(doc.tags(), "@deprecated") != null) {
            isDeprecated = true;
        }
        
        if (doc instanceof ProgramElementDoc) {
        
            ProgramElementDoc ped = (ProgramElementDoc) doc;
            
            for (AnnotationDesc desc : ped.annotations()) {
                
                try {
                
                    AnnotationTypeDoc adoc = desc.annotationType();
                
                    if (adoc.name().equals("Deprecated")) {
                        isDeprecated = true;
                    }
                
                } catch (Throwable oops) {
                    // Die Bibliothek der Anootation war nicht im Classpath.
                }
            }
        }
        
        if (isDeprecated == false) {
            return;
        }
        
        String key = "C_UNKNOWN_TYPE";
        
        if (doc.isAnnotationType()) {
            key = DEPRECATED_ANNOTATIONS;
        }
        
        if (doc.isAnnotationTypeElement()) {
            key = DEPRECATED_ANNOTATION_ELEMENTS;
        }
        
        if (doc.isClass()) {
            key = DEPRECATED_CLASSES;
        }
        
        if (doc.isConstructor()) {
            key = DEPRECATED_CONSTRUCTORS;
        }
        
        if (doc.isEnum()) {
            key = DEPRECATED_ENUMS;
        }
        
        if (doc.isError()) {
            key = DEPRECATED_ERRORS;
        }
        
        if (doc.isException()) {
            key = DEPRECATED_EXCEPTIONS;
        }
        
        if (doc.isField()) {
            key = DEPRECATED_FIELDS;
        }
        
        if (doc.isInterface()) {
            key = DEPRECATED_INTERFACES;
        }
        
        if (doc.isMethod()) {
            key = DEPRECATED_METHODS;
        }
        
        if (deprecatedMap.get(key) == null) {
            deprecatedMap.put(key, new ArrayList<Doc>());
        }
        
        ArrayList<Doc> docList = deprecatedMap.get(key);
        docList.add(doc);
    }
    
    public TreeMap<String,ArrayList<Doc>> getDeprecatedMap() {
        return deprecatedMap;
    }
}

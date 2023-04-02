package org.dbdoclet.doclet;

import static java.util.Objects.nonNull;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;

import org.dbdoclet.doclet.scanner.ClassScanner;
import org.dbdoclet.doclet.scanner.FieldScanner;
import org.dbdoclet.doclet.scanner.PackageScanner;
import org.dbdoclet.doclet.scanner.TypeScanner;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ProvidesTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.UsesTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SimpleDocTreeVisitor;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

public class DocManager {

	private DocletEnvironment env;
	private Reporter reporter;
	
	public PackageElement containingPackage(TypeElement classElem) {

		Element parent = classElem.getEnclosingElement();
		while (nonNull(parent) && parent.getKind() != ElementKind.PACKAGE) {
			parent = parent.getEnclosingElement();
		}
		return (PackageElement) parent;
	}

	public Set<TypeElement> getClassElements() {
		ClassScanner scanner = new ClassScanner(getDocletEnvironment().getIncludedElements());
		return scanner.getClassElements();
	}

	public DocCommentTree getDocCommentTree(Element element) {
		return getDocletEnvironment().getDocTrees().getDocCommentTree(element);
	}

	public DocletEnvironment getDocletEnvironment() {
		return env;
	}

	// TODO: getElement
	public Element getElement(DocTree tree) {
		return null;
	}

	public Elements getElementUtils() {
		return getDocletEnvironment().getElementUtils();
	}

	public Set<VariableElement> getFieldElements(TypeElement typeElem) {
		FieldScanner scanner = new FieldScanner(typeElem.getEnclosedElements());
		return scanner.getFieldElements();		
	}

	public FileObject getFileObject(TypeElement typeElem) {
		DocTrees docTrees = getDocletEnvironment().getDocTrees();
		return docTrees.getPath(typeElem).getCompilationUnit().getSourceFile();
    }

	public Set<PackageElement> getPackageElements() {
		PackageScanner scanner = new PackageScanner(getDocletEnvironment().getIncludedElements());
		return scanner.getPackageElements();
	}
	
	public Element getReferencedElement(DocTree docTree) {

		return new SimpleDocTreeVisitor<Element, Void>() {
            @Override
            protected Element defaultAction(DocTree node, Void p) {
               return null;
            }

            @Override
            public Element visitLink(LinkTree node, Void p) {
                return visit(node.getReference(), null);
            }

            @Override
            public Element visitProvides(ProvidesTree node, Void p) {
                return visit(node.getServiceType(), null);
            }

            @Override
            public Element visitReference(ReferenceTree node, Void p) {
                return getElement(node);
            }

            @Override
            public Element visitSee(SeeTree node, Void p) {
                for (DocTree dt : node.getReference()) {
                    return visit(dt, null);
                }
                return null;
            }

            @Override
            public Element visitSerialField(SerialFieldTree node, Void p) {
                return visit(node.getType(), null);
            }

            @Override
            public Element visitUses(UsesTree node, Void p) {
                return visit(node.getServiceType(), null);
            }

            @Override
            public Element visitValue(ValueTree node, Void p) {
                return visit(node.getReference(), null);
            }
        }.visit(docTree, null);
	
	}

	public Reporter getReporter() {
		return reporter;
	}

	public Set<? extends Element> getSpecifiedElements() {
		return env.getSpecifiedElements();
	}

	public Set<TypeElement> getTypeElements() {
		TypeScanner scanner = new TypeScanner(getDocletEnvironment().getIncludedElements());
		return scanner.getTypeElements();
	}

	public Set<TypeElement> getTypeElements(PackageElement pkgDoc) {
		TypeScanner scanner = new TypeScanner(pkgDoc.getEnclosedElements());
		return scanner.getTypeElements();
	}

	public Types getTypeUtils() {
		return getDocletEnvironment().getTypeUtils();
	}

	public boolean isAbstract(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.ABSTRACT);
	}

	public boolean isAnnotationType(TypeElement typeElem) {
		return typeElem.getKind() == ElementKind.ANNOTATION_TYPE;
	}

	public boolean isClassOrInterface(Element elem) {
		return elem.getKind() == ElementKind.CLASS || elem.getKind() == ElementKind.INTERFACE;
	}

	public boolean isFinal(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.FINAL);
	}

	public boolean isInterface(TypeElement typeElem) {
		return typeElem.getKind().isInterface();
	}

	public boolean isNative(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.NATIVE);
	}

	public boolean isPrivate(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.PRIVATE);
	}

	public boolean isProtected(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.PROTECTED);
	}

	public boolean isPublic(Element elem) {
		return elem.getModifiers().contains(Modifier.PUBLIC);
	}

	public boolean isStatic(Element elem) {
		return elem.getModifiers().contains(Modifier.STATIC);
	}

	public boolean isSynchronized(Element elem) {
		return elem.getModifiers().contains(Modifier.SYNCHRONIZED);
	}

	public boolean isTransient(Element elem) {
		return elem.getModifiers().contains(Modifier.TRANSIENT);
	}

	public boolean isVolatile(Element elem) {
		return elem.getModifiers().contains(Modifier.VOLATILE);
	}
	
	public void setDocletEnvironment(DocletEnvironment environment) {
		this.env = environment;		
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	public boolean isPrimitiveType(TypeMirror type) {
		switch (type.getKind()) {
		case BOOLEAN:
		case BYTE:
		case CHAR:
		case DOUBLE:
		case FLOAT:
		case INT:
		case LONG:
		case SHORT:
			return true;
		default:
			return false;
			
		}
	}
}

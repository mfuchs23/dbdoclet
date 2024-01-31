package org.dbdoclet.doclet.doc;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;

import org.dbdoclet.doclet.scanner.ClassScanner;
import org.dbdoclet.doclet.scanner.ConstructorScanner;
import org.dbdoclet.doclet.scanner.FieldScanner;
import org.dbdoclet.doclet.scanner.MethodScanner;
import org.dbdoclet.doclet.scanner.PackageScanner;
import org.dbdoclet.doclet.scanner.TypeScanner;

import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ParamTree;
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
	private String overviewFile;
	private Reporter reporter;

	public PackageElement containingPackage(TypeElement classElem) {

		Element parent = classElem.getEnclosingElement();
		while (nonNull(parent) && parent.getKind() != ElementKind.PACKAGE) {
			parent = parent.getEnclosingElement();
		}
		return (PackageElement) parent;
	}

	public String createMethodFlatSignature(ExecutableElement executableElement) {
		
		StringBuilder buffer = new StringBuilder();
		buffer.append('(');
		
		for (VariableElement param : executableElement.getParameters()) {
			buffer.append(typeToString(param.asType(), false));
			buffer.append(", ");
		}

		if (buffer.length() > 2) {
			buffer.delete(buffer.length()-2, buffer.length());
		}
		
		buffer.append(')');
		return buffer.toString();
	}
	
	public String createMethodPrettySignature(ExecutableElement executableElement) {
		StringBuilder buffer = new StringBuilder();
		buffer.append('(');
		for (VariableElement param : executableElement.getParameters()) {
			buffer.append(typeToString(param.asType(), false));
			buffer.append(" ");
			buffer.append(param.getSimpleName());
			buffer.append(", ");
		}
		if (buffer.length() > 2) {
			buffer.delete(buffer.length()-2, buffer.length());
		}
		buffer.append(')');
		return buffer.toString();
	}

	public String createMethodSignature(ExecutableElement executableElement) {
		
		StringBuilder buffer = new StringBuilder();
		buffer.append('(');
		for (VariableElement param : executableElement.getParameters()) {
			buffer.append(typeToString(param.asType(), true));
			buffer.append(", ");
		}
		
		if (buffer.length() > 2) {
			buffer.delete(buffer.length()-2, buffer.length());
		}
		
		buffer.append(')');
		return buffer.toString();
	}

	@SuppressWarnings("unchecked")
	public <T extends DocTree> List<T> findDocTreeList(Element memberDoc, Class<T> clazz, DocTree.Kind kind) {
		
		ArrayList<T> list = new ArrayList<>();
		DocCommentTree docCommentTree = getDocCommentTree(memberDoc);
		for (DocTree docTree : docCommentTree.getFullBody()) {
			if (kind.equals(docTree.getKind())) {
				list.add((T) docTree);
			}
		}
		return list;
	}

	public Set<ExecutableElement> getAnnotationElements(TypeElement annotationElem) {
		MethodScanner scanner = new MethodScanner(annotationElem.getEnclosedElements());
		return scanner.getMethodElements();
	}

	public Set<TypeElement> getClassElements() {
		ClassScanner scanner = new ClassScanner(getDocletEnvironment().getIncludedElements());
		return scanner.getClassElements();
	}
	
	public String getCommentText(DocTree docTree) {
		if (isNull(docTree)) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(docTree.toString());
		return buffer.toString();
	}

	public String getCommentText(Element elem) {

		if (isNull(elem)) {
			return null;
		}

		StringBuilder buffer = new StringBuilder();

		DocCommentTree docTreeList = getDocCommentTree(elem);
		if (nonNull(docTreeList)) {
			for (DocTree dtree : docTreeList.getFullBody()) {
				buffer.append(dtree.toString());
			}
		}

		return buffer.toString();
	}

	public String getCommentText(List<? extends DocTree> docTreeList) {
		if (isNull(docTreeList)) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		for (DocTree dtree : docTreeList) {
			buffer.append(dtree.toString());
		}
		return buffer.toString();
	}

	public Set<ExecutableElement> getConstructorElements(TypeElement typeElem) {
		ConstructorScanner scanner = new ConstructorScanner(typeElem.getEnclosedElements());
		return scanner.getConstructorElements();
	}
	
	public TypeElement getContainingClass(Element element) {
		Element parent = element.getEnclosingElement();
		if (nonNull(parent) && ElementKind.CLASS.equals(parent.getKind())) {
			return (TypeElement) parent;
		}
		return null;
	}

	public DocCommentTree getDocCommentTree(Element element) {
		return getDocletEnvironment().getDocTrees().getDocCommentTree(element);
	}

	public DocletEnvironment getDocletEnvironment() {
		return env;
	}

	// @TODO Migration
	public Element getElement(ReferenceTree tree) {
		tree.getSignature();
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
	
	public Set<ExecutableElement> getMethodElements(TypeElement typeElem) {
		MethodScanner scanner = new MethodScanner(typeElem.getEnclosedElements());
		return scanner.getMethodElements();
	}

	public String getName(Element element) {
		if (isNull(element)) {
			return "";
		}
		return element.getSimpleName().toString();
	}

	public String getName(TypeMirror type) {
		return getQualifiedName(getTypeUtils().asElement(type));
	}

	public List<? extends DocTree> getOverviewComment() {

		if (isNull(overviewFile)) {
			return Collections.emptyList();
		}

		Set<TypeElement> typeList = ElementFilter.typesIn(getDocletEnvironment().getSpecifiedElements());
		TypeElement element = typeList.iterator().next();

		DocCommentTree docCommentTree;
		try {
			docCommentTree = getDocletEnvironment().getDocTrees().getDocCommentTree(element, overviewFile);
		} catch (IOException e) {
			reporter.print(Diagnostic.Kind.WARNING, "The overview file could not be read: " + e.getMessage());
			return Collections.emptyList();
		}

		if (docCommentTree != null) {
			return docCommentTree.getFullBody();
		}

		return Collections.emptyList();
	}

	public Set<PackageElement> getPackageElements() {
		PackageScanner scanner = new PackageScanner(getDocletEnvironment().getIncludedElements());
		return scanner.getPackageElements();
	}

	public List<ParamTree> getParamTags(ExecutableElement elem) {

		var paramList = new ArrayList<ParamTree>();
		DocCommentTree tree = getDocCommentTree(elem);
		if (nonNull(tree)) {
			for (var docTree : tree.getBlockTags()) {
				if (docTree.getKind() == DocTree.Kind.PARAM) {
					paramList.add((ParamTree) docTree);
				}
			}
		}

		return paramList;
	}

	public String getQualifiedName(Element element) {

		if (isNull(element)) {
			return null;
		}
		
		switch (element.getKind()) {
		case PACKAGE:
			return ((PackageElement) element).getQualifiedName().toString();
		case CLASS:
		case INTERFACE:
			return getQualifiedName((TypeElement) element);
		case CONSTRUCTOR:
		case METHOD:
			return getQualifiedName((ExecutableElement) element);
		case FIELD:
		case LOCAL_VARIABLE:
		case PARAMETER:
			return getQualifiedName((VariableElement) element);
		case MODULE:
		case OTHER:
		case INSTANCE_INIT:
		case EXCEPTION_PARAMETER:
		case ENUM_CONSTANT:
		case ENUM:
		case ANNOTATION_TYPE:
		case TYPE_PARAMETER:
		case STATIC_INIT:
		case RESOURCE_VARIABLE:
		default:
			return element.getSimpleName().toString();
		}
	}

	public String getQualifiedName(ExecutableElement executableElement) {
		if (isNull(executableElement)) {
			return "";
		}
		
		StringBuilder qname = new StringBuilder();
		Element parent = executableElement.getEnclosingElement();
		if (nonNull(parent) && isClassOrInterface(parent)) {
			qname.append(((TypeElement) parent).getQualifiedName().toString());
			qname.append('.');
		}
		
		qname.append(executableElement.getSimpleName().toString());
		return qname.toString();
	}

	public String getQualifiedName(TypeElement typeElement) {
		if (isNull(typeElement)) {
			return "";
		}
		return typeElement.getQualifiedName().toString();
	}

	public String getQualifiedName(TypeMirror type) {
		return getQualifiedName(getTypeUtils().asElement(type));
	}

	public String getQualifiedName(VariableElement variableElement) {
		if (isNull(variableElement)) {
			return "";
		}
		
		StringBuilder qname = new StringBuilder();
		Element parent = variableElement.getEnclosingElement();
		if (nonNull(parent) && ElementKind.CLASS.equals(parent.getKind())) {
			qname.append(((TypeElement) parent).getQualifiedName().toString());
			qname.append('.');
		}
		qname.append(variableElement.getSimpleName().toString());
		return qname.toString();
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

	@SuppressWarnings("unchecked")
	public Set<TypeElement> getSpecifiedElements() {
		return (Set<TypeElement>) env.getSpecifiedElements();
	}

	public TypeElement getSuperclass(TypeElement typeElem) {
		TypeMirror superType = typeElem.getSuperclass();
		if (isNull(superType)) {
			return null;
		}
		return (TypeElement) getTypeUtils().asElement(superType);
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

	public ExecutableElement implementedMethod(TypeElement classElem, ExecutableElement methodElem) {

		if (methodElem == null) {
			throw new IllegalArgumentException("The argument methodElem must not be null!");
		}
		
		if (classElem != null) {

			List<? extends TypeMirror> interfaces = classElem.getInterfaces();
			for (TypeMirror mirror : interfaces) {

				TypeElement mirrorElem = (TypeElement) getTypeUtils().asElement(mirror);
				Set<ExecutableElement> methodElements = getMethodElements(mirrorElem);
				for (ExecutableElement interfaceMethodElem : methodElements) {				  
					if (getElementUtils().overrides(methodElem, interfaceMethodElem, classElem)) {
						return interfaceMethodElem;						
					}
				} 
			}
		}

		return null;
	}

	public boolean isAbstract(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.ABSTRACT);
	}

	public boolean isAnnotationType(Element elem) {
		return elem.getKind() == ElementKind.ANNOTATION_TYPE;
	}

	public boolean isClass(Element elem) {
		return elem.getKind() == ElementKind.CLASS;
	}

	public boolean isClassOrInterface(Element elem) {
		return elem.getKind() == ElementKind.CLASS || elem.getKind() == ElementKind.INTERFACE;
	}

	public boolean isConstructor(ExecutableElement elem) {
		return elem.getKind() == ElementKind.CONSTRUCTOR || elem.getKind() == ElementKind.INTERFACE;
	}

	public boolean isEnum(Element elem) {
		return elem.getKind() == ElementKind.ENUM;
	}

	public boolean isError(Element elem) {
		if (isClass(elem)) {
			Elements elements = getElementUtils();
			TypeElement errorElem = elements.getTypeElement("java.lang.Error");
			Types types = getTypeUtils();
			return types.isSubtype(elem.asType(), errorElem.asType());
		}
		return false;
	}

	public boolean isException(Element elem) {
		if (isClass(elem)) {
			Elements elements = getElementUtils();
			TypeElement exceptionElem = elements.getTypeElement("java.lang.Exception");
			Types types = getTypeUtils();
			return types.isSubtype(elem.asType(), exceptionElem.asType());
		}
		return false;
	}

	public boolean isFinal(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.FINAL);
	}

	public boolean isInterface(TypeElement typeElem) {
		return typeElem.getKind().isInterface();
	}

	public boolean isMethod(ExecutableElement elem) {
		return elem.getKind() == ElementKind.METHOD;
	}

	public boolean isNative(Element typeElem) {
		return typeElem.getModifiers().contains(Modifier.NATIVE);
	}

	public boolean isPackagePrivate(Element typeElem) {
		Set<Modifier> modifiers = typeElem.getModifiers();
		return !modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.PROTECTED)
				&& !modifiers.contains(Modifier.PUBLIC);
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

	public ExecutableElement overriddenMethod(ExecutableElement methodElem) {

		if (isStatic(methodElem)) {
            return null;
        }
        
		TypeElement classElem = (TypeElement) methodElem.getEnclosingElement();
		TypeMirror superType = classElem.getSuperclass();
		TypeElement superElem = (TypeElement) getTypeUtils().asElement(superType);
		
		while (superElem != null) {
			for (ExecutableElement m : getMethodElements(superElem)) {
				if (getElementUtils().overrides(methodElem, m, superElem)) {
						return m;						
				}
			}
			superType = superElem.getSuperclass();
			superElem = (TypeElement) getTypeUtils().asElement(superType);
		}
		
		return null;
    }

	public void setDocletEnvironment(DocletEnvironment environment) {
		this.env = environment;
	}

	public void setOverviewFile(String overviewFile) {
		this.overviewFile = overviewFile;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	public String typeToString(TypeMirror type, boolean showFullQualifiedName) {

		if (isPrimitiveType(type)) {
			return type.toString();
		}

		Element elem = getTypeUtils().asElement(type);
		if (nonNull(elem)) {
			if (showFullQualifiedName && elem instanceof TypeElement) {
				return ((TypeElement) elem).getQualifiedName().toString();
			} else {
				return elem.getSimpleName().toString();
			}
		} else {
			return type.toString();
		}
	}
}

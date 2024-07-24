package org.dbdoclet.doclet.common.scanner;

import static java.util.Objects.isNull;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner9;

import org.dbdoclet.doclet.common.doc.TagManager;

import com.google.inject.Inject;

public class MethodScanner extends ElementScanner9<Void, Integer> {

	@Inject
	private TagManager tagManager;

	private TreeSet<ExecutableElement> set = new TreeSet<>(
			(a, b) -> (a.getSimpleName().toString().compareTo(b.getSimpleName().toString())));
	private List<? extends Element> elements;

	public void setElements(List<? extends Element> elements) {
		this.elements = elements;
	}

	public SortedSet<ExecutableElement> getMethodElements() {

		if (isNull(elements)) {
			throw new IllegalStateException("The field elements must not be null!");
		}

		set.clear();
		scan(elements, 0);
		return set;
	}

	@Override
	public Void scan(Element elem, Integer depth) {

		ElementKind kind = elem.getKind();
		if (kind == ElementKind.METHOD && !tagManager.isHidden(elem)) {
			set.add((ExecutableElement) elem);
		}

		return super.scan(elem, depth + 1);
	}

}

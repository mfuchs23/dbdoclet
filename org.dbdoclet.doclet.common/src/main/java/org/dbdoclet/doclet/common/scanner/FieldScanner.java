package org.dbdoclet.doclet.common.scanner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner9;

import org.dbdoclet.doclet.common.doc.TagManager;

import com.google.inject.Inject;

public class FieldScanner extends ElementScanner9<Void, Integer> {

	@Inject
	private TagManager tagManager;

	private HashSet<VariableElement> set = new HashSet<>();
	private List<? extends Element> elements;

	public void setElements(List<? extends Element> elements) {
		this.elements = elements;
	}

	public Set<VariableElement> getFieldElements() {
		set.clear();
		scan(elements, 0);
		return set;
	}

	@Override
	public Void scan(Element elem, Integer depth) {

		ElementKind kind = elem.getKind();
		if (kind == ElementKind.FIELD && !tagManager.isHidden(elem)) {
			set.add((VariableElement) elem);
		}

		return super.scan(elem, depth + 1);
	}

}

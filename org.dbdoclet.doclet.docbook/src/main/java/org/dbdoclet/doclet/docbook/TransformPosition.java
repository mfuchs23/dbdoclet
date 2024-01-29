package org.dbdoclet.doclet.docbook;

import javax.lang.model.element.Element;

import org.dbdoclet.tag.ITransformPosition;

public class TransformPosition implements ITransformPosition {

	private Element doc;

	public TransformPosition(Element elem) {
		this.doc = elem;
	}
	
	@Override
	public String getDescription() {
		return doc != null ? doc.toString() : "";
	}
}

package org.dbdoclet.doclet8.docbook;

import org.dbdoclet.tag.ITransformPosition;

import com.sun.javadoc.Doc;

public class TransformPosition implements ITransformPosition {

	private Doc doc;

	public TransformPosition(Doc doc) {
		this.doc = doc;
	}
	
	@Override
	public String getDescription() {
		return doc != null ? doc.toString() : "";
	}

}

package org.dbdoclet.doclet.docbook;

import javax.inject.Inject;
import javax.inject.Provider;

import org.dbdoclet.doclet.CDI;

public class StyleProvider implements Provider<Style> {

	@Inject
	DbdScript script;
	
	@Override
	public Style get() {

		String styleName = script.getDocumentStyle();

		if (styleName.equals("table")) {
			return CDI.getInstance(StyleTable.class);
		}

		return CDI.getInstance(StyleVariableList.class);
	}
}

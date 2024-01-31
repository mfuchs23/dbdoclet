package org.dbdoclet.doclet.docbook;

import org.dbdoclet.doclet.CDI;

import com.google.inject.Inject;
import com.google.inject.Provider;

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

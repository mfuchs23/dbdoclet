package org.dbdoclet.doclet8.docbook;

import javax.inject.Inject;
import javax.inject.Provider;

import org.dbdoclet.doclet8.InstanceFactory;

public class StyleProvider implements Provider<Style> {

	@Inject
	DbdScript script;
	
	@Override
	public Style get() {

		String styleName = script.getDocumentStyle();

		if (styleName.equals("table")) {
			return InstanceFactory.getInstance(StyleTable.class);
		}

		return InstanceFactory.getInstance(StyleVariableList.class);
	}
}

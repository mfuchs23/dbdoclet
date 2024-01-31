package org.dbdoclet.doclet8.docbook;

import org.dbdoclet.doclet8.InstanceFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

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

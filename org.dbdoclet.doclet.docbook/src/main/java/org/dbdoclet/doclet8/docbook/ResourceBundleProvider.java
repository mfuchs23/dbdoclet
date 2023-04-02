package org.dbdoclet.doclet8.docbook;

import java.util.ResourceBundle;

import javax.inject.Provider;

public class ResourceBundleProvider implements Provider<ResourceBundle> {

	@Override
	public ResourceBundle get() {
		
		ResourceBundle res = ResourceBundle
				.getBundle("org.dbdoclet.doclet.docbook.Resources");

		return res;
	}

}

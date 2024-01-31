package org.dbdoclet.doclet.docbook;

import java.util.ResourceBundle;

import com.google.inject.Provider;

public class ResourceBundleProvider implements Provider<ResourceBundle> {

	@Override
	public ResourceBundle get() {
		
		ResourceBundle res = ResourceBundle
				.getBundle("org.dbdoclet.doclet.docbook.Resources");

		return res;
	}

}

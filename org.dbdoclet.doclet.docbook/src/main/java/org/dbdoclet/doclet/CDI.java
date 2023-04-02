package org.dbdoclet.doclet;

import com.google.inject.Injector;

public class CDI {

	private static Injector injector;

	public static Injector getInjector() {
		return injector;
	}

	public static <T> T getInstance(Class<T> type) {
		return CDI.injector.getInstance(type);
	}

	public static void setInjector(Injector injector) {
		CDI.injector = injector;
	}
}

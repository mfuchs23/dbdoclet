package org.dbdoclet.doclet;

import com.google.inject.Injector;

public class InstanceFactory {

	private static Injector injector;

	public static Injector getInjector() {
		return injector;
	}

	public static <T> T getInstance(Class<T> type) {
		return InstanceFactory.injector.getInstance(type);
	}

	public static void setInjector(Injector injector) {
		InstanceFactory.injector = injector;
	}
}

package org.dbdoclet.doclet;

import org.dbdoclet.doclet.annotation.Param;

public class MethodArguments {

	public int test_1(String param1) {
		return 42;
	}

	public int test_2(int paramInt, float paramFloat, boolean paramBooleanPrimitive, String paramString) {
		return 42;
	}

	public int test_3(String...opts) {
		return 42;
	}

	public int test_4(@Param String paramWithAnnotation) {
		return 42;
	}
}

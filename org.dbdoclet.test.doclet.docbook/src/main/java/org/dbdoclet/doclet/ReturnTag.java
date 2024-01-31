package org.dbdoclet.doclet;

public class ReturnTag {

	/**
	 * @return Die Antwort auf alles.
	 */
	public int test_1() {
		return 42;
	}

	/**
	 * Mit inline Tag code.
	 * 
	 * @return Die Zahl 42 als {@code Integer}.
	 */
	public int test_2() {
		return 42;
	}
}

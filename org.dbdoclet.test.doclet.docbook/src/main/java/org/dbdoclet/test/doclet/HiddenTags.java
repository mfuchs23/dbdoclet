package org.dbdoclet.test.doclet;

public class HiddenTags {

	/**
	 * @hidden
	 */
	public int hiddenField;
	
	public int visibleField = 1;
	
	/**
	 * @hidden
	 * @return 42
	 */
	public int hiddenMethod() {
		return 42;
	}

	/**
	 * @return 42
	 */
	public int visibleMethod() {
		return 42;
	}
}

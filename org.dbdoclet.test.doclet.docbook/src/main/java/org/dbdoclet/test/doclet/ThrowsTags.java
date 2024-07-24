package org.dbdoclet.test.doclet;

public class ThrowsTags {

	/**
	 * @exception TestException Unchecked <code>Exception</code>.
	 * @return 42 
	 */
	public int test_1() throws TestException {
		return 42;
	}

	/**
	 * Mit inline link-Tag code.
	 * 
	 * @throws NullPointerException If 42 couldn't be found.
	 * @throws TestException Lorem <code>RuntimeException</code> {@link org.dbdoclet.test.doclet.TestException Blabla}
	 * @return 42
	 */
	public int test_2() throws TestException, NullPointerException {
		return 42;
	}
}

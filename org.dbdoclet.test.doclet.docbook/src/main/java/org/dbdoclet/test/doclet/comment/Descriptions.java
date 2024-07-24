package org.dbdoclet.test.doclet.comment;

public class Descriptions {

	/**
	 * Eine <code>Beschreibung</code> mit erster Zeile und weiteren Absätzen.
	 * <p>
	 * Weiterer Absatz
	 * <p>
	 * Und noch einer
	 */
	public void multipleP() {
	}

	/**
	 * <p>
	 * Eine <code>Beschreibung</code> mit p-Tag vor erster Zeile und weiteren
	 * Absätzen.
	 * <p>
	 * Weiterer Absatz
	 * <p>
	 * Und noch einer
	 */

	public void emptyLines() {
	}

	/**
	 * Eine <code>Beschreibung</code> mit 3 Absätzen, die durch Leerzeilen getrennt
	 * sind.
	 *
	 * Weiterer Absatz
	 * 
	 * Und noch einer
	 */
	public void multiplePWithTopP() {
	}

	/** Eine einfache <code>Beschreibung</code> der Methode. */
	public void simpleWithCode() {
	}

	/** Eine einfache Beschreibung der Methode. */
	public void simple() {
	}
}

package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextTests extends AbstractTestCase {

	private static final String PROFILE_MAXIMAL = "src/main/resources/profile/showAll.her";
	private static final String PROFILE_MINIMAL = "src/main/resources/profile/showMinimal.her";
	
	@Test
	public void corporationDefined() {

		javadoc("-profile", PROFILE_MAXIMAL);

		String value = xpath("/db:book/db:info/db:legalnotice/db:simpara");
		assertTrue(value.indexOf("Michael Fuchs") != -1);
	}

	@Test
	public void corporationUndefined() {

		javadoc("-profile", PROFILE_MINIMAL);

		String value = xpath("/db:book/db:info/db:legalnotice/db:simpara");
		assertNull(value);
	}
}

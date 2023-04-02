package org.dbdoclet.doclet.docbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.spi.ToolProvider;

import org.dbdoclet.doclet.DocletException;
import org.junit.Test;

public class MigrationTests {

	private String sourcePath = "src/main/java/";

	@Test
	public void testSimple() throws DocletException, IOException {

		// String srcpath = sourcePath + "org/dbdoclet/music/MusicElement.java";
		String srcpath = "org.dbdoclet.music";
		String classpath = sourcePath;

		javadoc("-cp", classpath, srcpath);
	}

	private void javadoc(String... options) {
		
		ToolProvider javadoc = ToolProvider.findFirst("javadoc").orElseThrow();
		ArrayList<String> ol = new ArrayList<>();
		ol.add("-doclet");
		ol.add("org.dbdoclet.doclet.migration.MigrationDoclet");
		ol.addAll(Arrays.asList(options));
		javadoc.run(System.out, System.err, ol.toArray(new String[ol.size()]));
	}	
}

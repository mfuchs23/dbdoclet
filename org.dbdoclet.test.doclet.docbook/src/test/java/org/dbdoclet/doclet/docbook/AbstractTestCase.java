package org.dbdoclet.doclet.docbook;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.dbdoclet.progress.InfoListener;
import org.dbdoclet.service.ExecResult;
import org.dbdoclet.service.ExecServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.xiphias.XmlServices;
import org.dbdoclet.xiphias.XmlValidationResult;
import org.dbdoclet.xiphias.XsdServices;
import org.junit.Before;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.tools.javadoc.Main;

public class AbstractTestCase implements InfoListener {

	private static final String DEFAULT_DOCBOOK_SCHEMA_PATH = "src/main/resources/xsd/docbook.xsd";
	private static final String DEFAULT_PROFILE_PATH = "src/main/resources/profile/";
	private static final String DEFAULT_DEST_PATH = "build/test/docbook/";
	
	protected static final ResourceBundle res = ResourceBundle
			.getBundle("org/dbdoclet/doclet/docbook/Resources");
	
	private String docbookSchemaPath = DEFAULT_DOCBOOK_SCHEMA_PATH;

	protected String dbdocletVersion = "8.0.2";
	protected String destPath = DEFAULT_DEST_PATH;
	protected String profilePath = DEFAULT_PROFILE_PATH;
	protected String generatedPath = "build/test/generated/";
	protected String sourcePath = "src/main/java/";
	protected String docbookDocletJarFileName = "lib/dbdoclet_" + dbdocletVersion + ".jar";

	@Override
	public void info(String text) {
		System.out.println(text);
	}

	@Before
	public void startUp() throws IOException {

		URL url = AbstractTestCase.class
				.getResource("/org/dbdoclet/music/Note.java");

		if (url != null) {
			
			String path = url.getPath();
			
			sourcePath = StringServices.cutSuffix(path,
					"org/dbdoclet/music/Note.java");
			
			String projectPath = StringServices.cutSuffix(sourcePath, "src/main/java/");
			destPath = FileServices.appendPath(projectPath, DEFAULT_DEST_PATH);
			profilePath = FileServices.appendPath(projectPath, DEFAULT_PROFILE_PATH);
			docbookSchemaPath = FileServices.appendPath(projectPath, DEFAULT_DOCBOOK_SCHEMA_PATH);
			docbookDocletJarFileName = projectPath + "/lib/dbdoclet_" + dbdocletVersion + ".jar";
		}

		File destDir = new File(destPath);

		if (destDir.exists()) {
			FileServices.delete(destDir);
		}

		System.out.println("Arbeitsverzeichnis: "
				+ new File(".").getAbsolutePath());
		System.out.println("sourcePath: " + sourcePath);
		System.out.println("destPath: " + destPath);
		System.out.println("profilePath: " + profilePath);
	}

	protected void pln(String text) {
		System.out.println(text);
	}

	protected void runForked(String cmd) {
		try {

			ExecResult rc = ExecServices.exec(cmd, this);
			assertTrue(rc.failed() == false);

			XmlValidationResult result = XsdServices.validate(new File(destPath
					+ "Reference.xml"), new File(docbookSchemaPath));

			if (result.failed()) {
				System.out.println(result.createTextReport());
			}

			assertTrue(result.failed() == false);

		} catch (Exception oops) {

			oops.printStackTrace();
			fail(oops.getMessage());
		}
	}

	protected String xpath(String query) {
		return xpath(query, "Reference.xml");
	}

	protected void printDocBookFile() throws IOException {
		System.out.println(FileServices.readToString(new File(destPath, "Reference.xml")));
	}
	
	protected String xpath(String query, String fileName) {

		try {

			Document doc = XmlServices
					.parse(new File(destPath + "/" + fileName));

			Object obj = null;

			JXPathContext context = JXPathContext.newContext(doc);
			context.registerNamespace("db", "http://docbook.org/ns/docbook");
			CompiledExpression expr = JXPathContext.compile(query);

			obj = expr.getValue(context);

			if (obj == null) {
				return null;
			}

			return obj.toString();

		} catch (JXPathException oops) {

			String msg = oops.getMessage();

			if (msg != null && msg.startsWith("No value for xpath")) {
				return null;

			}

			oops.printStackTrace();
			fail(oops.getMessage());

		} catch (Exception oops) {

			oops.printStackTrace();
			fail(oops.getMessage());
		}

		return "";
	}

	protected String docComment(String comment) throws IOException, SAXException, ParserConfigurationException {
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("public class DocComment {\n");
		buffer.append("\n/**\n");
		buffer.append(comment);
		buffer.append("\n*/\n");
		buffer.append("public void testDocComment() {}\n");		
		buffer.append("}");
		File genSrcFile = new File(generatedPath, "DocComment.java");
		FileServices.writeFromString(genSrcFile, buffer.toString());
		
		javadoc("-d", destPath, genSrcFile.getAbsolutePath());
				
		File docBookFile = new File(destPath, "Reference.xml");
		XmlServices.parse(docBookFile, true, ResourceServices.getResourceAsUrl("/xsd/docbook/docbook.xsd"));
		return FileServices.readToString(docBookFile);
	}
	
	protected void javadocTestPackage(String... options) {

		String[] mandatoryOptions = { "-d", destPath, "-sourcepath",
				sourcePath, "org.dbdoclet.music" };

		ArrayList<String> optionList = new ArrayList<String>();

		for (String option : options) {
			
			if (option.endsWith(".her")) {
				option = FileServices.appendPath(profilePath, option);
			}
			
			optionList.add(option);
		}

		for (String option : mandatoryOptions) {
			optionList.add(option);
		}

		String[] cmd = optionList.toArray(new String[optionList.size()]);
		javadoc(cmd);
	}

	protected void javadoc(String... options) {

		ArrayList<String> optionList = new ArrayList<String>();

		boolean foundDestDir = false;
		
		for (String option : options) {
			optionList.add(option);
			if (option.equals("-d")) {
				foundDestDir = true;
			}
		}

		if (foundDestDir == false) {
			optionList.add("-d");
			optionList.add(destPath);
		}
		
		String[] cmd = optionList.toArray(new String[optionList.size()]);
		Main.execute("Test", "org.dbdoclet.doclet.docbook.DocBookDoclet", cmd);
	}

}

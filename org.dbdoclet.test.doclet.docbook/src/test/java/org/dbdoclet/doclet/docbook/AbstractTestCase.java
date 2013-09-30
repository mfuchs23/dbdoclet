package org.dbdoclet.doclet.docbook;

import static com.sun.tools.javac.code.Flags.PRIVATE;
import static com.sun.tools.javac.code.Flags.PROTECTED;
import static com.sun.tools.javac.code.Flags.PUBLIC;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.dbdoclet.progress.InfoListener;
import org.dbdoclet.service.ExecResult;
import org.dbdoclet.service.ExecServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.xiphias.XmlServices;
import org.dbdoclet.xiphias.XmlValidationResult;
import org.dbdoclet.xiphias.XsdServices;
import org.junit.Before;
import org.w3c.dom.Document;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javadoc.JavadocTool;
import com.sun.tools.javadoc.Main;
import com.sun.tools.javadoc.Messager;
import com.sun.tools.javadoc.ModifierFilter;
import com.sun.tools.javadoc.RootDocImpl;

public class AbstractTestCase implements InfoListener {

	protected static final ResourceBundle res = ResourceBundle
			.getBundle("org/dbdoclet/doclet/docbook/Resources");
	private final String docbookXsdFileName = "src/main/resources/xsd/docbook.xsd";

	protected String destPath = "build/test/docbook/";
	protected String sourcePath = "src/main/java/";
	protected String docbookDocletJarFileName = "lib/dbdoclet_7.0.0.jar";

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
			destPath = StringServices.cutSuffix(sourcePath, "src/main/java/")
					+ "/build/test/docbook/";
			docbookDocletJarFileName = StringServices.cutSuffix(sourcePath,
					"src/main/java/") + "/lib/dbdoclet_7.0.0.jar";
		}

		File destDir = new File(destPath);

		if (destDir.exists()) {
			FileServices.delete(destDir);
		}

		System.out.println("Arbeitsverzeichnis: "
				+ new File(".").getAbsolutePath());
		System.out.println("sourcePath: " + sourcePath);
		System.out.println("destPath: " + destPath);

	}

	protected RootDocImpl javadoc(String[] sources, String classpath,
			String[][] args) {

		Context context = new Context();
		Messager.preRegister(context, "dbdoclet");

		JavadocTool tool = JavadocTool.make0(context);

		final ListBuffer<String> subPackages = new ListBuffer<String>();
		subPackages.append("yes");

		final ListBuffer<String> xcludePackages = new ListBuffer<String>();

		final ListBuffer<String> javaNames = new ListBuffer<String>();

		for (String srcpath : sources) {
			javaNames.append(srcpath);
		}

		final ListBuffer<String[]> options = new ListBuffer<String[]>();
		options.append(new String[] { "-d", destPath });

		if (args != null) {

			for (String[] arg : args) {
				options.append(arg);
			}
		}
		// final Options compOpts = Options.instance(context);
		// compOpts.put("-classpath", classpath);
		// compOpts.put("-d", destPath);

		RootDocImpl root;

		try {
			root = tool.getRootDocImpl("de", "", new ModifierFilter(PUBLIC
					| PROTECTED | PRIVATE), javaNames.toList(),
					options.toList(), false, subPackages.toList(),
					xcludePackages.toList(), false, false, false);
		} catch (IOException oops) {
			oops.printStackTrace();
			return null;
		}

		return root;
	}

	protected void pln(String text) {
		System.out.println(text);
	}

	protected void runForked(String cmd) {
		try {

			ExecResult rc = ExecServices.exec(cmd, this);
			assertTrue(rc.failed() == false);

			XmlValidationResult result = XsdServices.validate(new File(destPath
					+ "Reference.xml"), new File(docbookXsdFileName));

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

	protected void javadoc(String... options) {

		String[] mandatoryOptions = { "-d", destPath, "-sourcepath",
				sourcePath, "org.dbdoclet.music" };

		ArrayList<String> optionList = new ArrayList<String>();

		for (String option : options) {
			optionList.add(option);
		}

		for (String option : mandatoryOptions) {
			optionList.add(option);
		}

		String[] cmd = optionList.toArray(new String[optionList.size()]);
		Main.execute("Test", "org.dbdoclet.doclet.docbook.DocBookDoclet", cmd);
	}

}

package org.dbdoclet.doclet;

import static com.sun.tools.javac.code.Flags.PROTECTED;
import static com.sun.tools.javac.code.Flags.PUBLIC;

import java.io.IOException;

import com.sun.javadoc.RootDoc;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javadoc.JavadocTool;
import com.sun.tools.javadoc.Messager;
import com.sun.tools.javadoc.ModifierFilter;
import com.sun.tools.javadoc.RootDocImpl;

public class DocCreator {

	public static RootDoc javadoc(String[] sources, String classpath) throws IOException {

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

		final Options compOpts = Options.instance(context);
		compOpts.put("-classpath", classpath);

		RootDocImpl root = tool.getRootDocImpl("de", "", new ModifierFilter(
				PUBLIC | PROTECTED), javaNames.toList(), options.toList(),
				false, subPackages.toList(), xcludePackages.toList(), false,
				false, false);

		return root;
	}

}

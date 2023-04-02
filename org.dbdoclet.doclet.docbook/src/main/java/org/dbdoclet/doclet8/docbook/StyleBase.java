/*
 * $Id$
 *
 * ### Copyright (C) 2005 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 *
 * RCS Information
 * Author..........: $Author$
 * Date............: $Date$
 * Revision........: $Revision$
 * State...........: $State$
 */
package org.dbdoclet.doclet8.docbook;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.dbdoclet.doclet8.ClassDiagramManager;
import org.dbdoclet.doclet8.ReferenceManager;
import org.dbdoclet.doclet8.StatisticData;
import org.dbdoclet.doclet8.TagManager;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.xiphias.Hyphenation;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;

public class StyleBase {

	protected static final String FSEP = System.getProperty("file.separator");
	protected static final String INDENT = "  ";
	protected static final String LSEP = System.getProperty("line.separator");
	protected static final String RSEP = "/";

	@Inject
	protected ClassDiagramManager classDiagramManager;
	@Inject
	protected DbdTransformer dbdTrafo;
	@Inject
	protected DocBookTagFactory dbfactory;
	@Inject
	protected Hyphenation hyphenation;
	@Inject
	protected ReferenceManager referenceManager;
	@Inject
	protected ResourceBundle res;
	@Inject
	protected DbdScript script;
	@Inject
	protected StatisticData statisticData;
	@Inject
	protected StrictSynopsis synopsis;
	@Inject
	protected TagManager tagManager;

	protected final LinkedHashMap<String, ArrayList<Tag>> createTagMap(
			Doc doc) {

		ArrayList<Tag> list;
		String kind;

		LinkedHashMap<String, ArrayList<Tag>> tagMap = new LinkedHashMap<String, ArrayList<Tag>>();

		Tag[] tags = doc.tags();

		for (int i = 0; i < tags.length; i++) {

			kind = tags[i].kind();

			list = tagMap.get(kind);

			if (list == null) {

				list = new ArrayList<Tag>();
				tagMap.put(kind, list);
			}

			list.add(tags[i]);
		}

		return tagMap;
	}
}
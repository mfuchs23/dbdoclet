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
package org.dbdoclet.doclet.docbook;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import javax.lang.model.element.Element;

import org.dbdoclet.doclet.ClassDiagramManager;
import org.dbdoclet.doclet.StatisticData;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.doc.ReferenceManager;
import org.dbdoclet.doclet.doc.TagManager;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.xiphias.Hyphenation;

import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTree.Kind;

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
	protected DocManager docManager;
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
	protected DocBookTagManager tagManager;

	protected final LinkedHashMap<DocTree.Kind, ArrayList<BlockTagTree>> createTagMap(Element doc) {

		LinkedHashMap<DocTree.Kind, ArrayList<BlockTagTree>> tagMap = new LinkedHashMap<>();
		DocCommentTree docCommentTree = docManager.getDocCommentTree(doc);
		
		if (isNull(docCommentTree)) {
			return tagMap;
		}
		
		for (DocTree bt : docCommentTree.getBlockTags()) {

			if (bt instanceof BlockTagTree == false) {
				continue;
			}
			
			Kind kind = bt.getKind();
			ArrayList<BlockTagTree> list = tagMap.get(kind);

			if (isNull(list)) {
				list = new ArrayList<BlockTagTree>();
				tagMap.put(kind, list);
			}

			list.add((BlockTagTree) bt);
		}
		
		return tagMap;
	}
}
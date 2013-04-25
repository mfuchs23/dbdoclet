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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.dbdoclet.doclet.ReferenceManager;
import org.dbdoclet.doclet.StatisticData;
import org.dbdoclet.doclet.TagManager;
import org.dbdoclet.trafo.tag.docbook.DocBookTagFactory;

import com.sun.javadoc.Doc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.Tag;

public class StyleBase {

	protected static final String RSEP = "/";
	protected static final String FSEP = System.getProperty("file.separator");
	protected static final String LSEP = System.getProperty("line.separator");
	protected static final String INDENT = "  ";

	@Inject
	protected DocBookTagFactory dbfactory;

	@Inject
	protected DbdTransformer htmlDocBookTrafo;

	@Inject
	protected ResourceBundle res;

	@Inject
	protected ReferenceManager referenceManager;

	@Inject
	protected DbdScript script;

	@Inject
	protected TagManager tagManager;

	@Inject
	protected StatisticData statisticData;

	@Inject
	protected StrictSynopsis synopsis;

	public static final ArrayList<MemberDoc> getPrivateMembers(
			MemberDoc[] members) {

		if (members == null) {

			throw new IllegalArgumentException("Parameter members is null!");
		}

		ArrayList<MemberDoc> list = new ArrayList<MemberDoc>();

		if ((members != null) && (members.length > 0)) {

			for (int i = 0; i < members.length; i++)

				if (members[i].isPrivate()) {
					list.add(members[i]);
				}
		}

		return list;
	}

	public static final ArrayList<MemberDoc> getPackageMembers(
			MemberDoc[] members) {

		if (members == null) {

			throw new IllegalArgumentException("Parameter members is null!");
		}

		ArrayList<MemberDoc> list = new ArrayList<MemberDoc>();

		if ((members != null) && (members.length > 0)) {

			for (int i = 0; i < members.length; i++)

				if (members[i].isPackagePrivate()) {
					list.add(members[i]);
				}
		}

		return list;
	}

	public static final ArrayList<MemberDoc> getProtectedMembers(
			MemberDoc[] members) {

		if (members == null) {

			throw new IllegalArgumentException("Parameter members is null!");
		}

		ArrayList<MemberDoc> list = new ArrayList<MemberDoc>();

		if ((members != null) && (members.length > 0)) {

			for (int i = 0; i < members.length; i++)

				if (members[i].isProtected()) {
					list.add(members[i]);
				}
		}

		return list;
	}

	public static final ArrayList<MemberDoc> getPublicMembers(
			MemberDoc[] members) {

		if (members == null) {

			throw new IllegalArgumentException("Parameter members is null!");
		}

		ArrayList<MemberDoc> list = new ArrayList<MemberDoc>();

		if ((members != null) && (members.length > 0)) {

			for (int i = 0; i < members.length; i++)

				if (members[i].isPublic()) {
					list.add(members[i]);
				}
		}

		return list;
	}

	public static final LinkedHashMap<String, ArrayList<Tag>> createTagMap(
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
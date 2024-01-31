/*
 * ### Copyright (C) 2005-2008 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.doclet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.doclet.doc.DocManager;
import org.dbdoclet.doclet.statistic.ClassesPerPackage;
import org.dbdoclet.doclet.statistic.DirectKnownSubclasses;
import org.dbdoclet.doclet.statistic.LabeledInteger;
import org.dbdoclet.doclet.statistic.TotalsDiagram;

public class StatisticData {

	private static Log logger = LogFactory.getLog(StatisticData.class);

	private TreeMap<Integer, ArrayList<String>> classesPerPackageMap;
	private HashMap<String, ArrayList<TypeElement>> directKnownSubclassesMap;
	private TreeMap<String, TreeMap<String, TypeElement>> pkgMap;
	private int totalClasses = 0;
	private int totalFields = 0;
	private int totalMethods = 0;
	private int totalPackagePrivateClasses = 0;
	private int totalPackagePrivateFields = 0;
	private int totalPackagePrivateMethods = 0;
	private int totalPackages = 0;
	private int totalPrivateFields = 0;
	private int totalPrivateMethods = 0;
	private int totalProtectedFields = 0;
	private int totalProtectedMethods = 0;
	private int totalPublicClasses = 0;
	private int totalPublicFields = 0;
	private int totalPublicMethods = 0;

	private DocManager docManager;

	public StatisticData() throws IOException {

		try {
			System.setProperty("java.awt.headless", "true");
		} catch (Throwable oops) {
			logger.error("Can't set system property java.awt.headless", oops);
		}
	}

	public ClassesPerPackage getClassesPerPackageDiagram() throws IOException {

		// System.out.println("Creating classes per packages diagram...");

		int max = 20;
		int index = 0;
		Integer key;

		Iterator<Integer> iterator = classesPerPackageMap.keySet().iterator();
		ClassesPerPackage classesPerPackage = CDI
				.getInstance(ClassesPerPackage.class);

		while (iterator.hasNext()) {

			key = iterator.next();

			ArrayList<String> list = classesPerPackageMap.get(key);
			Collections.sort(list);

			for (String name : list) {

				index++;
				classesPerPackage.add(new LabeledInteger(key, name));

				if (index > max) {
					break;
				}
			}

			if (index > max) {
				break;
			}
		}

		return classesPerPackage;
	}

	public DirectKnownSubclasses getDirectKnownSubclassesDiagram()
			throws IOException {

		// System.out.println("Creating direct known subclasses diagram...");

		DirectKnownSubclasses directKnownSubclasses = CDI
				.getInstance(DirectKnownSubclasses.class);

		Iterator<String> iterator = directKnownSubclassesMap.keySet()
				.iterator();

		TreeMap<Integer, ArrayList<String>> topTen = new TreeMap<Integer, ArrayList<String>>(
				Collections.reverseOrder());

		ArrayList<TypeElement> list;
		ArrayList<String> classnames;
		Integer count;
		int index;

		String name;

		while (iterator.hasNext()) {

			name = iterator.next();
			index = name.lastIndexOf('.');

			if (index != -1) {

				String pkgname = name.substring(0, index);

				if (pkgMap.get(pkgname) == null) {
					continue;
				}
			}

			list = directKnownSubclassesMap.get(name);

			if ((list != null) && (list.size() > 0)) {

				count = Integer.valueOf(list.size());
				classnames = topTen.get(count);

				if (classnames == null) {

					classnames = new ArrayList<String>();
					topTen.put(count, classnames);
				}

				classnames.add(name);
			}
		}

		int max = 10;
		index = 0;

		Iterator<Integer> topTenIterator = topTen.keySet().iterator();

		while (topTenIterator.hasNext()) {

			count = topTenIterator.next();

			classnames = topTen.get(count);

			Object[] sorted = classnames.toArray();
			Arrays.sort(sorted);

			for (int i = 0; i < sorted.length; i++) {

				name = (String) sorted[i];

				if ((name != null) && name.startsWith("java.lang")) {
					continue;
				}

				directKnownSubclasses.add(new LabeledInteger(count, name));
				index++;

				if (index > max) {
					break;
				}
			}

			if (index > max) {
				break;
			}
		}

		return directKnownSubclasses;
	}

	public ArrayList<TypeElement> getSubclasses(String qualifiedName) {

		if (qualifiedName == null) {

			throw new IllegalArgumentException(
					"The argument qualifiedName must not be null!");
		}

		ArrayList<TypeElement> subclasses = directKnownSubclassesMap
				.get(qualifiedName);

		if (subclasses == null) {
			subclasses = new ArrayList<TypeElement>();
		}

		return subclasses;
	}

	public TotalsDiagram getTotalsDiagram() throws IOException {

		TotalsDiagram totals = CDI.getInstance(TotalsDiagram.class);
		totals.setTotalPackages(totalPackages);
		totals.setTotalClasses(totalClasses);
		totals.setTotalFields(totalFields);
		totals.setTotalMethods(totalMethods);
		totals.setTotalPackagePrivateClasses(totalPackagePrivateClasses);
		totals.setTotalPackagePrivateFields(totalPackagePrivateFields);
		totals.setTotalPackagePrivateMethods(totalPackagePrivateMethods);
		totals.setTotalPrivateFields(totalPrivateFields);
		totals.setTotalPrivateMethods(totalPrivateMethods);
		totals.setTotalProtectedFields(totalProtectedFields);
		totals.setTotalProtectedMethods(totalProtectedMethods);
		totals.setTotalPublicClasses(totalPublicClasses);
		totals.setTotalPublicFields(totalPublicFields);
		totals.setTotalPublicMethods(totalPublicMethods);

		return totals;
	}

	private void analyseClasses(TreeMap<String, TypeElement> classMap) {

		if (classMap == null) {
			throw new IllegalArgumentException(
					"The argument classMap must not be null!");
		}

		totalClasses += classMap.size();

		TypeElement cdoc;
		String className;

		for (Iterator<String> iterator = classMap.keySet().iterator(); iterator
				.hasNext();) {

			className = iterator.next();
			cdoc = classMap.get(className);

			if (docManager.isPublic(cdoc)) {
				totalPublicClasses++;
			}

			if (docManager.isPackagePrivate(cdoc)) {
				totalPackagePrivateClasses++;
			}

			TypeElement superclass = docManager.getSuperclass(cdoc);
			ArrayList<TypeElement> subclasses;

			if (superclass != null) {

				subclasses = directKnownSubclassesMap.get(docManager.getQualifiedName(superclass));
				if (subclasses == null) {
					subclasses = new ArrayList<TypeElement>();
					directKnownSubclassesMap.put(
							docManager.getQualifiedName(superclass), subclasses);
				}

				subclasses.add(cdoc);
			}
		}

		for (Iterator<String> iterator = classMap.keySet().iterator(); iterator
				.hasNext();) {

			className = iterator.next();
			cdoc = classMap.get(className);

			Set<ExecutableElement> methods = docManager.getMethodElements(cdoc);
			totalMethods += methods.size();

			for (var method : methods) {

				if (docManager.isPublic(method)) {
					totalPublicMethods++;
				}

				if (docManager.isProtected(method)) {
					totalProtectedMethods++;
				}

				if (docManager.isPackagePrivate(method)) {
					totalPackagePrivateMethods++;
				}

				if (docManager.isPrivate(method)) {
					totalPrivateMethods++;
				}
			}
		}

		for (Iterator<String> iterator = classMap.keySet().iterator(); iterator
				.hasNext();) {

			className = iterator.next();
			cdoc = classMap.get(className);

			Set<VariableElement> fields = docManager.getFieldElements(cdoc);
			totalFields += fields.size();

			for (var field : fields) {

				if (docManager.isPublic(field)) {
					totalPublicFields++;
				}

				if (docManager.isProtected(field)) {
					totalProtectedFields++;
				}

				if (docManager.isPackagePrivate(field)) {
					totalPackagePrivateFields++;
				}

				if (docManager.isPrivate(field)) {
					totalPrivateFields++;
				}
			}
		}
	}

	public void init(TreeMap<String, TreeMap<String, TypeElement>> pkgMap) {

		if (pkgMap == null) {
			throw new IllegalArgumentException(
					"The field pkgMap must not be null!");
		}

		this.pkgMap = pkgMap;

		totalPackages = 0;
		totalClasses = 0;
		totalMethods = 0;
		totalFields = 0;
		totalPublicClasses = 0;
		totalPublicMethods = 0;
		totalPublicFields = 0;

		classesPerPackageMap = new TreeMap<>(Collections.reverseOrder());
		directKnownSubclassesMap = new HashMap<>();
		totalPackages = pkgMap.size();

		for (var pkgName : pkgMap.keySet()) {

			TreeMap<String, TypeElement> classMap = pkgMap.get(pkgName);
			Integer key = 0;

			if (classMap != null) {
				key = classMap.size();
				analyseClasses(classMap);
			}

			ArrayList<String> list = classesPerPackageMap.get(key);

			if (list == null) {
				list = new ArrayList<String>();
				classesPerPackageMap.put(key, list);
			}

			list.add(pkgName);
		}
	}

	public void setDocManager(DocManager docManager) {
		this.docManager = docManager;
	}
}
package org.dbdoclet.doclet.statistic;

import java.io.IOException;
import java.util.ArrayList;

import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;
import org.jfree.data.category.DefaultCategoryDataset;

public class TotalsDiagram extends Diagram {

	private int totalPackages = -1;
	private int totalClasses = -1;
	private int totalMethods = -1;
	private int totalFields = -1;
	private int totalPublicClasses = -1;
	private int totalPublicMethods = -1;
	private int totalPublicFields = -1;
	private int totalProtectedMethods = -1;
	private int totalProtectedFields = -1;
	private int totalPackagePrivateClasses = -1;
	private int totalPackagePrivateMethods = -1;
	private int totalPackagePrivateFields = -1;
	private int totalPrivateMethods = -1;
	private int totalPrivateFields = -1;

	@Override
	public String getImageHref() {
		return super.getImagesRef() + "Totals.png";
	}

	@Override
	public void createDiagram() throws IOException {

		String image = FileServices
				.appendFileName(getImagePath(), "Totals.png");
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(totalPackages,
				ResourceServices.getString(res, "C_PACKAGES"),
				ResourceServices.getString(res, "C_TOTALS"));
		dataset.addValue(totalClasses,
				ResourceServices.getString(res, "C_CLASSES"),
				ResourceServices.getString(res, "C_TOTALS"));
		dataset.addValue(totalMethods,
				ResourceServices.getString(res, "C_METHODS"),
				ResourceServices.getString(res, "C_TOTALS"));
		dataset.addValue(totalFields,
				ResourceServices.getString(res, "C_FIELDS"),
				ResourceServices.getString(res, "C_TOTALS"));

		dataset.addValue(totalPublicClasses,
				ResourceServices.getString(res, "C_CLASSES"), "Public");
		dataset.addValue(totalPublicMethods,
				ResourceServices.getString(res, "C_METHODS"), "Public");
		dataset.addValue(totalPublicFields,
				ResourceServices.getString(res, "C_FIELDS"), "Public");

		dataset.addValue(totalProtectedMethods,
				ResourceServices.getString(res, "C_METHODS"), "Protected");
		dataset.addValue(totalProtectedFields,
				ResourceServices.getString(res, "C_FIELDS"), "Protected");

		dataset.addValue(totalPackagePrivateClasses,
				ResourceServices.getString(res, "C_CLASSES"), "Package");
		dataset.addValue(totalPackagePrivateMethods,
				ResourceServices.getString(res, "C_METHODS"), "Package");
		dataset.addValue(totalPackagePrivateFields,
				ResourceServices.getString(res, "C_FIELDS"), "Package");

		dataset.addValue(totalPrivateMethods,
				ResourceServices.getString(res, "C_METHODS"), "Private");
		dataset.addValue(totalPrivateFields,
				ResourceServices.getString(res, "C_FIELDS"), "Private");

		createBarChart(ResourceServices.getString(res, "C_TOTALS"), dataset,
				image);
	}

	public int getTotalClasses() {
		return totalClasses;
	}

	public int getTotalFields() {
		return totalFields;
	}

	public int getTotalMethods() {
		return totalMethods;
	}

	public int getTotalPackagePrivateClasses() {
		return totalPackagePrivateClasses;
	}

	public int getTotalPackagePrivateFields() {
		return totalPackagePrivateFields;
	}

	public int getTotalPackagePrivateMethods() {
		return totalPackagePrivateMethods;
	}

	public int getTotalPackages() {
		return totalPackages;
	}

	public int getTotalPrivateFields() {
		return totalPrivateFields;
	}

	public int getTotalPrivateMethods() {
		return totalPrivateMethods;
	}

	public int getTotalProtectedFields() {
		return totalProtectedFields;
	}

	public int getTotalProtectedMethods() {
		return totalProtectedMethods;
	}

	public int getTotalPublicClasses() {
		return totalPublicClasses;
	}

	public int getTotalPublicFields() {
		return totalPublicFields;
	}

	public int getTotalPublicMethods() {
		return totalPublicMethods;
	}

	public void setTotalClasses(int totalClasses) {
		this.totalClasses = totalClasses;
	}

	public void setTotalFields(int totalFields) {
		this.totalFields = totalFields;
	}

	public void setTotalMethods(int totalMethods) {
		this.totalMethods = totalMethods;
	}

	public void setTotalPackagePrivateClasses(int totalPackagePrivateClasses) {
		this.totalPackagePrivateClasses = totalPackagePrivateClasses;
	}

	public void setTotalPackagePrivateFields(int totalPackagePrivateFields) {
		this.totalPackagePrivateFields = totalPackagePrivateFields;
	}

	public void setTotalPackagePrivateMethods(int totalPackagePrivateMethods) {
		this.totalPackagePrivateMethods = totalPackagePrivateMethods;
	}

	public void setTotalPackages(int totalPackages) {
		this.totalPackages = totalPackages;
	}

	public void setTotalPrivateFields(int totalPrivateFields) {
		this.totalPrivateFields = totalPrivateFields;
	}

	public void setTotalPrivateMethods(int totalPrivateMethods) {
		this.totalPrivateMethods = totalPrivateMethods;
	}

	public void setTotalProtectedFields(int totalProtectedFields) {
		this.totalProtectedFields = totalProtectedFields;
	}

	public void setTotalProtectedMethods(int totalProtectedMethods) {
		this.totalProtectedMethods = totalProtectedMethods;
	}

	public void setTotalPublicClasses(int totalPublicClasses) {
		this.totalPublicClasses = totalPublicClasses;
	}

	public void setTotalPublicFields(int totalPublicFields) {
		this.totalPublicFields = totalPublicFields;
	}

	public void setTotalPublicMethods(int totalPublicMethods) {
		this.totalPublicMethods = totalPublicMethods;
	}

	@Override
	public ArrayList<LabeledInteger> getItemList() {

		ArrayList<LabeledInteger> list = super.getItemList();

		list.clear();
		list.add(new LabeledInteger(totalPackages, ResourceServices.getString(
				res, "C_TOTALS")
				+ " "
				+ ResourceServices.getString(res, "C_PACKAGES")));
		list.add(new LabeledInteger(totalClasses, ResourceServices.getString(
				res, "C_TOTALS")
				+ " "
				+ ResourceServices.getString(res, "C_CLASSES")));
		list.add(new LabeledInteger(totalFields, ResourceServices.getString(
				res, "C_TOTALS")
				+ " "
				+ ResourceServices.getString(res, "C_FIELDS")));
		list.add(new LabeledInteger(totalMethods, ResourceServices.getString(
				res, "C_TOTALS")
				+ " "
				+ ResourceServices.getString(res, "C_METHODS")));

		list.add(new LabeledInteger(totalPublicClasses, "Public "
				+ ResourceServices.getString(res, "C_CLASSES")));
		list.add(new LabeledInteger(totalPublicFields, "Public "
				+ ResourceServices.getString(res, "C_FIELDS")));
		list.add(new LabeledInteger(totalPublicMethods, "Public "
				+ ResourceServices.getString(res, "C_METHODS")));

		list.add(new LabeledInteger(totalProtectedFields, "Protected "
				+ ResourceServices.getString(res, "C_FIELDS")));
		list.add(new LabeledInteger(totalProtectedMethods, "Protected "
				+ ResourceServices.getString(res, "C_METHODS")));

		list.add(new LabeledInteger(totalPackagePrivateClasses, "Package "
				+ ResourceServices.getString(res, "C_CLASSES")));
		list.add(new LabeledInteger(totalPackagePrivateFields, "Package "
				+ ResourceServices.getString(res, "C_FIELDS")));
		list.add(new LabeledInteger(totalPackagePrivateMethods, "Package "
				+ ResourceServices.getString(res, "C_METHODS")));

		list.add(new LabeledInteger(totalPrivateFields, "Private "
				+ ResourceServices.getString(res, "C_FIELDS")));
		list.add(new LabeledInteger(totalPrivateMethods, "Private "
				+ ResourceServices.getString(res, "C_METHODS")));

		return list;
	}

}

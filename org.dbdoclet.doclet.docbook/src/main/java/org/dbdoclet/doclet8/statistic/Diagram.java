package org.dbdoclet.doclet8.statistic;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.dbdoclet.doclet8.docbook.DbdScript;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.tag.docbook.Colspec;
import org.dbdoclet.tag.docbook.DocBookTagFactory;
import org.dbdoclet.tag.docbook.Entry;
import org.dbdoclet.tag.docbook.Informaltable;
import org.dbdoclet.tag.docbook.Para;
import org.dbdoclet.tag.docbook.Row;
import org.dbdoclet.tag.docbook.Tbody;
import org.dbdoclet.tag.docbook.Tgroup;
import org.dbdoclet.xiphias.ImageServices;
import org.dbdoclet.xiphias.dom.ProcessingInstructionImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.TextAnchor;

public abstract class Diagram {

	private static final int IMAGE_HEIGHT = 300;
	private static final int IMAGE_WIDTH = 500;

	private String imageHeight;
	private String imageHref;
	private String imagePath;
	private String imageRef;
	private String imageWidth;
	private ArrayList<LabeledInteger> itemList;

	@Inject
	protected ResourceBundle res;

	@Inject
	DbdScript script;

	public Diagram() {

		itemList = new ArrayList<LabeledInteger>();
	}

	public void add(LabeledInteger item) {

		if (item == null) {
			throw new IllegalArgumentException(
					"The argument item must not be null!");
		}

		itemList.add(item);
	}

	protected void createBarChart(String title, CategoryDataset dataset,
			String image) throws IOException {

		// JFreeChart chart = ChartFactory.createBarChart3D(title, title, "",
		// dataset,
		// PlotOrientation.VERTICAL, true, true, true);

		// JFreeChart chart = ChartFactory.createBarChart3D(title, title, "",
		// dataset,
		// PlotOrientation.HORIZONTAL, true, true, true);

		JFreeChart chart = ChartFactory.createBarChart(title, title, "",
				dataset, PlotOrientation.VERTICAL, true, true, true);

		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis axis = plot.getDomainAxis();

		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setBaseItemLabelsVisible(true);

		ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER,
				TextAnchor.CENTER, TextAnchor.CENTER, 0.0);
		renderer.setBasePositiveItemLabelPosition(p);

		BarRenderer r = (BarRenderer) renderer;
		r.setMaximumBarWidth(0.1);

		File imageFile = new File(image);

		ChartUtilities.saveChartAsPNG(imageFile, chart, IMAGE_WIDTH,
				IMAGE_HEIGHT);

		List<String> imageFormatList = script.getImageDataFormats();

		imageWidth = String.valueOf(ImageServices.getWidth(imageFile)) + "px";
		imageHeight = String.valueOf(ImageServices.getHeight(imageFile)) + "px";

		if (imageFormatList.contains("BASE64")) {

			String imagePath = FileServices.getFileBase(imageFile) + ".base64";
			FileServices.writeFromString(new File(imagePath),
					ImageServices.toXml(imageFile));

			if (script.isAbsoluteImagePathEnabled() == true) {

				if (FileServices.isAbsolutePath(imagePath) == false) {
					imagePath = FileServices.appendFileName(
							script.getDestinationDirectory(), imagePath);
				}

				imageFile = new File(imagePath);
				setImageHref(imageFile.toURI().toURL().toString());

			} else {

				setImageHref(imagePath);
			}
		}
	}

	public abstract void createDiagram() throws IOException;

	public Informaltable createTable(String title,
			List<LabeledInteger> itemList, DocBookTagFactory dbfactory) {

		if (itemList == null) {
			throw new IllegalArgumentException(
					"The argument list must not be null!");
		}

		if (dbfactory == null) {
			throw new IllegalArgumentException(
					"The argument dbfactory must not be null!");
		}

		Informaltable table = dbfactory.createInformaltable();
		table.setRole("parameter");
		table.setFrame("all");

		Tgroup tgroup = dbfactory.createTgroup();
		tgroup.setCols(2);

		Colspec c1 = dbfactory.createColspec();
		tgroup.appendChild(c1);
		c1.setAttribute("colname", "c1");
		c1.setAttribute("colwidth", "4*");

		Colspec c2 = dbfactory.createColspec();
		tgroup.appendChild(c2);
		c2.setAttribute("colname", "c2");
		c2.setAttribute("colwidth", "1*");

		table.appendChild(tgroup);

		Tbody tbody = dbfactory.createTbody();
		tgroup.appendChild(tbody);

		Row row;
		Entry entry;
		Para para;

		row = dbfactory.createRow();
		tbody.appendChild(row);

		entry = dbfactory.createEntry(title);
		row.appendChild(entry);
		entry.setAlign("left");
		entry.setNameSt("c1");
		entry.setNameEnd("c2");
		entry.appendChild(new ProcessingInstructionImpl("dbfo",
				"bgcolor=\"#eeeeee\""));

		for (LabeledInteger item : itemList) {

			row = dbfactory.createRow();
			tbody.appendChild(row);

			entry = dbfactory.createEntry();
			row.appendChild(entry);

			para = dbfactory.createPara(item.getLabel());
			entry.appendChild(para);

			entry = dbfactory.createEntry();
			row.appendChild(entry);

			para = dbfactory.createPara(String.valueOf(item.getInteger()));
			entry.appendChild(para);

		}

		return table;
	}

	public String getImageHeight() {
		return imageHeight;
	}

	public String getImageHref() {
		return imageHref;
	}

	public String getImagePath() throws IOException {

		if (imagePath == null) {
			imagePath = FileServices.appendPath(
					script.getDestinationDirectory(), script.getImagePath());
			imagePath = FileServices.appendPath(imagePath, "statistics");
			FileServices.createPath(imagePath);
		}

		return imagePath;
	}

	public String getImagesRef() {

		if (imageRef == null) {
			imageRef = "./"
					+ StringServices.replace(script.getImagePath(), "\\", "/")
					+ "/statistics/";
		}

		return imageRef;
	}

	public String getImageWidth() {
		return imageWidth;
	}

	public ArrayList<LabeledInteger> getItemList() {
		return itemList;
	}

	public boolean isEmpty() {
		return itemList.isEmpty();
	}

	public void setImageHref(String imageHref) {
		this.imageHref = imageHref;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setImageRef(String imageRef) {
		this.imageRef = imageRef;
	}

	public void setItemList(ArrayList<LabeledInteger> itemList) {
		this.itemList = itemList;
	}
}

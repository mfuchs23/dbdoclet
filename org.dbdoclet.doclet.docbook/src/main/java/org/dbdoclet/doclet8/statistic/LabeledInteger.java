package org.dbdoclet.doclet8.statistic;

public class LabeledInteger {

    private String category;
    private String label;
    private Integer integer;

    public LabeledInteger(Integer integer, String label) {

	this(integer, "", label);
    }
    public LabeledInteger(Integer integer, String category, String label) {
	
	this.integer = integer;
	this.category = category;
	this.label = label;
    }

    public String getCategory() {
        return category;
    }
	
    public Integer getInteger() {
        return integer;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setInteger(Integer integer) {
        this.integer = integer;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
}

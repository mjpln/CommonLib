package com.knowology.Bean;
/**
 * 属性类
 * @author xsheng
 */
public class Attribute {
	// 属性id
	private String attrID;
	// 资源类型
	private String resourceType;
	// 对应列
	private String columnNum;
	// 属性名
	private String attrName;
	// 数据类型
	private String dataType;
	// 展现形式
	private String shape;
	public String getAttrID() {
		return attrID;
	}
	public void setAttrID(String attrID) {
		this.attrID = attrID;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getColumnNum() {
		return columnNum;
	}
	public void setColumnNum(String columnNum) {
		this.columnNum = columnNum;
	}
	public String getAttrName() {
		return attrName;
	}
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
}

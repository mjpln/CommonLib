package com.knowology.Bean;

import java.util.List;

public class User {
	// 用户IP
	private String userIP;
	// 用户ID
	private String userID;
	// 用户名
	private String userName;
	// 密码
	private String password;
	// 性别
	private String gender;
	// 电话
	private String phoneNum;
	// 组织机构
	private String customer;
	// 部门
	private String department;
	// 角色
	private List<Role> roleList;
	//菜单默认加载项
	private String loadMenuName;
	//角色关联菜单
	private String menuName;
	//四层结构串
	private String industryOrganizationApplication;
	//Brand
	private String brand;
	
	private String realindustryOrganizationApplication;
	
	//用户serviceRoot 
	private String[] serviceRoot;
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) { 
		this.userID = userID;
	}
	public List<Role> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getUserIP() {
		return userIP;
	}
	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}
	public String getLoadMenuName() {
		return loadMenuName;
	}
	public void setLoadMenuName(String loadMenuName) {
		this.loadMenuName = loadMenuName;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getIndustryOrganizationApplication() {
		return industryOrganizationApplication;
	}
	public void setIndustryOrganizationApplication(
			String industryOrganizationApplication) {
		this.industryOrganizationApplication = industryOrganizationApplication;
		if(industryOrganizationApplication != null && !industryOrganizationApplication.equals(""))
		this.brand = industryOrganizationApplication.split("->")[1] + "问题库";
	}
	public String getBrand() {
		return brand;
	}
	public String getRealindustryOrganizationApplication() {
		return realindustryOrganizationApplication;
	}
	public void setRealindustryOrganizationApplication(
			String realindustryOrganizationApplication) {
		this.realindustryOrganizationApplication = realindustryOrganizationApplication;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String[] getServiceRoot() {
		return serviceRoot;
	}
	public void setServiceRoot(String[] serviceRoot) {
		this.serviceRoot = serviceRoot;
	}


}

package com.knowology.Bean;

import java.util.List;


public class Role {

	// 角色ID
	private String roleID;
	// 角色名称
	private String roleName;
	// 角色归属机构（四层结构）
	private String belongCom; 
	// 角色的资源权限
	private List<RoleResourceAccessRule> roleResourcePrivileges;
	//角色关联菜单
	private String menuName;
	//角色默认加载菜单
	private String loadMenuName;
	
	
	public Role() {
		
	}
	
	public Role(String roleID,String roleName,List<RoleResourceAccessRule> roleResourcePrivileges, String belongCom,String menuName,String loadMenuName) {
		this.roleID = roleID;
		this.roleName = roleName;
		this.belongCom = belongCom;
		this.roleResourcePrivileges = roleResourcePrivileges;
		this.menuName = menuName;
		this.loadMenuName = loadMenuName;
	}
	
	public String getRoleID() {
		return roleID;
	}
	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName; 
	}
	public List<RoleResourceAccessRule> getRoleResourcePrivileges() {
		return roleResourcePrivileges;
	}
	public void setRoleResourcePrivileges(
			List<RoleResourceAccessRule> roleResourcePrivileges) {
		this.roleResourcePrivileges = roleResourcePrivileges;
	}
	public String getBelongCom() {
		return belongCom;
	}

	public void setBelongCom(String belongCom) {
		this.belongCom = belongCom;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getLoadMenuName() {
		return loadMenuName;
	}

	public void setLoadMenuName(String loadMenuName) {
		this.loadMenuName = loadMenuName;
	} 
	

}

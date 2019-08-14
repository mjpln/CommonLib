package com.knowology.Bean;

import java.util.List;
import java.util.Map;

public class RoleResourceAccessRule {
	// 资源类型(业务，摘要)
	private String resourceType;
	// 操作方式 (A\D\U\(S:查看)，如果多个选项，以“|”分隔）
	private String operateLimit;
	// 用户录入的资源名
	private List<String> resourceNames;
	// 是否关联子业务
	private String isRelateChild;
	// 用户给角色配置的属性信息
	private Map<String,String> accessResourceMap;
	
	public RoleResourceAccessRule() {
		
	}
	
	public String getIsRelateChild() {
		return isRelateChild;
	}

	public void setIsRelateChild(String isRelateChild) {
		this.isRelateChild = isRelateChild;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getOperateLimit() {
		return operateLimit;
	}

	public void setOperateLimit(String operateLimit) {
		this.operateLimit = operateLimit;
	}

	public Map<String, String> getAccessResourceMap() {
		return accessResourceMap;
	}

	public void setAccessResourceMap(Map<String, String> accessResourceMap) {
		this.accessResourceMap = accessResourceMap;
	}

	public List<String> getResourceNames() {
		return resourceNames;
	}

	public void setResourceNames(List<String> resourceNames) {
		this.resourceNames = resourceNames;
	}


}

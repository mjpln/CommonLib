package com.knowology.Bean;

public class ImportNormalqueryBean {
	private String normalquery;
	private String responsetype;
	private String interacttype;


	public ImportNormalqueryBean() {
	}

	public ImportNormalqueryBean(String normalquery, String responsetype,
			String interacttype) {
		this.normalquery = normalquery;
		this.responsetype = responsetype;
		this.interacttype = interacttype;
	}


	public String getNormalquery() {
		return normalquery;
	}
	public void setNormalquery(String normalquery) {
		this.normalquery = normalquery;
	}
	public String getResponsetype() {
		return responsetype;
	}
	public void setResponsetype(String responsetype) {
		this.responsetype = responsetype;
	}
	public String getInteracttype() {
		return interacttype;
	}
	public void setInteracttype(String interacttype) {
		this.interacttype = interacttype;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((normalquery == null) ? 0 : normalquery.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportNormalqueryBean other = (ImportNormalqueryBean) obj;
		if (normalquery == null) {
			if (other.normalquery != null)
				return false;
		} else if (!normalquery.equals(other.normalquery))
			return false;
		return true;
	}
	
}

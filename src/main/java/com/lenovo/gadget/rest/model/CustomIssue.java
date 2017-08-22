package com.lenovo.gadget.rest.model;


public class CustomIssue{
	
	String issueType;
	int count;
	public CustomIssue(String issueType, int count) {
		this.issueType = issueType;
		this.count = count;
	}
	public String getIssueType() {
		return issueType;
	}
	public int getCount() {
		return count;
	}
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((issueType == null) ? 0 : issueType.hashCode());
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
		CustomIssue other = (CustomIssue) obj;
		if (issueType == null) {
			if (other.issueType != null)
				return false;
		} else if (!issueType.equals(other.issueType))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CustomIssue [issueType=" + issueType + ", count=" + count + "]";
	}
}
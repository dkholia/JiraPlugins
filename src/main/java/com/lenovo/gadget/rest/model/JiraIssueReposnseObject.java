package com.lenovo.gadget.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class JiraIssueReposnseObject implements Serializable{

	private static final long serialVersionUID = 4498939040495072025L;
	
	private Map<String, ArrayList<CustomIssue>> issueMap = new HashMap<String, ArrayList<CustomIssue>>();
	
	public Map<String, ArrayList<CustomIssue>> getIssueMap() {
		return issueMap;
	}
	public void setIssueMap(Map<String, ArrayList<CustomIssue>> issueMap) {
		this.issueMap = issueMap;
	}
	@Override
	public String toString() {
		return "JiraIssueReposnseObject [issueMap=" + issueMap + "]";
	} 
}

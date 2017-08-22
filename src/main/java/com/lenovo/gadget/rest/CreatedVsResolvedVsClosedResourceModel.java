package com.lenovo.gadget.rest;

import javax.xml.bind.annotation.*;

import com.lenovo.gadget.rest.model.JiraIssueReposnseObject;
@XmlRootElement(name = "responseData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreatedVsResolvedVsClosedResourceModel {

    @XmlElement(name = "issueType")
    private JiraIssueReposnseObject response;

    public CreatedVsResolvedVsClosedResourceModel() {}

    public CreatedVsResolvedVsClosedResourceModel(JiraIssueReposnseObject response) {
        this.response = response;
    }

    public JiraIssueReposnseObject getMessage() {
        return response;
    }

    public void setMessage(JiraIssueReposnseObject response) {
        this.response = response;
    }
}
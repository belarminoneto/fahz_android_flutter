package br.com.avanade.fahz.model;

import java.util.List;

public class FollowupUpdate {


    private List<FollowupScholarship> scholarshipFollowups;
    private List<String> documents;

    public List<FollowupScholarship> getFollowupScholarships() {
        return scholarshipFollowups;
    }

    public void setFollowupScholarships(List<FollowupScholarship> followupScholarships) {
        this.scholarshipFollowups = followupScholarships;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

}

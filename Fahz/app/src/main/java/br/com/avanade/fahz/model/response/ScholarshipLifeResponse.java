package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScholarshipLifeResponse {

    @SerializedName("scholarshipLife")
    @Expose
    private br.com.avanade.fahz.model.ScholarshipLifeResponse scholarshipLifeResponse;

    public br.com.avanade.fahz.model.ScholarshipLifeResponse getScholarshipLifeResponse() {
        return scholarshipLifeResponse;
    }

    public void setScholarshipLifeResponse(br.com.avanade.fahz.model.ScholarshipLifeResponse scholarshipLifeResponse) {
        this.scholarshipLifeResponse = scholarshipLifeResponse;
    }
}

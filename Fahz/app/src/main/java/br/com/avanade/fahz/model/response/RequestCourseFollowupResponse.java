package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.RequestCourseFollowup;
import br.com.avanade.fahz.model.SystemBehaviorModel;

public class RequestCourseFollowupResponse {

    @SerializedName("requestCourseFollowup")
    @Expose
    private RequestCourseFollowup requestCourseFollowup;

    public RequestCourseFollowup getRequestCourseFollowup() {
        return requestCourseFollowup;
    }

    public void setRequestCourseFollowup(RequestCourseFollowup requestCourseFollowup) {
        this.requestCourseFollowup = requestCourseFollowup;
    }
}

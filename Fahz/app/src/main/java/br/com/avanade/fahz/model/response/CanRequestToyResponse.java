package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CanRequestToyResponse {

    @SerializedName("holdertoycampaign")
    @Expose
    private DataResponse holdertoycampaign;

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String messageIdentifier;

    public DataResponse getHoldertoycampaign() {
        return holdertoycampaign;
    }

    public Boolean getCommited() {
        return commited;
    }

    public String getMessageIdentifier() {
        return messageIdentifier;
    }

    public class DataResponse {
        @SerializedName("CanRequest")
        @Expose
        private Boolean canRequest;

        @SerializedName("Message")
        @Expose
        private String message;

        @SerializedName("Observation")
        @Expose
        private String observation;

        public Boolean getCanRequest() {
            return canRequest;
        }

        public String getMessage() {
            return message;
        }

        public String getObservation() {
            return observation;
        }
    }
}

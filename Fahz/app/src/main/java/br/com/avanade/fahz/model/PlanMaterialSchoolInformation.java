package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class PlanMaterialSchoolInformation {
    @SerializedName("PlanMaterialSchool")
    @Expose
    private PlanMaterialSchool planMaterialSchool;

    public PlanMaterialSchool getPlanMaterialSchool() {
        return planMaterialSchool;
    }

    public void setPlanMaterialSchool(PlanMaterialSchool planMaterialSchool) {
        this.planMaterialSchool = planMaterialSchool;
    }


    public class PlanMaterialSchool {
        @SerializedName("StartRequest")
        @Expose
        private String startRequest;
        @SerializedName("EndRequest")
        @Expose
        private String endRequest;
        @SerializedName("StartValidityVoucher")
        @Expose
        private String startValidityVoucher;
        @SerializedName("EndValidityVoucher")
        @Expose
        private String endValidityVoucher;
        @SerializedName("Rules")
        @Expose
        private String rules;

        public String getStartRequest() {
            return startRequest;
        }

        public void setStartRequest(String startRequest) {
            this.startRequest = startRequest;
        }

        public String getEndRequest() {
            return endRequest;
        }

        public void setEndRequest(String endRequest) {
            this.endRequest = endRequest;
        }

        public String getStartValidityVoucher() {
            return startValidityVoucher;
        }

        public void setStartValidityVoucher(String startValidityVoucher) {
            this.startValidityVoucher = startValidityVoucher;
        }

        public String getEndValidityVoucher() {
            return endValidityVoucher;
        }

        public void setEndValidityVoucher(String endValidityVoucher) {
            this.endValidityVoucher = endValidityVoucher;
        }

        public String getRules() {
            return rules;
        }

        public void setRules(String rules) {
            this.rules = rules;
        }
    }
}

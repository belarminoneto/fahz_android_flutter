package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SchoolSuppliesCanRequest {

    @SerializedName("UserRequestSchool")
    @Expose
    private UserRequestSchool userRequestSchool;

    public UserRequestSchool getUserRequestSchool() {
        return userRequestSchool;
    }

    public void setUserRequestSchool(UserRequestSchool userRequestSchool) {
        this.userRequestSchool = userRequestSchool;
    }


    public class UserRequestSchool {
        @SerializedName("IsCard")
        @Expose
        private Boolean isCard;
        @SerializedName("IsRefund")
        @Expose
        private Boolean isRefund;
        @SerializedName("PlanId")
        @Expose
        private Integer planId;
        @SerializedName("AlreayHasCardBenefit")
        @Expose
        private Boolean alreayHasCardBenefit;
        @SerializedName("HolderHasBankData")
        @Expose
        private Boolean HolderHasBankData;
        @SerializedName("AlreayHasRefundBenefit")
        @Expose
        private Boolean AlreayHasRefundBenefit;

        public Boolean getIsCard() {
            return isCard;
        }

        public void setIsCard(Boolean isCard) {
            this.isCard = isCard;
        }

        public Boolean getIsRefund() {
            return isRefund;
        }

        public void setIsRefund(Boolean isRefund) {
            this.isRefund = isRefund;
        }

        public Integer getPlanId() {
            return planId;
        }

        public void setPlanId(Integer planId) {
            this.planId = planId;
        }

        public Boolean getAlreayHasCardBenefit() {
            return alreayHasCardBenefit;
        }

        public void setAlreayHasCardBenefit(Boolean alreayHasCardBenefit) {
            this.alreayHasCardBenefit = alreayHasCardBenefit;
        }

        public Boolean getHolderHasBankData() {
            return HolderHasBankData;
        }

        public void setHolderHasBankData(Boolean holderHasBankData) {
            HolderHasBankData = holderHasBankData;
        }

        public Boolean getAlreayHasRefundBenefit() {
            return AlreayHasRefundBenefit;
        }

        public void setAlreayHasRefundBenefit(Boolean alreayHasRefundBenefit) {
            AlreayHasRefundBenefit = alreayHasRefundBenefit;
        }
    }
}

package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FinancialPlanList {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Period")
    @Expose
    private String period;
    @SerializedName("PeriodDate")
    @Expose
    private String periodDate;
    @SerializedName("AmountPeriod")
    @Expose
    private Double amountPeriod;
    @SerializedName("AmountRefundPeriod")
    @Expose
    private Double amountRefundPeriod;
    @SerializedName("AmountPaid")
    @Expose
    private Double amountPaid;
    @SerializedName("AmountRefund")
    @Expose
    private Double amountRefund;
    @SerializedName("ApprovalDate")
    @Expose
    private Object approvalDate;
    @SerializedName("RequestDate")
    @Expose
    private String requestDate;
    @SerializedName("CreditDate")
    @Expose
    private Object creditDate;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("Justification")
    @Expose
    private Object justification;
    @SerializedName("ScholarshipLife")
    @Expose
    private ScholarshipLife scholarshipLife;
    private boolean Selected;

    private Date dateTocheck;

    public Date getDateTocheck() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            dateTocheck = format.parse(periodDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTocheck;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(String periodDate) {

        this.periodDate = periodDate;
    }

    public Double getAmountPeriod() {
        return amountPeriod;
    }

    public void setAmountPeriod(Double amountPeriod) {
        this.amountPeriod = amountPeriod;
    }

    public Double getAmountRefundPeriod() {
        return amountRefundPeriod;
    }

    public void setAmountRefundPeriod(Double amountRefundPeriod) {
        this.amountRefundPeriod = amountRefundPeriod;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getAmountRefund() {
        return amountRefund;
    }

    public void setAmountRefund(Double amountRefund) {
        this.amountRefund = amountRefund;
    }

    public Object getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Object approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public Object getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(Object creditDate) {
        this.creditDate = creditDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getJustification() {
        return justification;
    }

    public void setJustification(Object justification) {
        this.justification = justification;
    }

    public ScholarshipLife getScholarshipLife() {
        return scholarshipLife;
    }

    public void setScholarshipLife(ScholarshipLife scholarshipLife) {
        this.scholarshipLife = scholarshipLife;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }
}

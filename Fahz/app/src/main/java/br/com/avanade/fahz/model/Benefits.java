package br.com.avanade.fahz.model;

import com.google.gson.annotations.SerializedName;

public class Benefits {
    @SerializedName("CPF")
    public String cpf;
    @SerializedName("Benefit")
    public Benefit benefit;
    @SerializedName("Status")
    public int status;
    @SerializedName("Message")
    public String message;
    @SerializedName("StartDate")
    public String startDate;
    @SerializedName("EndDate")
    public String endDate;
    @SerializedName("UserHasIt")
    public boolean userHasIt;
    @SerializedName("UserCanSchedule")
    public boolean userCanSchedule;
    @SerializedName("UserCanSeePregnantProgram")
    public boolean userCanSeePregnantProgram;
    @SerializedName("CanSeeHealthProfile")
    public boolean userCanSeeHealthProfile;
    @SerializedName("CanChangeBenefit")
    public boolean userCanChangeBenefit;
    @SerializedName("UserIsPregnant")
    public boolean userIsPregnant;
    @SerializedName("CanMakeAttendance")
    public boolean userCanMakeAttendance;
    @SerializedName("HolderHasBenefit")
    public boolean userHolderHasBenefit;

}

package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.List;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.controls.DateEditText;
import br.com.avanade.fahz.model.response.AddressProntmed;

public class ListOfAppointmentsItemResponse {

    @SerializedName("cpfPatient")
    @Expose
    private String cpfPatient;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isHolder")
    @Expose
    private Boolean isHolder;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("hour")
    @Expose
    private String hour;
    @SerializedName("doctorName")
    @Expose
    private String doctorName;
    @SerializedName("Speciality")
    @Expose
    private List<String> speciality = null;
    @SerializedName("idAppointment")
    @Expose
    private Integer idAppointment;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("canRemove")
    @Expose
    private Boolean canRemove;
    @SerializedName("address")
    @Expose
    private AddressProntmed address;
    @SerializedName("messageCanNotRemove")
    @Expose
    private String messageCanNotRemove;

    public String getCpfPatient() {
        return cpfPatient;
    }

    public void setCpfPatient(String cpfPatient) {
        this.cpfPatient = cpfPatient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsHolder() {
        return isHolder;
    }

    public void setIsHolder(Boolean isHolder) {
        this.isHolder = isHolder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public List<String> getSpeciality() {
        return speciality;
    }

    public void setSpeciality(List<String> speciality) {
        this.speciality = speciality;
    }

    public Integer getIdAppointment() {
        return idAppointment;
    }

    public void setIdAppointment(Integer idAppointment) {
        this.idAppointment = idAppointment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getCanRemove() {
        return canRemove;
    }

    public void setCanRemove(Boolean canRemove) {
        this.canRemove = canRemove;
    }

    public AddressProntmed getAddress() {
        return address;
    }

    public void setAddress(AddressProntmed address) {
        this.address = address;
    }

    public String getMessageCanNotRemove() {
        return messageCanNotRemove;
    }

    public void setMessageCanNotRemove(String messageCanNotRemove) {
        this.messageCanNotRemove = messageCanNotRemove;
    }

    private Calendar scheduleDate;

    private void updateScheduleDate() {
        if (scheduleDate == null && date != null && !date.isEmpty()) {
            scheduleDate = DateEditText.parseToCalendar(date);
        }
    }

    public String getMonth() {
        updateScheduleDate();
        String month = "";
        switch (scheduleDate.get(Calendar.MONTH)) {
            case 0:
                month = "JAN";
                break;
            case 1:
                month = "FEV";
                break;
            case 2:
                month = "MAR";
                break;
            case 3:
                month = "ABR";
                break;
            case 4:
                month = "MAI";
                break;
            case 5:
                month = "JUN";
                break;
            case 6:
                month = "JUL";
                break;
            case 7:
                month = "AGO";
                break;
            case 8:
                month = "SET";
                break;
            case 9:
                month = "OUT";
                break;
            case 10:
                month = "NOV";
                break;
            case 11:
                month = "DEZ";
                break;
        }
        return month;
    }

    public String getDay() {
        updateScheduleDate();
        String day = String.valueOf(scheduleDate.get(Calendar.DATE));
        return day.length() == 1 ? "0" + day : day;
    }

    public String getHourAndMinutes() {
        return hour;
    }

    public int getStatusColor() {
        int color = R.color.yellow;
        switch (status) {
            case "Agendado":
                color = R.color.green;
                break;
            case "Cancelado":
                color = R.color.red_info;
                break;
        }
        return color;
    }

    public int getBeneficiaryTypeStringResource() {
        if (isHolder) {
            return R.string.type_holder;
        } else {
            return R.string.type_dependent;
        }
    }
}

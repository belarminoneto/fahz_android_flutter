package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.controls.DateEditText;

public class ScheduleAppointmentRequest {

    @SerializedName("dateConverter")
    @Expose
    private String dateConverter;

    @SerializedName("dateTimeConverter")
    @Expose
    private String dateTimeConverter;

    @SerializedName("tipoOper")
    @Expose
    private String tipoOper;

    @SerializedName("codPac")
    @Expose
    private String codPac;

    @SerializedName("calendarId")
    @Expose
    private Long calendarId;

    public void setDateConverter(String dateConverter) {
        this.dateConverter = DateEditText.formatDate(dateConverter, "dd/MM/yyyy", "yyyyMMdd");
    }

    public void setDateTimeConverter(String date, String hour) {
        this.dateTimeConverter = DateEditText.formatDate(String.format("%s %s", date, hour), "dd/MM/yyyy HH:mm", "yyyyMMdd HHmm");
    }

    public void setTipoOper(String tipoOper) {
        this.tipoOper = tipoOper;
    }
    public void setCodPac(String codPac) {
        this.codPac = codPac;
    }
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }
}

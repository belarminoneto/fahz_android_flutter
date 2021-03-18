package br.com.avanade.fahz.model.response.prontmed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchAppointmentResponse {

    @SerializedName("commited")
    @Expose
    private Boolean commited;

    @SerializedName("messageIdentifier")
    @Expose
    private String messageIdentifier;

    @SerializedName("appointments")
    @Expose
    private Appointment appointment;

    public Appointment getAppointment() {
        return appointment;
    }
    public boolean isCommited() {
        return commited != null ? commited : true;
    }
    public String getMessage() {
        return messageIdentifier != null ? messageIdentifier : "";
    }

    public class Appointment {

        @SerializedName("agenda")
        @Expose
        private List<Agenda> agendas;

        @SerializedName("careProviderName")
        @Expose
        private String careProviderName;

        @SerializedName("specialtyName")
        @Expose
        private List<String> specialties;

        @SerializedName("logradouro")
        @Expose
        private String logradouro;

        @SerializedName("number")
        @Expose
        private String number;

        @SerializedName("neighborhood")
        @Expose
        private String neighborhood;

        @SerializedName("zipCode")
        @Expose
        private String zipCode;

        @SerializedName("city")
        @Expose
        private String city;

        @SerializedName("state")
        @Expose
        private String state;

        public List<Agenda> getAgendas() {
            return agendas;
        }

        public String getAddress() {
            return String.format("%s, %s, %s, %s, %s", logradouro, number, neighborhood, city, state);
        }
    }

    public class Agenda {

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("hours")
        @Expose
        private List<Hour> hours;

        public String getDate() {
            return date;
        }

        public List<Hour> getHours() {
            return hours;
        }
    }

    public class Hour {

        @SerializedName("label")
        @Expose
        private String label;

        @SerializedName("calendarId")
        @Expose
        private Long calendarId;

        public String getLabel() {
            return label;
        }

        public Long getCalendarId() {
            return calendarId;
        }
    }
}

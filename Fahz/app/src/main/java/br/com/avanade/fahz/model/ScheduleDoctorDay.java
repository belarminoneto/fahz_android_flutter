package br.com.avanade.fahz.model;

import java.util.Date;

public class ScheduleDoctorDay {

    private java.util.Date Date;
    private boolean isAvailable;
    private boolean shouldBeSelected;

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isShouldBeSelected() {
        return shouldBeSelected;
    }

    public void setShouldBeSelected(boolean shouldBeSelected) {
        this.shouldBeSelected = shouldBeSelected;
    }

    public ScheduleDoctorDay(java.util.Date date, boolean isAvailable, boolean shouldBeSelected) {
        Date = date;
        this.isAvailable = isAvailable;
        this.shouldBeSelected = shouldBeSelected;
    }
}

package br.com.avanade.fahz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class ScholarshipLife implements Parcelable {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("CPF")
    @Expose
    private String cPF;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Course")
    @Expose
    private String course;
    @SerializedName("Institution")
    @Expose
    private String institution;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Student")
    @Expose
    private Boolean student;
    @SerializedName("Period")
    @Expose
    private String period;
    @SerializedName("EvaluationIGC")
    @Expose
    private int evaluationIGC;
    @SerializedName("Financing")
    @Expose
    private Boolean financing;
    @SerializedName("StartCourse")
    @Expose
    private String startCourse;
    @SerializedName("EndCourse")
    @Expose
    private String endCourse;
    @SerializedName("Exception")
    @Expose
    private Boolean exception;
    @SerializedName("Classification")
    @Expose
    private String classification;
    @SerializedName("MonthlyFee")
    @Expose
    private Double monthlyFee;
    @SerializedName("Status")
    @Expose
    private Status status;
    @SerializedName("TypeMonitoringScholarship")
    @Expose
    private Status typeMonitoringScholarship;
    @SerializedName("Scholarship")
    @Expose
    private Scholarship scholarship;
    @SerializedName("Schooling")
    @Expose
    private Schooling schooling;

    protected ScholarshipLife(Parcel in) {
        id = in.readString();
        cPF = in.readString();
        course = in.readString();
        institution = in.readString();
        city = in.readString();
        byte tmpStudent = in.readByte();
        student = tmpStudent == 0 ? null : tmpStudent == 1;
        period = in.readString();
        byte tmpEvaluationIGC = in.readByte();
        evaluationIGC = tmpEvaluationIGC;
        byte tmpFinancing = in.readByte();
        financing = tmpFinancing == 0 ? null : tmpFinancing == 1;
        startCourse = in.readString();
        endCourse = in.readString();
        byte tmpException = in.readByte();
        exception = tmpException == 0 ? null : tmpException == 1;
        classification = in.readString();
        if (in.readByte() == 0) {
            monthlyFee = null;
        } else {
            monthlyFee = in.readDouble();
        }
    }

    public static final Creator<ScholarshipLife> CREATOR = new Creator<ScholarshipLife>() {
        @Override
        public ScholarshipLife createFromParcel(Parcel in) {
            return new ScholarshipLife(in);
        }

        @Override
        public ScholarshipLife[] newArray(int size) {
            return new ScholarshipLife[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCPF() {
        return cPF;
    }

    public void setCPF(String cPF) {
        this.cPF = cPF;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getStudent() {
        return student;
    }

    public void setStudent(Boolean student) {
        this.student = student;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getEvaluationIGC() {
        return evaluationIGC;
    }

    public void setEvaluationIGC(int evaluationIGC) {
        this.evaluationIGC = evaluationIGC;
    }

    public Boolean getFinancing() {
        return financing;
    }

    public void setFinancing(Boolean financing) {
        this.financing = financing;
    }

    public String getStartCourse() {
        return startCourse;
    }

    public void setStartCourse(String startCourse) {
        this.startCourse = startCourse;
    }

    public String getEndCourse() {
        return endCourse;
    }

    public void setEndCourse(String endCourse) {
        this.endCourse = endCourse;
    }

    public Boolean getException() {
        return exception;
    }

    public void setException(Boolean exception) {
        this.exception = exception;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Double getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(Double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getTypeMonitoringScholarship() {
        return typeMonitoringScholarship;
    }

    public void setTypeMonitoringScholarship(Status typeMonitoringScholarship) {
        this.typeMonitoringScholarship = typeMonitoringScholarship;
    }

    public Scholarship getScholarship() {
        return scholarship;
    }

    public void setScholarship(Scholarship scholarship) {
        this.scholarship = scholarship;
    }

    public Schooling getSchooling() {
        return schooling;
    }

    public void setSchooling(Schooling schooling) {
        this.schooling = schooling;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(cPF);
        parcel.writeString(course);
        parcel.writeString(institution);
        parcel.writeString(city);
        parcel.writeByte((byte) (student == null ? 0 : student ? 1 : 2));
        parcel.writeString(period);
        parcel.writeInt(evaluationIGC);
        parcel.writeByte((byte) (financing == null ? 0 : financing ? 1 : 2));
        parcel.writeString(startCourse);
        parcel.writeString(endCourse);
        parcel.writeByte((byte) (exception == null ? 0 : exception ? 1 : 2));
        parcel.writeString(classification);
        if (monthlyFee == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(monthlyFee);
        }
    }
}

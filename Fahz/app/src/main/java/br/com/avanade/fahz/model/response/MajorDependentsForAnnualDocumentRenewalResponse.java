package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MajorDependentsForAnnualDocumentRenewalResponse {

    @SerializedName("commited")
    private Boolean commited;

    @SerializedName("count")
    private Integer count;

    @SerializedName("result")
    private List<Dependent> dependents;

    public Boolean getCommited() {
        return commited != null ? commited : false;
    }

    public Integer getCount() {
        return count;
    }

    public List<Dependent> getDependents() {
        return dependents;
    }

    public class Dependent {
        @SerializedName("Name")
        private String name;
        @SerializedName("CPF")
        private String cpf;
        @SerializedName("StatusAnualRenewal")
        private Integer statusAnualRenewal;
        @SerializedName("Status")
        private Status status;
        @SerializedName("BirthDate")
        private String birthDate;
        @SerializedName("Years")
        private List<Year> years;

        public String getName() {
            return name;
        }

        public String getCpf() {
            return cpf;
        }

        public Integer getStatusAnualRenewal() {
            return statusAnualRenewal;
        }

        public Status getStatus() {
            return status;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public List<Year> getYears() {
            return years;
        }

        public class Status {
            @SerializedName("Id")
            private Integer id;

            @SerializedName("Description")
            private String description;

            public Integer getId() {
                return id;
            }

            public String getDescription() {
                return description;
            }
        }

        class Year {

        }
    }
}

package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AnnualDocumentsByDependentResponse {

    @SerializedName("commited")
    private Boolean commited;

    @SerializedName("result")
    private Result result;

    public Boolean getCommited() {
        return commited != null ? commited : false;
    }

    public List<Result.Year> getYears() {
        return result.getYears() != null ? result.getYears() : new ArrayList<Result.Year>();
    }

    public Integer getStatusAnnualRenewal() {
        return result.getStatusAnualRenewal();
    }

    public class Result {
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

        List<Year> getYears() {
            return years;
        }

        public String getName() {
            return name;
        }

        public String getCpf() {
            return cpf;
        }

        Integer getStatusAnualRenewal() {
            return statusAnualRenewal;
        }

        public Status getStatus() {
            return status;
        }

        public String getBirthDate() {
            return birthDate;
        }

        class Status {
            @SerializedName("Id")
            private Long id;
            @SerializedName("Description")
            private String description;
        }

        public class Year {
            @SerializedName("Year")
            private Integer year;
            @SerializedName("HistoryId")
            private String historyId;
            @SerializedName("TypeDocuments")
            private List<TypeDocument> typeDocuments;

            public Integer getYear() {
                return year;
            }

            public String getIdHistory() {
                return historyId;
            }

            public List<TypeDocument> getTypeDocuments() {
                return typeDocuments;
            }

            public class TypeDocument {
                @SerializedName("Id")
                private Long id;
                @SerializedName("Description")
                private String description;
                @SerializedName("Documents")
                private List<Document> documents;

                public String getDescription() {
                    return description;
                }

                public List<Document> getDocuments() {
                    return documents;
                }

                public class Document {
                    @SerializedName("Id")
                    private String id;
                    @SerializedName("Path")
                    private String path;
                    @SerializedName("Date")
                    private String date;
                    @SerializedName("Name")
                    private String name;

                    public String getId() {
                        return id;
                    }

                    public String getPath() {
                        return path;
                    }

                    public String getDate() {
                        return date;
                    }

                    public String getName() {
                        return name;
                    }
                }
            }

        }
    }
}

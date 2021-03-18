package br.com.avanade.fahz.model.anamnesisModel;

import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.util.AnamnesisUtils;

public class LifeAnamnesis {

    @SerializedName("sharpid")
    private long sharpID;

    @SerializedName("cpf")
    private String cpf;

    @SerializedName("nome")
    private String name;

    @SerializedName("nomemae")
    private String motherName;

    @SerializedName("sexo")
    private String sex;

    @SerializedName("nacionalidade")
    private String nationality;

    @SerializedName("datanascimento")
    private String birthDate;

    @SerializedName("cidadenascimento")
    private String birthCity;

    @SerializedName("estadocivil")
    private String maritalStatus;

    @SerializedName("celular")
    private String cellPhone;

    @SerializedName("cargo")
    private String jobRole;

    @SerializedName("lotacao")
    private String workplace;

    @SerializedName("empresa")
    private String company;

    @SerializedName("grauparentesco")
    private String degreeKinship;

    @SerializedName("dataadmissao")
    private String admissionDate;

    @SerializedName("datadesligamento")
    private String resignationDate;

    @SerializedName("cep")
    private String zipCode;

    @SerializedName("logradouro")
    private String street;

    @SerializedName("numero")
    private String houseNumber;

    @SerializedName("complemento")
    private String addressComplement;

    @SerializedName("bairro")
    private String neighborhood;

    @SerializedName("cidade")
    private String city;

    @SerializedName("uf")
    private String state;

    @SerializedName("tipo")
    private String type;

    @SerializedName("idade")
    private int age;

    @SerializedName("situacao")
    private String situation;

    @SerializedName("status")
    private String status;

    @SerializedName("dadoscomplementares")
    private SupplementaryDataAnamnesis supplementaryData;

    public long getSharpID() {
        return sharpID;
    }

    public void setSharpID(long sharpID) {
        this.sharpID = sharpID;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCpfFormatted() {
        String mask = "###.###.###-##";
        return AnamnesisUtils.setMaskFormatter(cpf, mask);
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getName() {
        return name;
    }

    public String getNameFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isMale() {
        return sex.equalsIgnoreCase("m");
    }

    public boolean isFemale() {
        return sex.equalsIgnoreCase("f");
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getMaritalStatusFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(maritalStatus);
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getType() {
        return type;
    }

    public String getTypeFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSituation() {
        return situation;
    }

    public String getSituationFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(situation);
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getMotherNameFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(motherName);
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getNationality() {
        return nationality;
    }

    public String getNationalityFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(nationality);
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getBirthDateFormatted() {
        return AnamnesisUtils.parseTODate(birthDate);
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public String getBirthCityFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(birthCity);
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getJobRole() {
        return jobRole;
    }

    public String getJobRoleFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(jobRole);
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getWorkplace() {
        return workplace;
    }

    public String getWorkplaceFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(workplace);
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getCompany() {
        return company;
    }

    public String getCompanyFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(company);
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDegreeKinship() {
        return degreeKinship;
    }

    public String getDegreeKinshipFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(degreeKinship);
    }

    public void setDegreeKinship(String degreeKinship) {
        this.degreeKinship = degreeKinship;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public String getAdmissionDateFormatted() {
        return AnamnesisUtils.parseTODate(admissionDate);
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getResignationDate() {
        return resignationDate;
    }

    public String getResignationDateFormatted() {
        return AnamnesisUtils.parseTODate(resignationDate);
    }

    public void setResignationDate(String resignationDate) {
        this.resignationDate = resignationDate;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getZipCodeFormatted() {
        String mask = "#####-###";
        return AnamnesisUtils.setMaskFormatter(zipCode, mask);
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(street);
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAddressComplement() {
        return addressComplement;
    }

    public String getAddressComplementFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(addressComplement);
    }

    public void setAddressComplement(String addressComplement) {
        this.addressComplement = addressComplement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getNeighborhoodFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(neighborhood);
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getCityFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(city);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public SupplementaryDataAnamnesis getSupplementaryData() {
        return supplementaryData;
    }

    public void setSupplementaryData(SupplementaryDataAnamnesis supplementaryData) {
        this.supplementaryData = supplementaryData;
    }
}

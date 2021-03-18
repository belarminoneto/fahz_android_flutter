package br.com.avanade.fahz.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsageEntry {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("InvoiceId")
    @Expose
    private Object invoiceId;
    @SerializedName("NumeroConta")
    @Expose
    private Object numeroConta;
    @SerializedName("DataAtendimento")
    @Expose
    private String dataAtendimento;
    @SerializedName("DescricaoProcedimento")
    @Expose
    private String descricaoProcedimento;
    @SerializedName("NomePrestador")
    @Expose
    private String nomePrestador;
    @SerializedName("NomeUsuario")
    @Expose
    private String nomeUsuario;
    @SerializedName("ValorRecebido")
    @Expose
    private Double valorRecebido;
    @SerializedName("ValorParteEmpresa")
    @Expose
    private Double valorParteEmpresa;
    @SerializedName("ValorParteEmpregado")
    @Expose
    private Double valorParteEmpregado;
    @SerializedName("BillingRule")
    @Expose
    private Object billingRule;
    @SerializedName("CPFVida")
    @Expose
    private Object cPFVida;
    @SerializedName("BillingRuleDescription")
    @Expose
    private Object billingRuleDescription;
    @SerializedName("ValorRecebidoStr")
    @Expose
    private String valorRecebidoStr;
    @SerializedName("ValorParteEmpresaStr")
    @Expose
    private String valorParteEmpresaStr;
    @SerializedName("ValorParteEmpregadoStr")
    @Expose
    private String valorParteEmpregadoStr;
    @SerializedName("Cpf")
    @Expose
    private String cpf;
    @SerializedName("EntryType")
    @Expose
    private Integer entryType;
    @SerializedName("EntryTypeDescription")
    @Expose
    private String entryTypeDescription;
    @SerializedName("HasInvoice")
    @Expose
    private Boolean hasInvoice;
    @SerializedName("DataCronograma")
    @Expose
    private String DataCronograma;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Object invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Object getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(Object numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(String dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getDescricaoProcedimento() {
        return descricaoProcedimento;
    }

    public void setDescricaoProcedimento(String descricaoProcedimento) {
        this.descricaoProcedimento = descricaoProcedimento;
    }

    public String getNomePrestador() {
        return nomePrestador;
    }

    public void setNomePrestador(String nomePrestador) {
        this.nomePrestador = nomePrestador;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Double getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(Double valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public Double getValorParteEmpresa() {
        return valorParteEmpresa;
    }

    public void setValorParteEmpresa(Double valorParteEmpresa) {
        this.valorParteEmpresa = valorParteEmpresa;
    }

    public Double getValorParteEmpregado() {
        return valorParteEmpregado;
    }

    public void setValorParteEmpregado(Double valorParteEmpregado) {
        this.valorParteEmpregado = valorParteEmpregado;
    }

    public Object getBillingRule() {
        return billingRule;
    }

    public void setBillingRule(Object billingRule) {
        this.billingRule = billingRule;
    }

    public Object getCPFVida() {
        return cPFVida;
    }

    public void setCPFVida(Object cPFVida) {
        this.cPFVida = cPFVida;
    }

    public Object getBillingRuleDescription() {
        return billingRuleDescription;
    }

    public void setBillingRuleDescription(Object billingRuleDescription) {
        this.billingRuleDescription = billingRuleDescription;
    }

    public String getValorRecebidoStr() {
        return valorRecebidoStr;
    }

    public void setValorRecebidoStr(String valorRecebidoStr) {
        this.valorRecebidoStr = valorRecebidoStr;
    }

    public String getValorParteEmpresaStr() {
        return valorParteEmpresaStr;
    }

    public void setValorParteEmpresaStr(String valorParteEmpresaStr) {
        this.valorParteEmpresaStr = valorParteEmpresaStr;
    }

    public String getValorParteEmpregadoStr() {
        return valorParteEmpregadoStr;
    }

    public void setValorParteEmpregadoStr(String valorParteEmpregadoStr) {
        this.valorParteEmpregadoStr = valorParteEmpregadoStr;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Integer getEntryType() {
        return entryType;
    }

    public void setEntryType(Integer entryType) {
        this.entryType = entryType;
    }

    public String getEntryTypeDescription() {
        return entryTypeDescription;
    }

    public void setEntryTypeDescription(String entryTypeDescription) {
        this.entryTypeDescription = entryTypeDescription;
    }

    public Boolean getHasInvoice() {
        return hasInvoice;
    }

    public void setHasInvoice(Boolean hasInvoice) {
        this.hasInvoice = hasInvoice;
    }

    public String getDataCronograma() {
        return DataCronograma;
    }

    public void setDataCronograma(String dataCronograma) {
        DataCronograma = dataCronograma;
    }
}

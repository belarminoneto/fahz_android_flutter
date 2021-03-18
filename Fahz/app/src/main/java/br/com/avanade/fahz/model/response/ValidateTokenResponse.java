package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.SerializedName;

import br.com.avanade.fahz.model.ValidateToken;

public class ValidateTokenResponse<T> {
    @SerializedName("commited")
    public boolean commited;
    @SerializedName("result")
    public ValidateToken result;
}

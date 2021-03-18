package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.Message;

public class MessagesResponse {


    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("registers")
    @Expose
    private List<Message> messages = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Message> getRegisters() {
        return messages;
    }

    public void setRegisters(List<Message> registers) {
        this.messages = registers;
    }
}

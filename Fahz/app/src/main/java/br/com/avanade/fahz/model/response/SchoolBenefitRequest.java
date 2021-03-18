package br.com.avanade.fahz.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.com.avanade.fahz.model.SchoolBenefitPeople;

public class SchoolBenefitRequest{

        @SerializedName("Count")
        @Expose
        private Integer count;
        @SerializedName("Registers")
        @Expose
        private List<SchoolBenefitPeople> registers = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<SchoolBenefitPeople> getRegisters() {
            return registers;
        }

        public void setRegisters(List<SchoolBenefitPeople> registers) {
            this.registers = registers;
        }

}

package br.com.avanade.fahz.model.anamnesisModel;

import java.io.Serializable;

import br.com.avanade.fahz.util.AnamnesisUtils;

public class AuxDataBridge implements Serializable {

    private String id;

    private String title1;

    private String title2;

    private String title3;

    private String title4;

    private String desc1;

    private String desc2;

    private String desc3;

    private String desc4;

    private String textShown;

    private boolean isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc1() {
        return desc1;
    }

    public String getDesc1FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(desc1);
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getDesc2() {
        return desc2;
    }

    public String getDesc2FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(desc2);
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getDesc3() {
        return desc3;
    }

    public String getDesc3FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(desc3);
    }

    public void setDesc3(String desc3) {
        this.desc3 = desc3;
    }

    public String getDesc4() {
        return desc4;
    }

    public String getDesc4FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(desc4);
    }

    public void setDesc4(String desc4) {
        this.desc4 = desc4;
    }

    public String getTitle1() {
        return title1;
    }

    public String getTitle1FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(title1);
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public String getTitle2FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(title2);
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle3() {
        return title3;
    }

    public String getTitle3FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(title3);
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public String getTitle4() {
        return title4;
    }

    public String getTitle4FirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(title4);
    }

    public void setTitle4(String title4) {
        this.title4 = title4;
    }

    public String getTextShown() {
        return textShown;
    }

    public String getTextShownFirstLetterCapitalized() {
        return AnamnesisUtils.capitalizeFirstLetter(textShown);
    }

    public void setTextShown(String textShown) {
        this.textShown = textShown;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void reverseSelection() {
        isSelected = !isSelected;
    }
}

package br.com.avanade.fahz.controls;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;

public class ZipEditText extends TextInputEditText {
    private static final String MASK_ZIP_CODE = "#####-###";
    private boolean isUpdating = false;

    public ZipEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isUpdating)
            return;

        if(text.length() > 8){
            isUpdating = true;
            this.setText(text.toString().substring(0, 9));
            this.setSelection(9);
            isUpdating = false;
            return;
        }

        if(text.length() > 0){
            String maskedValue = maskZip(unmaskZip(text.toString()));
            this.isUpdating = true;
            this.setText(maskedValue);
            this.setSelection(maskedValue.length());
            this.isUpdating = false;
        }
    }

    public static String unmaskZip(String input){
        return input.replace("-", "");
    }

    public static String maskZip(String input){
        if(input == null || input.equals("") || input.isEmpty())
            return "";

        StringBuilder maskedValue = new StringBuilder();

        int i = 0;

        for(final char digit : MASK_ZIP_CODE.toCharArray()){
            if(digit == '#'){
                maskedValue.append(input.charAt(i));
                i++;

                if(i >= input.length())
                    break;
            }
            else{
                maskedValue.append(digit);
            }
        }

        return maskedValue.toString();
    }
}

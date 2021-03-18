package br.com.avanade.fahz.controls;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;

public class PhoneEditText extends TextInputEditText {
    private static final String MASK_PHONE = "(##) ####-####";
    private static final String MASK_CELLPHONE = "(##) #####-####";
    private boolean isUpdating = false;

    public PhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if(isUpdating)
            return;

//        if(text.length() > 14){
//            isUpdating = true;
//            this.setText(text.toString().substring(0, 15));
//            this.setSelection(15);
//            isUpdating = false;
//            return;
//        }

        if(text.length() > 0){
            String maskedValue = maskPhone(unmaskPhone(text.toString()));
            this.isUpdating = true;
            this.setText(maskedValue);
            this.setSelection(maskedValue.length());
            this.isUpdating = false;
        }
    }

    public static String unmaskPhone(String input){
        return input.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
    }

    public static String maskPhone(String inputPhone){
        if(inputPhone == null || inputPhone.equals("") || inputPhone.isEmpty())
            return "";

        String mask = inputPhone.length() == 11 ? MASK_CELLPHONE : MASK_PHONE;
        StringBuilder maskedValue = new StringBuilder();

        int i = 0;
        for(final char digit : mask.toCharArray()){
            if(digit == '#') {
                maskedValue.append(inputPhone.charAt(i));
                i++;

                if(i >= inputPhone.length())
                    break;
            }
            else{
                maskedValue.append(digit);
            }
        }

        return maskedValue.toString();
    }
}

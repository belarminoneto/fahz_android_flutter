package br.com.avanade.fahz.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.avanade.fahz.R;

public class AnamnesisUtils {

    public static Dialog showQuestionDialog(String title, String message,
                                            Context context, View.OnClickListener onClickListener) {

        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_alert_question);

            Button confirmButton = dialog.findViewById(R.id.dialog_confirm_button);
            Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

            TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
            TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);

            txtTitle.setText(title);
            txtMessage.setText(message);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            confirmButton.setOnClickListener(onClickListener);

            dialog.show();
            return dialog;
        } catch (Exception ex) {
            String a = ex.getMessage();
        }
        return null;
    }

    public static Dialog showSimpleDialogGoBack(String title, String message, @Nullable String buttonMessage,
                                                Context context, View.OnClickListener onClickListener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);

        Button chatButton = dialog.findViewById(R.id.dialog_confirm_button);
        if (buttonMessage != null) {
            chatButton.setText(buttonMessage);
        }

        TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
        TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);

        txtTitle.setText(title);
        txtMessage.setText(message);

        chatButton.setOnClickListener(onClickListener);

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
        return dialog;
    }

    public static Dialog showQuestionDialog(String title, String message, String yesMessage, String noMessage,
                                            Context context,
                                            View.OnClickListener onYesClickListener,
                                            View.OnClickListener onNoClickListener) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_alert_question);

            Button confirmButton = dialog.findViewById(R.id.dialog_confirm_button);
            Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);
            confirmButton.setText(yesMessage);
            cancelButton.setText(noMessage);
            confirmButton.setOnClickListener(onYesClickListener);
            cancelButton.setOnClickListener(onNoClickListener);

            TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
            TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);
            txtTitle.setText(title);
            txtMessage.setText(message);

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            return dialog;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Dialog showQuestionDialog(String title, String message, String confirmMessage, String cancelMessage,
                                            Context context, View.OnClickListener onClickListener) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_alert_question);

            Button confirmButton = dialog.findViewById(R.id.dialog_confirm_button);
            Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

            TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
            TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);

            txtTitle.setText(title);
            txtMessage.setText(message);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            cancelButton.setText(cancelMessage);

            confirmButton.setOnClickListener(onClickListener);
            confirmButton.setText(confirmMessage);

            dialog.show();
            return dialog;
        } catch (Exception ex) {
            String a = ex.getMessage();
        }
        return null;
    }

    public static void showExplicativeDialog(String message, Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_explicative);

        TextView txtText = dialog.findViewById(R.id.txtText);
        txtText.setText(message);

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showExplicativeDialogWithImage(Bitmap image, Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image);

        ImageView imgPhoto = dialog.findViewById(R.id.imgPhoto);
        imgPhoto.setImageBitmap(image);

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static String parseTODate(String time) {
        String inputPattern = "yyyyMMdd";
        String outputPattern = "dd/MM/yyyy";
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,myLocale);

        Date date;
        String str = "";

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String capitalizeFirstLetter(String name) {
        try {
            String[] words = name.toLowerCase().split(" ");
            StringBuilder capName = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()
                        && !(word.equalsIgnoreCase("da")
                        || word.equalsIgnoreCase("de")
                        || word.equalsIgnoreCase("di")
                        || word.equalsIgnoreCase("do")
                        || word.equalsIgnoreCase("das")
                        || word.equalsIgnoreCase("dos")
                        || word.equalsIgnoreCase("e"))) {
                    capName.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
                } else {
                    capName.append(word);
                }
                capName.append(" ");
            }
            return capName.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String setMaskFormatter(String value, String mask) {
        try {
            StringBuilder out = new StringBuilder();
            int j = 0;
            for (int i = 0; i < mask.length(); i++) {
                if (mask.charAt(i) == '#') {
                    out.append(value.charAt(j));
                    j++;
                } else {
                    out.append(mask.charAt(i));
                }
            }
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }
}

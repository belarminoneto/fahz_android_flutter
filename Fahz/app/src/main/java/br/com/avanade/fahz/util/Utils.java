package br.com.avanade.fahz.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.avanade.fahz.R;
import br.com.avanade.fahz.interfaces.ISimpleDialogOkAction;

public class Utils {
    private static final String MASK_PHONE = "(##) ####-####";
    private static final String MASK_CELLPHONE = "(##) #####-####";
    private static final String MASK_CPF = "###.###.###-##";

    public static String formatDateString(String inputDate) {
        return inputDate.substring(6, 8) + "/" + inputDate.substring(4, 6) + "/" + inputDate.substring(0, 4);
    }

    public static String formatValueMask(String valorInformado, String mascaraInformada, String caracteresControle) {
        // se valor nulo ou menor que 1
        if (valorInformado == null || valorInformado.length() < 1) {
            // retorna vazio
            return "";
        }
        int nCount = 0;
        StringBuilder valorFormatado = new StringBuilder();
        // realiza interação em todas as posições da mascara
        for (int i = 0; i <= mascaraInformada.length(); i++) {
            try {
                char caracter;
                // captura caracter da mascara no indice informado
                caracter = mascaraInformada.charAt(i);
                // verifica se caracter capturado é igual aos caracteres de controle
                boolean bolMask = caracteresControle.contains(caracter + "");
                // se for igual a caracter de controle
                if (bolMask) {
                    // adiciona caracter de controle
                    valorFormatado.append(caracter);
                } else {
                    // senão irá adicionar o valor capturado a partir do indice informado
                    valorFormatado.append(valorInformado.charAt(nCount));
                    nCount++;
                }
            } catch (StringIndexOutOfBoundsException e) {
                // quando a formatação for concluída lança exception
                return valorFormatado.toString();
            }
        }
        return valorFormatado.toString();
    }

    public static void showSimpleDialog(String title, String message, @Nullable String buttonMessage,
                                        Context context, @Nullable ISimpleDialogOkAction dialogActionHandler) {

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

        chatButton.setOnClickListener(v -> {
            dialog.cancel();
            if (dialogActionHandler != null) {
                dialogActionHandler.okButton();
            }
        });

        dialog.show();
    }

    public static void showSimpleDialogGoBack(String title, String message, @Nullable String buttonMessage,
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

        chatButton.setOnClickListener((view) -> {
            onClickListener.onClick(view);
            dialog.dismiss();
        });

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public static void showQuestionDialog(String title, String message,
                                          Context context, View.OnClickListener onClickListener ) {

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
        }
        catch (Exception ex)
        {
            String a = ex.getMessage();
        }
    }

    public static Dialog showQuestionReturnDialog(String title, String message,
                                                  Context context, View.OnClickListener onClickListener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert_question);

        Button confirmButton = dialog.findViewById(R.id.dialog_confirm_button);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

        TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
        TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);

        txtTitle.setText(Html.fromHtml(title));
        txtMessage.setText(Html.fromHtml(message));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        confirmButton.setOnClickListener(onClickListener);

        dialog.show();

        return dialog;
    }

    public static Dialog showQuestionDialog(String title, String message,
                                            Context context, View.OnClickListener onOkClickListener,View.OnClickListener onCancelClickListener, String confirmText, String deniedText) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert_question);

        Button confirmButton = dialog.findViewById(R.id.dialog_confirm_button);
        if (confirmText != null && !confirmText.equals(""))
            confirmButton.setText(confirmText);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);
        if (deniedText != null && !deniedText.equals(""))
            cancelButton.setText(deniedText);

        TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
        TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);

        txtTitle.setText(title);
        txtMessage.setText(message);

        confirmButton.setOnClickListener(onOkClickListener);
        cancelButton.setOnClickListener(onCancelClickListener);

        dialog.show();

        return dialog;
    }

    public static Dialog showSimpleDialogReturnDialog(String title, String message,
                                                      Context context, View.OnClickListener onClickListener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);

        Button confirmButton = dialog.findViewById(R.id.dialog_confirm_button);

        TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
        TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);

        txtTitle.setText(title);
        txtMessage.setText(message);

        confirmButton.setOnClickListener(onClickListener);

        dialog.show();

        return dialog;
    }

    public static Dialog showAlertDialogReturnDialog(String title, String message, @Nullable String buttonMessage,
                                                     Context context, View.OnClickListener onClickListener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);

        Button confirmButton = dialog.findViewById(R.id.dialog_confirm_button);

        if (buttonMessage != null) {
            confirmButton.setText(buttonMessage);
        }

        TextView txtTitle = dialog.findViewById(R.id.dialog_alert_title);
        TextView txtMessage = dialog.findViewById(R.id.dialog_alert_text);

        txtTitle.setText(title);
        txtMessage.setText(message);

        confirmButton.setOnClickListener(onClickListener);

        dialog.show();

        return dialog;
    }

    public static void showKeyboard(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void closeKeyboard(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static String AbbreviateName(String name, int size, boolean firstAndLastNameOnly)
    {
        String[] aName = name.split(" ");
        if (name.length() > size)
        {
            boolean fist = true, abbreviate = false;
            if (!firstAndLastNameOnly)
            {
                for (String part:aName)
                {
                    if (fist)
                    {
                        fist = !fist;
                        name = part;
                        continue;
                    }
                    String p = part.toLowerCase();
                    // remover da/de/do/das/dos
                    if (!(p.equals("da") || p.equals("de") || p.equals("do") || p.equals("das") || p.equals("dos")))
                    {
                        if (!abbreviate)
                        {
                            if (part.length() > 2)
                            {
                                abbreviate = !abbreviate;
                                name += " " +part.charAt(0)+".";
                            }
                            else
                                name += " "+part;
                        }
                        else
                            name += " " +part;
                    }
                }
                return AbbreviateName(name.replaceAll("\\s+$", "").toUpperCase(), size, firstAndLastNameOnly);
            }
            else
            {
                String test = aName[0];
                int testValue = aName.length -1;
                String newName = aName[testValue];
                String value = (test + " " + newName).replaceAll("\\s+$", "").toUpperCase();

                return value;
            }
        }
        else
            return name.replaceAll("\\s+$", "").toUpperCase();
    }

    public static String simpleMoneyFormat(Double value) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(value.doubleValue());
    }

    public static void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {

                //get the Context object that was used to create the dialog
                Context context = ((ContextWrapper) progressDialog.getContext()).getBaseContext();

                // if the Context used here was an activity AND it hasn't been finished or destroyed
                // then dismiss it
                if (context instanceof Activity) {

                    // Api >=17
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                            dismissWithExceptionHandling(progressDialog);
                        }
                    } else {

                        // Api < 17. Unfortunately cannot check for isDestroyed()
                        if (!((Activity) context).isFinishing()) {
                            dismissWithExceptionHandling(progressDialog);
                        }
                    }
                } else
                    // if the Context used wasn't an Activity, then dismiss it too
                    dismissWithExceptionHandling(progressDialog);
            }
            progressDialog = null;
        }
    }


    private static void dismissWithExceptionHandling(ProgressDialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        } finally {
            dialog = null;
        }
    }

    public static boolean validatePhone(String phone) {
        //Valida se telefone iniciando com +55
        return !phone.contains("+");
    }

    public static boolean validateCellPhoneNumber(String cellphone) {
        String cellphoneNumber = cellphone.replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "");

        return cellphoneNumber.length() == 11 && cellphoneNumber.matches("^[0-9]*$");
    }

    public static boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public static boolean isBirthDateValid(String date) {
        String regex = "^(?:(?:31(\\/)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/)" +
                "(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?" +
                "\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?" +
                "(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|" +
                "[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:" +
                "(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        return date != null && date.matches(regex);
    }

    public static boolean isPasswordValid(String password){


        return (password != null && password.matches(".*[a-zA-Z].*") && password.matches(".*[0-9].*") && password.length()>=8);
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = addr instanceof Inet4Address;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions

        return "";
    }

    // Método que calcula a idade de alguém baseado na data de nascimento
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge (LocalDate birthDay) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        int result = currentYear - birthDay.getYear();
        if(birthDay.getDayOfYear() > today.getDayOfYear())
            result--;

        return result;
    }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    public static String convertName(String name){
        int indexF = name.indexOf(' ');
        String firstname = name.substring(0, indexF);

        int indexL = name.lastIndexOf(' ');
        String lastname = name.substring(indexL + 1);

        return firstname + " " + lastname;
    }
}

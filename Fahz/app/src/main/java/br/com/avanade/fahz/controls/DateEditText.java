package br.com.avanade.fahz.controls;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.avanade.fahz.application.FahzApplication;
import br.com.avanade.fahz.util.Constants;

import static br.com.avanade.fahz.util.Constants.LAST_ADMISSION_DATE;

public class DateEditText extends androidx.appcompat.widget.AppCompatEditText {
    public DateEditText(Context context) {
        super(context);
    }

    public static String formatDateForYMD(String date) {
        date = date.replace("/", "");
        String day = date.substring(0, 2);
        String month = date.substring(2, 4);
        String year = date.substring(4, 8);
        return year + month + day;
    }

    public static String formatDateForDMY(String date) {
        date = date.replace("/", "");
        String year = date.substring(0, 4);
        String month = date.substring(4,6);
        String day = date.substring(6,8);
        return day + month + year;
    }

    public static Calendar parseToCalendar(String rawDate) {
        return parseToCalendar(rawDate, "yyyyMMdd");
    }

    public static Calendar parseToCalendar(String rawDate, String inputPattern) {
        if (inputPattern.isEmpty()) {
            inputPattern = "yyyyMMdd";
        }
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);

        Calendar calendar = Calendar.getInstance();
        try {
            Date date = inputFormat.parse(rawDate);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }


    public static String formatDate(String inputDate) {
        return formatDate(inputDate, "yyyyMMdd", "dd/MM/yyyy");
    }

    public static String parseCompleteToCalendar(String rawDate, String inputPattern) {
        if (inputPattern.isEmpty()) {
            inputPattern = "yyyyMMddHHmm";
        }

        String outputPattern = "dd/MM/yyyy HH:mm";

        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, myLocale);

        String str = null;
        try {
            Date date = inputFormat.parse(rawDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static String formatDate(String inputDate, String inputPattern, String outputPattern) {
        if (inputPattern.isEmpty()) {
            inputPattern = "yyyyMMdd";
        }

        if (outputPattern.isEmpty()) {
            outputPattern = "dd/MM/yyyy";
        }
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, myLocale);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(inputDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTODate(String time) {
        String inputPattern = "yyyyMMdd";
        String outputPattern = "dd/MM/yyyy";
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,myLocale);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseToShortDate(String time) {
        String outputPattern = "yyyyMMdd";
        String inputPattern = "dd/MM/yyyy";
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,myLocale);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseToDateExtract(String time) {
        String outputPattern = "yyyy-MM-dd";
        String inputPattern = "dd/MM/yyyy";
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,myLocale);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseToDateRequest(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd/MM/yyyy";
        final Locale myLocale = new Locale("pt", "BR");
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, myLocale);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,myLocale);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }


    public static int getAge(String dateOfBirth) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(sdf.parse(dateOfBirth));

        Calendar today = Calendar.getInstance();

        if (birthDate.after(today)) {
            throw new IllegalArgumentException("You don't exist yet");
        }
        int todayYear = today.get(Calendar.YEAR);
        int birthDateYear = birthDate.get(Calendar.YEAR);
        int todayDayOfYear = today.get(Calendar.DAY_OF_YEAR);
        int birthDateDayOfYear = birthDate.get(Calendar.DAY_OF_YEAR);
        int todayMonth = today.get(Calendar.MONTH);
        int birthDateMonth = birthDate.get(Calendar.MONTH);
        int todayDayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int birthDateDayOfMonth = birthDate.get(Calendar.DAY_OF_MONTH);
        int age = todayYear - birthDateYear;

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ((birthDateDayOfYear - todayDayOfYear > 3) || (birthDateMonth > todayMonth)) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDateMonth == todayMonth) && (birthDateDayOfMonth > todayDayOfMonth)) {
            age--;
        }
        return age;
    }

    public static boolean canShowStudentAndWork() throws ParseException {
        final Locale myLocale = new Locale("pt", "BR");
        String date = FahzApplication.getInstance().getFahzClaims().getAdmisionDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", myLocale);

        Calendar adimisstionDate = Calendar.getInstance();
        adimisstionDate.setTime(sdf.parse(date));

        Calendar adimisstionDateLast = Calendar.getInstance();
        adimisstionDateLast.setTime(sdf.parse(LAST_ADMISSION_DATE));

        boolean beforeLastAdmissionDate = adimisstionDate.getTime().before(adimisstionDateLast.getTime());
        boolean AmbevSource =
                FahzApplication.getInstance().getFahzClaims().getSource() != null && !FahzApplication.getInstance().getFahzClaims().getSource().equals(Integer.toString(Constants.Fahz));


        return beforeLastAdmissionDate && AmbevSource;
    }

    public static String notFormattedDateToDMY(int year, int month, int day) {
        String sDay = String.valueOf(day);
        sDay = sDay.length() == 1 ? String.format("0%s", sDay) : sDay;
        String sMonth = String.valueOf(month + 1);
        sMonth = sMonth.length() == 1 ? String.format("0%s", sMonth) : sMonth;
        String sYear = String.valueOf(year);
        return String.format("%s/%s/%s", sDay, sMonth, sYear);
    }
}

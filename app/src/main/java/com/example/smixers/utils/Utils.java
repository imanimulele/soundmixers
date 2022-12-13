package com.example.smixers.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.smixers.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Source;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class Utils
{

    private static SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private static DecimalFormat currencyFormat = new DecimalFormat("#.00");

    private static Date dateFirst = null;
    private static Date dateLast = null;


    // returns unique global id
    public static String getUniqueId()
    {
        String sUuId = UUID.randomUUID().toString();
        sUuId = sUuId.replaceAll("-", "");

        return sUuId;
    }


    // returns nice string value, no matter if it is null or not
    public static String getNiceString(String value, String valIfNull)
    {
        if(value != null && value.length() > 0)
            return value;
        else
            return valIfNull;
    }


    // returns nice string value, no matter if it is zero or not
    public static String getNiceString(int value, String valIfNull)
    {
        if(value != 0)
            return Integer.toString(value);
        else
            return valIfNull;
    }


    // returns nice string value, no matter if it is zero or not
    public static String getNiceString(float value, String format, String valIfNull)
    {
        if(value != 0f)
            return String.format(format, value);
        else
            return valIfNull;
    }


    // returns nice string value, no matter if it is zero or not
    public static String getMoneyString(float value, String valIfNull)
    {
        if(value != 0f)
        {
            String valueString = currencyFormat.format(value);

            int centsIndex = -1;
            if (valueString.endsWith(".00"))
                centsIndex = valueString.lastIndexOf(".00");
            else if (valueString.endsWith(",00"))
                centsIndex = valueString.lastIndexOf(",00");

            if (centsIndex != -1)
                valueString = valueString.substring(0, centsIndex);

            return valueString;
        }
        else
        {
            return valIfNull;
        }
    }


    // converts string to int value
    public static int getIntValue(String sValue)
    {
        try
        {
            if(sValue != null && sValue.trim().length() > 0)
            {
                return Integer.parseInt(sValue);
            }
        }
        catch (Exception ex)
        {
            // do nothing
        }

        return 0;
    }


    // converts string to float value
    public static float getFloatValue(String sValue)
    {
        try
        {
            if(sValue != null && sValue.trim().length() > 0)
            {
                return Float.parseFloat(sValue);
            }
        }
        catch (Exception ex)
        {
            // do nothing
        }

        return 0f;
    }


    // converts string to float money value
    public static float stringToMoney(String moneyString) throws Exception
    {
        float moneyValue = 0f;

        try
        {
            if(moneyString != null && moneyString.length() > 0)
            {
                moneyValue = currencyFormat.parse(moneyString).floatValue();
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            throw new Exception("Invalid money value: " + moneyString);
        }

        return moneyValue;
    }

    //get 9 digits values
    public static String intToString(long num,String valIfNull) {


        if(num != 0){

            int digits=9;
            String output = Integer.toString((int) num);
            while (output.length() < digits) output = "0" + output;
            return output;
        }

        else{

            return valIfNull;
        }



    }


    public static String intToStrings(int num, int digits) {
        String output = Integer.toString(num);
        while (output.length() < digits) output = "0" + output;
        return output;
    }
    // returns distribution value string, along with its unit
    public static String getDistrString(int value, String unit)
    {
        return (value + " " + unit);
    }


    // checks whether the start date is before the end date
    public static void checkStartEndDates(String dateStart, String dateEnd, boolean dateAndTime) throws Exception
    {
        if(dateStart != null && dateStart.length() > 0 && dateEnd != null && dateEnd.length() > 0)
        {
            Date dtStart = stringToDate(dateStart, dateAndTime);
            Date dtEnd = stringToDate(dateEnd, dateAndTime);

            if(dtStart != null && dtEnd != null && dtEnd.before(dtStart))
            {
                throw new Exception("End date '" + dateEnd + "' set before the start date '" + dateStart + "'.");
            }
        }
    }


    // converts string to java.util.Date
    public static Date stringToDate(String dateString, boolean dateAndTime) throws Exception
    {
        Date dt = null;
        boolean bDateValid = false;

        try
        {
            if(dateString != null && dateString.length() >= 8)
            {
                SimpleDateFormat dateFormat = dateAndTime ? dateTimeFormat : dateOnlyFormat;
                dt = dateFormat.parse(dateString);
            }

            if(dateFirst == null || dateLast == null)
            {
                dateFirst = dateOnlyFormat.parse("01.01.1900");
                dateLast = dateOnlyFormat.parse("01.01.2100");
            }

            bDateValid = dt != null && dt.after(dateFirst) && dt.before(dateLast);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            throw new Exception("Invalid date: " + dateString);
        }

        if(!bDateValid && dateString != null && dateString.length() > 0)
        {
            throw new Exception("Invalid date: " + dateString);
        }

        return bDateValid ? dt : null;
    }

    // converts string to com.google.firebase.Timestamp
    public static Timestamp stringToTimestamp(String dateString, boolean dateAndTime) throws Exception
    {
        Date dt = null;
        boolean bDateValid = false;

        try
        {
            if(dateString != null && dateString.length() >= 8)
            {
                SimpleDateFormat dateFormat = dateAndTime ? dateTimeFormat : dateOnlyFormat;
                dt = dateFormat.parse(dateString);
            }

            if(dateFirst == null || dateLast == null)
            {
                dateFirst = dateOnlyFormat.parse("01.01.1900");
                dateLast = dateOnlyFormat.parse("01.01.2100");
            }

            bDateValid = dt != null && dt.after(dateFirst) && dt.before(dateLast);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            throw new Exception("Invalid date: " + dateString);
        }

        if(!bDateValid && dateString != null && dateString.length() > 0)
        {
            throw new Exception("Invalid date: " + dateString);
        }

        return bDateValid ? new Timestamp(dt) : null;
    }


    // converts java.util.Date to string
    public static String dateToString(Date dateTime, boolean dateAndTime, String strIfNull)
    {
        String sDateTime = strIfNull;

        if(dateTime != null)
        {
            SimpleDateFormat dateFormat = dateAndTime ? dateTimeFormat : dateOnlyFormat;
            sDateTime = dateFormat.format(dateTime);
        }

        return sDateTime;
    }


    // converts com.google.firebase.Timestamp to string
    public static String timestampToString(Timestamp dateTime, boolean dateAndTime, String strIfNull)
    {
        String sDateTime = strIfNull;

        if(dateTime != null)
        {
            SimpleDateFormat dateFormat = dateAndTime ? dateTimeFormat : dateOnlyFormat;
            sDateTime = dateFormat.format(dateTime.toDate());
        }

        return sDateTime;
    }


    // converts byte array to hex string
    public static String byteArrayToString(byte[] btArray)
    {
        if(btArray == null || btArray.length == 0)
            return "";

        return byteArrayToString(btArray, 0, btArray.length);
    }


    // converts byte array to hex string
    public static String byteArrayToString(byte[] btArray, int ofs, int len)
    {
        if(btArray == null || btArray.length == 0)
            return "";

        String strBytes = "";
        int ofsLen = ofs + len;

        for(int i = ofs; i < ofsLen; i++)
        {
            String hexByte = Integer.toHexString(btArray[i] & 0xFF).toUpperCase();
            strBytes += ((hexByte.length() < 2) ? "0" + hexByte : hexByte);
        }

        return strBytes;
    }


    // converts hex string to byte array
    public static byte[] hexStringToByteArray(String sHexBytes)
    {
        int lenHexBytes = sHexBytes != null ? sHexBytes.length() : 0;
        byte[] btHexBytes = new byte[lenHexBytes >> 1];

        for (int i = 0; i < lenHexBytes; i += 2)
        {
            btHexBytes[i >> 1] = (byte) ((Character.digit(sHexBytes.charAt(i), 16) << 4)
                    + Character.digit(sHexBytes.charAt(i+1), 16));
        }

        return btHexBytes;
    }


    // get nice representation of householdId
    public static String getHouseholdId(String householdId, String valIfEmpty)
    {
        if(householdId == null || householdId.trim().length() == 0)
            householdId = valIfEmpty;

        if(householdId.trim().length() > 8)
            householdId = householdId.substring(0, 8) + "...";

        return householdId;
    }

    // converts location coordinates to string
    public static String latLongToString(double lat, double lon, boolean longFormat)
    {
        if(lat == 0.0 && lon == 0.0)
            return "-";

        String sLat = Location.convert(lat, Location.FORMAT_SECONDS);
        sLat = replaceLatLongDelimiters(sLat, 0);
        sLat += lat >= 0 ? "N" : "S";

        String sLon = Location.convert(lon, Location.FORMAT_SECONDS);
        sLon = replaceLatLongDelimiters(sLon, 0);
        sLon += lon >= 0 ? "E" : "W";

        String sLatLong = longFormat ? ("Lat: " + sLat + "\nLong: ") + sLon : (sLat + " " + sLon);

        return sLatLong;
    }

    private static String replaceLatLongDelimiters(String str, int decimals)
    {
        str = str.replaceFirst(":", "°");
        str = str.replaceFirst(":", "'");

        int pointIndex = str.indexOf(".");
        if(pointIndex >= 0)
        {
            int endIndex = pointIndex + decimals + (decimals > 0 ? 1 : 0);
            if(endIndex < str.length())
                str = str.substring(0, endIndex);
        }

        str = str + "\"";

        return str;
    }


    // displays info message in a dialog
    public static void showInfoDialog(Context context, String message)
    {
        try
        {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage(message);

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }


    // displays error message in a dialog
    public static void showErrorDialog(Context context, String message)
    {
        try
        {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(message);

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    // displays error message in a dialog
    public static void showErrorDialog(Context context, String message, Exception ex)
    {
        try
        {
            if (ex != null)
            {
                if(ex.getMessage() != null)
                    message += "\n\n" + ex.getMessage();
                else
                    message += "\n\n" + ex.toString();
            }

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(message);

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }


    // sleep for the given time in ms
    public static void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    // deletes the given image file
    public static boolean deleteFile(File file)
    {
        if(file == null)
            return false;

        return file.delete();
    }


    // checks if network is connected
    public static boolean isNetworkConnected(Context context)
    {
        try
        {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            return isConnected;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    // returns the source, according to the online/offline mode
    public static Source getFirestoreSource(Context context)
    {
        if(isNetworkConnected(context))
            return Source.DEFAULT;
        else
            return Source.CACHE;
    }


    // returns the person age in years. if dtToday is null, it is initialized with the current date
    public static int getPersonAge(Timestamp tsDateOfBirth, Date dtToday)
    {
        if(tsDateOfBirth != null)
        {
            if(dtToday == null)
            {
                dtToday = new Date();
            }

            return getDiffYears(tsDateOfBirth.toDate(), dtToday);
        }

        return 0;
    }

    // returns the year-part of a date
    public static int getDateYear(Timestamp dt)
    {
        if(dt == null)
            return 0;

        Calendar cal = getCalendar(dt.toDate());
        return cal.get(Calendar.YEAR);
    }

    // returns the year-part of a date
    public static int getDateYear(Date dt)
    {
        if(dt == null)
            return 0;

        Calendar cal = getCalendar(dt);
        return cal.get(Calendar.YEAR);
    }

    // returns the difference between two dates, in years
    public static int getDiffYears(Date first, Date last)
    {
        if(first == null || last == null)
            return 0;

        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);

        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE)))
        {
            diff--;
        }

        return diff;
    }

    // converts date to Calendar-object
    public static Calendar getCalendar(Date date)
    {
        Calendar cal = Calendar.getInstance();

        if(date != null)
        {
            cal.setTime(date);
        }

        return cal;
    }


    // converts YMD to Date (month should be 0-11)
    public static Date getDate(int year, int month, int day)
    {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        return cal.getTime();
    }


    // displays the date-picker dialog
    public static void showDatePickerDialog(String dateStr, Context context, DatePickerDialog.OnDateSetListener mDateSetListener)
    {
        try
        {
            Date date = stringToDate(dateStr, false);
            Calendar cal = getCalendar(date);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = null;
            if(Build.VERSION.SDK_INT == 24)  // API level 24 does not have spinner mode
            {
                dialog = new DatePickerDialog(
                        context,
                        //android.R.style.Theme_DeviceDefault_Dialog_Alert,
                        android.R.style.Theme_Material_Dialog_Alert,
                        mDateSetListener,
                        year, month, day);
            }
            else
            {
                // should display the spinner mode by default
                dialog = new DatePickerDialog(
                        context,
                        //R.style.MySpinnerDatePickerStyle,
                        //android.R.style.Theme_Holo_Dialog,
                        android.R.style.Theme_Holo_Light_Dialog,
                        mDateSetListener,
                        year, month, day);
            }

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            showErrorDialog(context, "Error creating date-picker dialog.", ex);
        }
    }


    // sets text field to the given value and returns the current value
    public static String setTextField(TextView textView, String setToValue)
    {
        String prevValue = textView.getText().toString();
        textView.setText(setToValue);

        return prevValue;
    }


    // fixes the first letter to be uppercase, in case it is lowercase letter
    public static String ensureFirstLetterUppercase(String s)
    {
        if(s == null || s.length() == 0)
            return "";

        if(s.charAt(0) >= 'a' && s.charAt(0) <= 'z')
        {
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        return s;
    }


    // returns the time group of the calendar's date as a number
    public static int getTimeGroup(Calendar cal)
    {
        byte year = (byte)(cal.get(Calendar.YEAR) % 100);
        byte month = (byte)(cal.get(Calendar.MONTH) + 1);
        byte day = (byte)cal.get(Calendar.DAY_OF_MONTH);
        //byte hour = (byte)cal.get(Calendar.HOUR_OF_DAY);

        //int timeGroup = ((year << 24) & 0xff000000) | ((month << 16) & 0xff0000) | ((day << 8) & 0xff00) | (hour & 0xff);
        int timeGroup = (year * 10000) + (month * 100) + day;

        return  timeGroup;
    }


    // returns the combination of the given time and cardId
    public static String getCardLogTimeId(Date dt, String cardId, int addTime)
    {
        if(dt == null)
            dt = new Date();

        long lDtTime = (new Date()).getTime() + addTime;
        String logTimeId = cardId + "_" + Long.toString(lDtTime);

        return logTimeId;
    }


    // returns the card-log storage directory
    public static File getCardLogDirectory(Context context)
    {
        File extDir = context.getExternalFilesDir(null);
        File logDir = new File(extDir, "cardlogs");

        if(!logDir.exists())
            logDir.mkdirs();

        return logDir;
    }


    // returns the archive storage directory
    public static File getArchiveDirectory(Context context)
    {
        File extDir = context.getExternalFilesDir(null);
        File archDir = new File(extDir, "archive");

        if(!archDir.exists())
            archDir.mkdirs();

        return archDir;
    }


    // copies the source file to the target
    public static void copyFile(File source, File target) throws IOException
    {
        InputStream in = new BufferedInputStream(new FileInputStream(source));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(target));

        byte[] buffer = new byte[20000];
        int lengthRead;

        while ((lengthRead = in.read(buffer)) > 0)
        {
            out.write(buffer, 0, lengthRead);
            out.flush();
        }

        out.close();
        in.close();
    }


    // navigates to the given fragment Id
    public static void navigateToFragment(NavController navController, int resId, Bundle bundle, FragmentActivity activity)
    {
        if(navController == null)
            return;

        if(bundle != null)
        {
            navController.navigate(resId, bundle);
        }
        else
        {
            navController.navigate(resId);
        }

        // if the current fragment is MyPagerFragment, destroy its pager items
        FragmentManager fragmentManager = activity != null ? activity.getSupportFragmentManager() : null;
        if(fragmentManager != null)
        {
            NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
            Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

            TabsFragmentInterface tabsFragment = fragment instanceof TabsFragmentInterface ? (TabsFragmentInterface)fragment : null;
            MyPagerAdapter pagerAdapter = tabsFragment != null ? tabsFragment.getMyPagerAdapter() : null;

            if(pagerAdapter != null)
            {
                pagerAdapter.destroyAllPagerItems();
            }
        }
    }

    // returns the current nav-host fragment
    public static Fragment getNavHostFragment(FragmentActivity activity)
    {
        Fragment fragment = null;

        FragmentManager fragmentManager = activity != null ? activity.getSupportFragmentManager() : null;
        if(fragmentManager != null)
        {
            NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
            fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        }

        return fragment;
    }

    /*
    public static String intToStrings(float num) {

        String output = Double.toString(balance);
        while (output.length() < balance) output = "00000000" + output;
        return ((output));

    }*/


    public static String getLeadingZero(float num){
        String zeros=new String("0");
        if (num<0|| String.valueOf(num).length()>=zeros.length()) {

            return String.valueOf(num);
        }
            if(num==0){
                return zeros;
            }

            return new StringBuilder(zeros.substring(0,zeros.length()-String.valueOf(num).length())).append(String.valueOf(num)).toString();
        }


    public static String intToStrings(float num, int digits) {
        String output = Double.toString(num);
        while (output.length() < digits) output = "0" + output;
        return output;
    }

    public static String getFormatedAmount(float amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }
}

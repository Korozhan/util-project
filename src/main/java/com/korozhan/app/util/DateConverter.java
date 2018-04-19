package com.korozhan.app.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by veronika.korozhan on 06.05.2017.
 */
public class DateConverter {

    public static final String DATE_FORMAT_SINPLE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_MMDDYYY = "MM/dd/yyyy";
    public static final String DATE_FORMAT = "dd-M-yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_GREGORIAN = "dd-M-yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_DEFAULT = "MM/dd/yyyy HH:mm:ss";
    public static final String TIME_PATTERN = "%02d %02d:%02d:%02d";

    final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_GREGORIAN);
    final SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    /**
     * @throws ParseException
     */
    public static void dateExample() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        String dateInString = "22-01-2015 10:15:55 AM";
        Date date = formatter.parse(dateInString);
        TimeZone tz = TimeZone.getDefault();

        // From TimeZone Asia/Singapore
        System.out.println("TimeZone : " + tz.getID() + " - " + tz.getDisplayName());
        System.out.println("TimeZone : " + tz);
        System.out.println("Date (Singapore) : " + formatter.format(date));

        // To TimeZone America/New_York
        SimpleDateFormat sdfAmerica = new SimpleDateFormat(DATE_FORMAT);
        TimeZone tzInAmerica = TimeZone.getTimeZone("America/New_York");
        sdfAmerica.setTimeZone(tzInAmerica);

        String sDateInAmerica = sdfAmerica.format(date); // Convert to String first
        Date dateInAmerica = formatter.parse(sDateInAmerica); // Create a new Date object

        System.out.println("\nTimeZone : " + tzInAmerica.getID() + " - " + tzInAmerica.getDisplayName());
        System.out.println("TimeZone : " + tzInAmerica);
        System.out.println("Date (New York) (String) : " + sDateInAmerica);
        System.out.println("Date (New York) (Object) : " + formatter.format(dateInAmerica));

    }

    /**
     * Converts XMLGregorianCalendar to java.util.Date in Java
     *
     * @param calendar
     * @return
     */
    public static Date toDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        TimeZone zone = TimeZone.getTimeZone("Europe/Minsk");

//        Calendar calendarZoned = calendar.toGregorianCalendar(zone, null, null);
//        System.out.println("clendar=" + calendarZoned);

        return calendar.toGregorianCalendar(zone, null, null).getTime();
    }

    /**
     * Converts java.util.Date to javax.xml.datatype.XMLGregorianCalendar
     *
     * @param date
     * @return
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = null;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
        } catch (DatatypeConfigurationException ex) {
        }
        return xmlCalendar;
    }

    /**
     * Converts date in String to javax.xml.datatype.XMLGregorianCalendar
     *
     * @param date
     * @return
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(String date) {
        XMLGregorianCalendar cal = null;
        try {
            cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return cal;
    }

    /**
     * Converts unix date to java.util.Date in Java
     *
     * @param date
     * @return
     */
    public static Date toDate(Long date) {
        //test values
        long today1 = 1493611200000L;//1493733960000 operationDate   2017-05-01T07:00:00.000+03:00     operationDate="2017-05-02T17:06:00"
        long today2 = 1493759160000L;//1493759160000 transactionDate 2017-05-03T00:06:00.000+03:00   transactionDate="2017-05-01T00:00:00"

        long today1w = 1493733960000L;
        long today2w = 1493586000000L;

        return new Date(date);
    }

    /**
     * Converts string date to long
     *
     * @param stringDate
     * @return
     * @throws ParseException
     */
    public static Long stringDatetoLongDate(String stringDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DEFAULT);
        Date date = formatter.parse(stringDate);
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    /**
     * Converts string date to util.Date
     *
     * @param dateInString
     * @param dateFormat
     */
    public static Date stringDateToDate(String dateInString, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts {@link Date} into {@link XMLGregorianCalendar}
     *
     * @return {@code null} in case of null {@code date} or exception on date conversion.
     */
    public static XMLGregorianCalendar toXMLGreg(Date date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        gregorianCalendar.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static XMLGregorianCalendar toXMLGregWithoutTimezone(Date date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalendar.setTime(date);
        try {
            XMLGregorianCalendar xmlGregCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            xmlGregCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return xmlGregCalendar;
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertTime(long nanos) {
        return String.format(TIME_PATTERN, TimeUnit.MILLISECONDS.toDays(nanos),
                TimeUnit.MILLISECONDS.toHours(nanos) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(nanos)),
                TimeUnit.MILLISECONDS.toMinutes(nanos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(nanos)),
                TimeUnit.MILLISECONDS.toSeconds(nanos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(nanos)));
    }

    public static String dateToString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
}

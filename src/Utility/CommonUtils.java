package Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils
{
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");

    public static String getCurrentDateTimeString()
    {
        return DATE_FORMAT.format(new Date());
    }
}
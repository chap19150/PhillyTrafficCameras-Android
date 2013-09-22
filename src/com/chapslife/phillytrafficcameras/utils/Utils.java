package com.chapslife.phillytrafficcameras.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

public class Utils {
	private static Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
	// another pattern: "^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$"
	private static Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]{6,32}$");
	
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public static String formatSongDuration( long msecs ) {
    	// format msecs into hrs:mins:secs
    	int fract = (int)(msecs % 1000);
    	msecs /= 1000;
    	int secs = (int)(msecs % 60);
    	msecs /= 60;
    	int mins = (int)(msecs % 60);
    	int hrs = (int)(msecs / 60);
    	
    	String formatted = "";
    	if (hrs > 0) {
    		formatted = String.valueOf(hrs) + ":";
    	}
    	if ((hrs > 0) && (mins < 10)) {
    		formatted += "0";
    	}
    	formatted += String.valueOf(mins) + ":";
    	if ((formatted.length() > 0) && (secs < 10)) {
    		formatted += "0";
    	}
    	formatted += String.valueOf(secs);
    	return formatted;
    }
    
}
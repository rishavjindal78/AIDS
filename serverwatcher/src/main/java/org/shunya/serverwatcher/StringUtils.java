package org.shunya.serverwatcher;

import java.io.*;

public class StringUtils {
    public static InputStream reverse(InputStream source) {
        PipedOutputStream ps = null;
        PipedInputStream is = null;

        try {
            DataInputStream dis = new DataInputStream(source);
            String input;

            ps = new PipedOutputStream();
            is = new PipedInputStream(ps);
            PrintStream os = new PrintStream(ps);

            while ((input = dis.readLine()) != null) {
                os.println(reverseString(input));
            }
            os.close();
        } catch (Exception e) {
            System.out.println("StringUtils reverse: " + e);
        }
        return is;
    }

    private static String reverseString(String source) {
        int i, len = source.length();
        StringBuffer dest = new StringBuffer(len);

        for (i = (len - 1); i >= 0; i--) {
            dest.append(source.charAt(i));
        }
        return dest.toString();
    }

    public static InputStream sort(InputStream source) {

        int MAXWORDS = 50;

        PipedOutputStream ps = null;
        PipedInputStream is = null;

        try {
            DataInputStream dis = new DataInputStream(source);
            String input;

            ps = new PipedOutputStream();
            is = new PipedInputStream(ps);
            PrintStream os = new PrintStream(ps);

            String listOfWords[] = new String[MAXWORDS];
            int numwords = 0, i = 0;

            while ((listOfWords[numwords] = dis.readLine()) != null) {
                numwords++;
            }
            quicksort(listOfWords, 0, numwords - 1);
            for (i = 0; i < numwords; i++) {
                os.println(listOfWords[i]);
            }
            os.close();
        } catch (Exception e) {
            System.out.println("StringUtils sort: " + e);
        }
        return is;
    }

    private static void quicksort(String a[], int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        if (lo >= hi) {
            return;
        }
        String mid = a[(lo + hi) / 2];
        while (lo < hi) {
            while (lo < hi && a[lo].compareTo(mid) < 0) {
                lo++;
            }
            while (lo < hi && a[hi].compareTo(mid) > 0) {
                hi--;
            }
            if (lo < hi) {
                String T = a[lo];
                a[lo] = a[hi];
                a[hi] = T;
            }
        }
        if (hi < lo) {
            int T = hi;
            hi = lo;
            lo = T;
        }
        quicksort(a, lo0, lo);
        quicksort(a, lo == lo0 ? lo + 1 : lo, hi0);
    }

    public static String getExceptionStackTrace(Throwable t, int maxLength) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String exception = sw.toString();
        if(maxLength >0 && maxLength< exception.length()){
            return exception.substring(0,maxLength);
        }
        return exception;
    }
    
    public static String getExceptionHeaders(Throwable t) {
        String headers="";
        do{
            headers += "\n"+t.toString();
            t=t.getCause();
        }
        while(t!=null);
            return headers;
        }

    public static String getElapsedTimeInString(long elapsedTime) {
        String format = String.format("%%0%dd", 2);
        elapsedTime = elapsedTime / 1000;
        String seconds = String.format(format, elapsedTime % 60);
        String minutes = String.format(format, (elapsedTime % 3600) / 60);
        String hours = String.format(format, elapsedTime / 3600);
        String time = hours + ":" + minutes + ":" + seconds;
        return time;
    }
}
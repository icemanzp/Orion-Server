package com.jack.netty.test;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateCompTest extends TestCase {

    public DateCompTest(String name) {
        super(name);
    }

    public void testComp() {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        Date date = new Date(System.currentTimeMillis());


        try {
            Date tDate = df.parse(df.format(date));

            Date dDate = df.parse("2015-9-5");

            System.out.println("Now : " + tDate.toString() + " dDate: " + dDate.toString());

            System.out.println(dDate.compareTo(tDate));

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        assertTrue(true);
    }

    public void testIPRex() {
        String str = "10.6.2.56:9092,10.6.2.57:9092,10.6.2.58:9092";
        String pattern = "((([0-9]{1,3}([:][0-9]{1,5})?[.]?){4})[,]?)*";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        System.out.println(m.matches());
    }

    public void testShufix() {
        String str = "*.htm$,*.json,*/webservice/*";
        String pattern = "(/?[*]?[a-z0-9A-Z_]*/?[*]?[.]?[a-z0-9A-Z_]*[$]?[,]?)*";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        System.out.println(m.matches());
    }

    public void testURI() {
        String path = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("").toString());

        System.out.println(path);


        path = path.substring(path.indexOf("/"));
        //path = path.replaceAll("%20", " ");

        System.out.println(path);

        System.out.println(path + "nettyserver.xml");
        try {
            FileInputStream fis = new FileInputStream(path + "nettyserver.xml");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testString() {
        pring("*.htm$,*.json");
    }

    private void pring(String... url) {
        for (String item : url) {
            System.out.println(item);
        }
    }
}

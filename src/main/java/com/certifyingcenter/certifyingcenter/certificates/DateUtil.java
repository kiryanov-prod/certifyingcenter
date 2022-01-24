package com.certifyingcenter.certifyingcenter.certificates;

import com.certifyingcenter.certifyingcenter.entryies.CertificateEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class DateUtil {
    public Date getCurrentDateAndTime() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String currentDateAndTime = dtf.format(now);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDateAndTime);
    }

    public String convertDateToString(Date date){
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat2.format(date);
    }
}

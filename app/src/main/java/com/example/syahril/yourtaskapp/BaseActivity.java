package com.example.syahril.yourtaskapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog mDialog;
    //    Activity lifecycle tracking
    public static int RESUME = 0;
    public static int START = -1;
    public static int CREATE = -2;
    public static int PAUSE = -3;
    public static int STOP = -4;
    public static int DESTROY = -5;
    public AtomicInteger STATE = new AtomicInteger(-5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        STATE.set(CREATE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        STATE.set(STOP);
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        STATE.set(PAUSE);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        STATE.set(DESTROY);
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        STATE.set(START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        STATE.set(RESUME);
    }


    public void showSnackBar(View root, String message) {
        Snackbar snackbar = Snackbar
                .make(root, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    public void showProgressDialog() {
//        if (STATE.get() < RESUME) return;
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        if (!mDialog.isShowing()) {
            mDialog.show();
        }

    }

    public void dismissProgressDialog(){
        if(mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog.cancel();
        }
    }

    public String convertDateBasedOnDay(String givenDateString){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        long timeInMilliseconds=0;
        String convertedDate="";
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
           convertedDate= convertTime(timeInMilliseconds);
           System.out.println("cobvert " +timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    private String convertTime(long created){
        Date date = null;
        Date curDate=null;
        Date currentTime=null;
        //check if today,display time,if yesterday display yesterday.if more than 2 days,display date
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault());
      ///  String dateString = formatter.format(new Date(created * 1000L));
        String dateString=  DateFormat.getDateTimeInstance().format(new Date(created));


        String currDate = formatter.format(new Date());

        //// currentTime = Calendar.getInstance().getTime();
        try {

            date = formatter.parse(dateString);
            curDate=formatter.parse(currDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (curDate.compareTo(date) > 0) {
            //// System.out.println("Date1 is after Date2");
            //calculate if 1 day diff,display yesterday,if more,display date
            int diff =showDiff(date,curDate);
            if(diff==1){
                dateString="YESTERDAY";
            }else{
                SimpleDateFormat formatterDate = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault());
                dateString = formatterDate.format(new Date(created));
            }



        } else if (curDate.compareTo(date) < 0) {
            //imposible
            ////System.out.println("Date1 is before Date2");
        } else if (curDate.compareTo(date) == 0) {
            //today
//            SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm",Locale.getDefault());
//            dateString=formatterTime.format(new Date(created));
            dateString="TODAY";
            ////  System.out.println("Date1 is equal to Date2");
        } else {
            System.out.println("How to get here?");
        }




        return dateString;
    }


    public int showDiff(Date before,Date now) {
        Period age = new Period(new DateTime(before), new DateTime(now));

        int days =age.getDays();
        return days;
    }
}



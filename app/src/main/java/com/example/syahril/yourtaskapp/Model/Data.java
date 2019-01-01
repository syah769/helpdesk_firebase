package com.example.syahril.yourtaskapp.Model;

public class Data {

    private String title;
    private String note;
    private String staff;

    private String date;
    private String id;



    public Data(){

    }

    public Data(String title, String note, String staff, String date, String id) {
        this.title = title;
        this.note = note;
        this.staff = staff;

        this.date = date;
        this.id = id;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

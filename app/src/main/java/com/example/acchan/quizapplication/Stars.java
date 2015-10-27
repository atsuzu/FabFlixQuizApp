package com.example.acchan.quizapplication;

/**
 * Created by Acchan on 6/1/2015.
 */
public class Stars {

    // Labels table name
    public static final String TABLE = "Stars";

    public Stars(int newID, String newfname, String newlname, String newdob){
        star_ID = newID;
        fname = newfname;
        lname = newlname;
        dob = newdob;
    }


    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_fname = "fname";
    public static final String KEY_lname = "lname";
    public static final String KEY_dob = "dob";

    // property help us to keep data
    public int star_ID;
    public String fname;
    public String lname;
    public String dob;

}

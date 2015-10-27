package com.example.acchan.quizapplication;

/**
 * Created by Acchan on 6/1/2015.
 */
public class Movies {

    // Labels table name
    public static final String TABLE = "Movies";

    public Movies(int newID, String newtitle, String newyear, String newdirector){
        movie_ID = newID;
        title = newtitle;
        year = newyear;
        director = newdirector;
    }


    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_fname = "title";
    public static final String KEY_lname = "year";
    public static final String KEY_dob = "director";

    // property help us to keep data
    public int movie_ID;
    public String title;
    public String year;
    public String director;

}

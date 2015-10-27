package com.example.acchan.quizapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "moviedb";
    private static final int DATABASE_VERSION = 1;

    //DATABASE ENTRY FOR STARS
    private static final String STAR_TABLE_NAME = "stars";
    private static final String STAR_ID = "_id";
    private static final String STAR_FNAME = "fname";
    private static final String STAR_LNAME = "lname";
    private static final String STAR_DOB = "dob";
    private static final String STAR_FILE_NAME = "stars.csv";
    private static final String STAR_CREATE_TABLE = "CREATE TABLE "+ STAR_TABLE_NAME + "("+STAR_ID+" integer primary key autoincrement, "+STAR_FNAME+" text not null, " + STAR_LNAME + " text not null, " + STAR_DOB + " text not null);";

    //DATABASE ENTRY FOR STARS_IN_MOVIES
    private static final String SM_TABLE_NAME = "stars_in_movies";
    private static final String SM_MOVIE_ID = "movie_id";
    private static final String SM_STAR_ID = "star_id";
    private static final String SM_FILE_NAME = "stars_in_movies.csv";
    private static final String SM_CREATE_TABLE = "CREATE TABLE "+ SM_TABLE_NAME + "("+SM_STAR_ID+" integer, "+ SM_MOVIE_ID +" integer, FOREIGN KEY(" + SM_STAR_ID + ") REFERENCES stars(_id), FOREIGN KEY (" + SM_MOVIE_ID + ") REFERENCES movies(_id));";


    //DATABASE ENTRY FOR MOVIES
    private static final String MOVIE_TABLE_NAME = "movies";
    private static final String MOVIE_ID = "_id";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_YEAR = "year";
    private static final String MOVIE_DIRECTOR = "director";
    private static final String MOVIE_FILE_NAME = "movies.csv";
    private static final String MOVIE_CREATE_TABLE = "CREATE TABLE "+ MOVIE_TABLE_NAME + "("+MOVIE_ID+" integer primary key autoincrement, "+MOVIE_TITLE+" text not null, " + MOVIE_YEAR + " text not null, " + MOVIE_DIRECTOR + " text not null);";


    //DATABASE ENTRY JUSTTTT FORRRR STATISTICS. :)
    private static final String STATS_TABLE_NAME = "statistics";
    private static final String STATS_ID = "id";
    private static final String STATS_CURRENT_SCORE = "currentScore";
    private static final String STATS_CURRENT_TOTAL = "currentTotal";
    private static final String STATS_OVERALL_SCORE = "overallScore";
    private static final String STATS_OVERALL_TOTAL = "overallTotal";
    private static final String STATS_CREATE_TABLE = "CREATE TABLE " + STATS_TABLE_NAME + "(" + STATS_ID + " integer primary key autoincrement, " + STATS_CURRENT_SCORE + " integer, " + STATS_CURRENT_TOTAL + " integer, " + STATS_OVERALL_SCORE + " integer, " + STATS_OVERALL_TOTAL + " integer);";

    private SQLiteDatabase mDb;
    private Context mContext;

    public DbAdapter(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = ctx;
        this.mDb = getWritableDatabase();

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STAR_CREATE_TABLE);
        db.execSQL(MOVIE_CREATE_TABLE);
        db.execSQL(SM_CREATE_TABLE);
        db.execSQL(STATS_CREATE_TABLE);
        // populate database
        try {
            db.execSQL("INSERT INTO " + STATS_TABLE_NAME + " VALUES (1, 0, 0, 0, 0);");

            BufferedReader inStars = new BufferedReader(new InputStreamReader(mContext.getAssets().open(STAR_FILE_NAME)));
            String lineStar;

            while ((lineStar = inStars.readLine()) != null) {
                String[] sarray = lineStar.split(",");
                ContentValues starValues = new ContentValues();
                starValues.put(STAR_ID, sarray[0]);
                starValues.put(STAR_FNAME, sarray[1]);
                starValues.put(STAR_LNAME, sarray[2]);
                starValues.put(STAR_DOB, sarray[3]);
                db.insert(STAR_TABLE_NAME, null, starValues);
            }

            BufferedReader inMovies = new BufferedReader(new InputStreamReader(mContext.getAssets().open(MOVIE_FILE_NAME)));
            String lineMovie;
            while ((lineMovie = inMovies.readLine()) != null) {
                String[] marray = lineMovie.split(",");
                ContentValues movieValues = new ContentValues();
                movieValues.put(MOVIE_ID, marray[0]);
                movieValues.put(MOVIE_TITLE, marray[1]);
                movieValues.put(MOVIE_YEAR, marray[2]);
                movieValues.put(MOVIE_DIRECTOR, marray[3]);
                db.insert(MOVIE_TABLE_NAME, null, movieValues);
            }

            BufferedReader SinM = new BufferedReader(new InputStreamReader(mContext.getAssets().open(SM_FILE_NAME)));
            String lineSinM;
            while ((lineSinM = SinM.readLine()) != null) {
                String[] smarray = lineSinM.split(",");
                ContentValues smValues = new ContentValues();
                smValues.put(SM_STAR_ID, smarray[0]);
                smValues.put(SM_MOVIE_ID, smarray[1]);
                db.insert(SM_TABLE_NAME, null, smValues);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        db.execSQL(MOVIE_CREATE_TABLE);
//        // populate database
//        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(mContext.getAssets().open(MOVIE_FILE_NAME)));
//            String line;
//
//            while((line=in.readLine())!=null) {
//                String[] marray = line.split(",");
//                ContentValues values = new ContentValues();
//                values.put(MOVIE_ID, marray[0]);
//                values.put(MOVIE_TITLE, marray[1]);
//                values.put(MOVIE_YEAR, marray[2]);
//                values.put(MOVIE_DIRECTOR, marray[3]);
//                db.insert(STAR_TABLE_NAME, null, values);
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+STAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+MOVIE_TABLE_NAME);
        onCreate(db);
    }

    public Cursor fetchAllStars() {
        return mDb.query(STAR_TABLE_NAME, new String[] {STAR_ID, STAR_FNAME, STAR_LNAME, STAR_DOB}, null, null, null, null, null);
    }

    public Cursor fetchAllMovies() {
        return mDb.query(MOVIE_TABLE_NAME, new String[] {MOVIE_ID, MOVIE_TITLE, MOVIE_YEAR, MOVIE_DIRECTOR}, null, null, null, null, null);
    }

    //    Which star was in the movie 'movieTitle'?
    public String case2(String movieName) {
        String query = "select fname, lname from stars INNER JOIN stars_in_movies on stars._id = stars_in_movies.star_id INNER JOIN movies on stars_in_movies.movie_id = movies._id WHERE title = '" + movieName + "';";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
//            product.setID(Integer.parseInt(cursor.getString(0)));
//            product.setProductName(cursor.getString(1));
//            product.setQuantity(Integer.parseInt(cursor.getString(2)));
            String name = cursor.getString(0) + " " + cursor.getString(1);
            cursor.close();
            db.close();
            return name;
        } else {
            cursor.close();
            db.close();
            return "none";
        }
    }

//    In which movie the stars 'starName1' and 'starName2' appear together?
    public String case3(String fname1, String fname2){//, String fname2, String lname2) {
        String query = "select fname, lname, title, COUNT (title) c from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars._id = stars_in_movies.star_id WHERE fname = '"+ fname1 +"' OR fname = '" + fname2 + "' GROUP BY title HAVING c > 1;";

//        select fname, lname, title, COUNT (title) c from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars._id = stars_in_movies.star_id WHERE fname = 'Liv' OR fname = 'Elijah' GROUP BY title HAVING c > 1;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
//            product.setID(Integer.parseInt(cursor.getString(0)));
//            product.setProductName(cursor.getString(1));
//            product.setQuantity(Integer.parseInt(cursor.getString(2)));
            String title = cursor.getString(2);
            cursor.close();
            db.close();
            return title;
        } else {
            cursor.close();
            db.close();
            return "none";
        }
    }

    //Who directed the star 'starName'?
    public String case4(String fname, String lname) {
        String query = "select director from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars._id = stars_in_movies.star_id where fname = \""+ fname +"\" AND lname = \""+ lname +"\";";

//      select director, fname, lname from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars._id = stars_in_movies.star_id where fname = "Renee" AND lname = "Zellweger";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
//            product.setID(Integer.parseInt(cursor.getString(0)));
//            product.setProductName(cursor.getString(1));
//            product.setQuantity(Integer.parseInt(cursor.getString(2)));
            String name = cursor.getString(0);
            cursor.close();
            db.close();
            return name;
        } else {
            cursor.close();
            db.close();
            return "none";
        }
    }

    //"Which star appears in both movies 'movieTitle1' and 'movieTitle2'?"
    public String case5(String movie1, String movie2) {
        String query = "select fname, lname, title, COUNT(fname) c from stars INNER JOIN stars_in_movies on stars_in_movies.star_id = stars._id INNER JOIN movies on stars_in_movies.movie_id = movies._id where title = '"+ movie1 +"' OR title = '"+ movie2 +"' GROUP BY fname HAVING c > 1;";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
//            product.setID(Integer.parseInt(cursor.getString(0)));
//            product.setProductName(cursor.getString(1));
//            product.setQuantity(Integer.parseInt(cursor.getString(2)));
            String name = cursor.getString(0) + " " + cursor.getString(1);
            cursor.close();
            db.close();
            return name;
        } else {
            cursor.close();
            db.close();
            return "none";
        }
    }

    //"Which star did not appear in the same movie with the star 'starName'?"
    public String case6() {
        String query = "select group_concat(fname || \" \" || lname) from stars s left join stars_in_movies sm on s._id = sm.star_id left join movies m on sm.movie_id = m._id group by title having count(*) = 4 order by random() limit 1;";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            //cursor.moveToFirst();
//            product.setID(Integer.parseInt(cursor.getString(0)));
//            product.setProductName(cursor.getString(1));
//            product.setQuantity(Integer.parseInt(cursor.getString(2)));
            String name = cursor.getString(0);
            cursor.close();
            db.close();
            return name;
        } else {
            cursor.close();
            db.close();
            return "none";
        }
    }

    //"Who directed the star 'starName' in year 'movieYear'?"
    public String case7(String fname, String lname, String myear) {
        String query = "select fname, director, year from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars_in_movies.star_id = stars._id where fname = \""+fname+"\" AND year = \"" + myear + "\";";


//        select fname, director, year from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars_in_movies.star_id = stars._id where fname = 'Robin' AND year = '1999';
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
//            product.setID(Integer.parseInt(cursor.getString(0)));
//            product.setProductName(cursor.getString(1));
//            product.setQuantity(Integer.parseInt(cursor.getString(2)));
            String name = cursor.getString(1);
            cursor.close();
            db.close();
            return name;
        } else {
            cursor.close();
            db.close();
            return "none";
        }
    }


    public int getStatsCurrentScore() {
        String query = "select currentScore from statistics where id = 1;";

//        select fname, director, year from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars_in_movies.star_id = stars._id where fname = 'Robin' AND year = '1999';
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

//        cursor.moveToFirst();
        cursor.moveToFirst();
        int results = cursor.getInt(0);
        cursor.close();
        db.close();
        return results;

    }

    public int getStatsCurrentTotal() {
        String query = "select currentTotal from statistics where id = 1;";

//        select fname, director, year from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars_in_movies.star_id = stars._id where fname = 'Robin' AND year = '1999';
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        cursor.moveToFirst();
        int results = cursor.getInt(0);
        cursor.close();
        db.close();
        return results;

    }

    public int getStatsOverallScore() {
        String query = "select overallScore from statistics where id = 1;";

//        select fname, director, year from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars_in_movies.star_id = stars._id where fname = 'Robin' AND year = '1999';
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        cursor.moveToFirst();
        int results = cursor.getInt(0);
        cursor.close();
        db.close();
        return results;

    }

    public int getStatsOverallTotal() {
        String query = "select overallTotal from statistics where id = 1;";

//        select fname, director, year from movies INNER JOIN stars_in_movies on stars_in_movies.movie_id = movies._id INNER JOIN stars on stars_in_movies.star_id = stars._id where fname = 'Robin' AND year = '1999';
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        cursor.moveToFirst();
        int results = cursor.getInt(0);
        cursor.close();
        db.close();
        return results;

    }

    public void setStatsCurrentScore(int score) {
        String query = "UPDATE " + STATS_TABLE_NAME + " Set " + STATS_CURRENT_SCORE + " = " + score + " WHERE id = 1;";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public void setStatsCurrentTotal(int score) {
        String query = "UPDATE " + STATS_TABLE_NAME + " Set " + STATS_CURRENT_TOTAL + " = " + score + " WHERE id = 1;";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public void setStatsOverallScore(int score) {
        String query = "UPDATE " + STATS_TABLE_NAME + " Set " + STATS_OVERALL_SCORE + " = " + score + " WHERE id = 1;";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
    public void setStatsOverallTotal(int score) {
        String query = "UPDATE " + STATS_TABLE_NAME + " Set " + STATS_OVERALL_TOTAL + " = " + score + " WHERE id = 1;";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }
}

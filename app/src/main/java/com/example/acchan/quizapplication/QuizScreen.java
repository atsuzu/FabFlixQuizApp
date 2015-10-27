package com.example.acchan.quizapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Acchan on 5/31/2015.
 */
public class QuizScreen extends Activity{

    //Question Number
    int count = 0;
    private static final String FILE_NAME = "questions.txt";

    Button choice1, choice2, choice3, choice4;

    boolean paused = false;

    List<String> quesList;
    List<Stars> starsList;
    List<Movies> moviesList;

    private TextView mTimeText;
    private Handler mHandler = new Handler();
    private long mStart;
    private static long duration = 180000;
    private long mPause;

    // Usually this can be a field rather than a method variable
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = 0;
    int randomChoice1;
    int randomChoice2;
    int randomChoice3;
    int randomChoice4;

    //will be 1 - 4
    int correctChoice = 0;

    //the score of the user
    int score = 0;

    TextView scoreText;

    Context ctx;

    boolean created = false;
    long now;
    private Runnable updateTask = new Runnable() {
        public void run() {
            now = SystemClock.uptimeMillis();
            long elapsed = duration - (now - mStart);
            mHandler.postAtTime(this, now + 1000);

            if(paused);
            else if (elapsed > 0)
            {
                // skip over.
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                if (seconds < 10) {
                    mTimeText.setText("" + minutes + ":0" + seconds);
                } else {
                    mTimeText.setText("" + minutes + ":" + seconds);
                }
            }
            else {
                mHandler.removeCallbacks(this);

                DbAdapter updateDB = new DbAdapter(ctx);
                updateDB.setStatsCurrentScore(score);
                updateDB.setStatsCurrentTotal(count + 1);

                updateDB.setStatsOverallScore(updateDB.getStatsCurrentScore() + updateDB.getStatsOverallScore());
                updateDB.setStatsOverallTotal(updateDB.getStatsCurrentTotal() + updateDB.getStatsOverallTotal());

                updateDB.close();
                //end the session?
                finish();

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizscreen);

        ctx = this.getApplicationContext();

        //Does the quit, next button
        final Button quitButton = (Button)this.findViewById(R.id.quitButton);
        final Button nextButton = (Button)this.findViewById(R.id.nextButton);
        final Button submitButton = (Button)this.findViewById(R.id.submitButton);
        final Button pauseButton = (Button)this.findViewById(R.id.pauseButton);
        final Button unpauseButton = (Button)this.findViewById(R.id.unpauseButton);

        unpauseButton.setVisibility(View.GONE);

        //required for timer
        mTimeText = (TextView)this.findViewById(R.id.timeText);
        mStart = SystemClock.uptimeMillis();
        mHandler.post(updateTask);

        //list for the questions
        quesList = new ArrayList<String>();
        AssetManager assetManager = getAssets();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(assetManager.open(FILE_NAME)));
            String line;

            while((line=in.readLine())!=null) {
                quesList.add(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        choice1 = (Button)this.findViewById(R.id.choice1);
        choice2 = (Button)this.findViewById(R.id.choice2);
        choice3 = (Button)this.findViewById(R.id.choice3);
        choice4 = (Button)this.findViewById(R.id.choice4);


        final TextView questionNum = (TextView)this.findViewById(R.id.questionNumber);
        questionNum.setText("Question " + (count+1));

        final TextView answerText = (TextView)this.findViewById(R.id.answerText);
        //start off with blank answer text
        answerText.setText("");

        scoreText = (TextView)this.findViewById(R.id.scoreText);

        DbAdapter db = new DbAdapter(this);
        Cursor cur1 = db.fetchAllStars();

        starsList = new ArrayList<Stars>();
        cur1.moveToFirst();
        while(!cur1.isAfterLast()){
            //quesList.add(cur.getString(0));
            Stars toAddStars = new Stars(cur1.getInt(0), cur1.getString(1), cur1.getString(2), cur1.getString(3));
            starsList.add(toAddStars);
            cur1.moveToNext();
        }

        Cursor cur2 = db.fetchAllMovies();

        moviesList = new ArrayList<Movies>();
        cur2.moveToFirst();
        while(!cur2.isAfterLast()){
            //quesList.add(cur.getString(0));
            Movies toAddMovies = new Movies(cur2.getInt(0), cur2.getString(1), cur2.getString(2), cur2.getString(3));
            moviesList.add(toAddMovies);
            cur2.moveToNext();
        }

        cur1.close();
        cur2.close();
        db.close();

        //question text view.
        randomChoice1 = rand.nextInt((moviesList.size()-1 - 0) + 1) + 0;
        randomChoice2 = rand.nextInt((moviesList.size()-1 - 0) + 1) + 0;
        randomChoice3 = rand.nextInt((moviesList.size()-1 - 0) + 1) + 0;
        randomChoice4 = rand.nextInt((moviesList.size()-1 - 0) + 1) + 0;

        randomNum = rand.nextInt(((quesList.size() - 1) - 0) + 1) + 0;


        final TextView questionText = (TextView)this.findViewById(R.id.quizQuestion);
        questionText.setText((quesList.get(0)).replace("'movieTitle'", moviesList.get(randomChoice1).title));
        choice1.setText(moviesList.get(randomChoice1).director);
        choice2.setText(moviesList.get(randomChoice2).director);
        choice3.setText(moviesList.get(randomChoice3).director);
        choice4.setText(moviesList.get(randomChoice4).director);

        correctChoice = 1;


        // Retrieve the button, change its value and add an event listener
        quitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int data;
                //calculate the statistics.
                DbAdapter updateDB = new DbAdapter(ctx);
                updateDB.setStatsCurrentScore(score);
                updateDB.setStatsCurrentTotal(count+1);

                updateDB.setStatsOverallScore(updateDB.getStatsCurrentScore() + updateDB.getStatsOverallScore());
                updateDB.setStatsOverallTotal(updateDB.getStatsCurrentTotal() + updateDB.getStatsOverallTotal());

                updateDB.close();
                //end the session?
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //make the submit button visible again.
                submitButton.setVisibility(View.VISIBLE);

                //random number between 1 and question list size.
                randomNum = rand.nextInt(((quesList.size() - 1) - 0) + 1) + 0;

                //when next is pressed, score out of total is also updated
                scoreText.setText(Integer.toString(score) + " out of " + Integer.toString(count + 1));

                //take off the checks
                ((RadioButton)choice1).setChecked(false);
                ((RadioButton)choice2).setChecked(false);
                ((RadioButton)choice3).setChecked(false);
                ((RadioButton)choice4).setChecked(false);

                //Next question each time buttom is clicked
                count++;
//                if (count < quesList.size()) {
//                    questionNum.setText("Question " + (count + 1));
//                    questionText.setText(quesList.get(count) + starsList.get(count).fname + " " + starsList.get(count).lname + " " + moviesList.get(count).title);
//
//                    //reset text each time
//                    answerText.setText("");
//                } else {
//                    quitButton.setText("Finish");
//                    nextButton.setVisibility(View.GONE);
//                }

                //switch case for the different questions.
                questionNum.setText("Question " + (count + 1));
                // questionText.setText(quesList.get(count) + starsList.get(count).fname + " " + starsList.get(count).lname + " " + moviesList.get(count).title);
                String modifiedQuestion = " ";
                int randomMovieIndex;
                switch(randomNum)
                {
                    case 0:
                        //"Who directed the movie 'movieTitle'?"

                        randomChoice1 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice2 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice3 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice4 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        choice1.setText(moviesList.get(randomChoice1).director);
                        choice2.setText(moviesList.get(randomChoice2).director);
                        choice3.setText(moviesList.get(randomChoice3).director);
                        choice4.setText(moviesList.get(randomChoice4).director);

                        modifiedQuestion = (quesList.get(0)).replace("'movieTitle'", moviesList.get(randomChoice2).title);
                        correctChoice = 2;
                        break;
                    case 1:
                        //"When was the movie 'movieTitle' released?"

                        randomChoice1 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice2 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice3 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice4 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        choice1.setText(moviesList.get(randomChoice1).year);
                        choice2.setText(moviesList.get(randomChoice2).year);
                        choice3.setText(moviesList.get(randomChoice3).year);
                        choice4.setText(moviesList.get(randomChoice4).year);
                        modifiedQuestion = (quesList.get(1)).replace("'movieTitle'", moviesList.get(randomChoice3).title);
                        correctChoice = 3;

                        break;
                    case 2:
                        //"Which star was in the movie 'movieTitle'?"
                        randomChoice1 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice2 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice3 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice4 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;

                        randomMovieIndex = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        DbAdapter dbC2 = new DbAdapter(ctx);
                        String answerName = dbC2.case2(moviesList.get(randomMovieIndex).title);

                        choice1.setText(answerName);
                        choice2.setText(starsList.get(randomChoice2).fname + starsList.get(randomChoice2).lname);
                        choice3.setText(starsList.get(randomChoice3).fname + starsList.get(randomChoice3).lname);
                        choice4.setText(starsList.get(randomChoice4).fname + starsList.get(randomChoice4).lname);

                        modifiedQuestion = (quesList.get(2)).replace("'movieTitle'", moviesList.get(randomMovieIndex).title);
                        correctChoice = 1;
                        dbC2.close();
                        break;
                    case 3:
                        //"In which movie the stars 'starName1' and 'starName2' appear together?"
                        randomChoice1 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice2 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice3 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice4 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        int randomStarIndex1 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        int randomStarIndex2 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;

                        DbAdapter dbC3 = new DbAdapter(ctx);
                        String answerNameC3 = dbC3.case3(starsList.get(randomStarIndex1).fname, starsList.get(randomStarIndex2).fname);

                        choice1.setText(moviesList.get(randomChoice1).title);
                        choice2.setText(moviesList.get(randomChoice3).title);
                        choice3.setText(answerNameC3);
                        choice4.setText(moviesList.get(randomChoice4).title);

                        correctChoice = 3;
                        dbC3.close();

                        modifiedQuestion = ((quesList.get(3)).replace("'starName1'", starsList.get(randomStarIndex1).fname + " " + starsList.get(randomStarIndex1).lname)).replace("'starName2'", starsList.get(randomStarIndex2).fname + " " + starsList.get(randomStarIndex2).lname);
                        break;
                    case 4:
                        //Who directed the star 'starName'?
                        randomChoice1 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice2 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice3 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        randomChoice4 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        int randomStarIndexC4 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        DbAdapter dbC4 = new DbAdapter(ctx);
                        String answerNameC4 = dbC4.case4(starsList.get(randomStarIndexC4).fname, starsList.get(randomStarIndexC4).lname);

                        choice1.setText(moviesList.get(randomChoice1).director);
                        choice2.setText(moviesList.get(randomChoice3).director);
                        choice3.setText(answerNameC4);
                        choice4.setText(moviesList.get(randomChoice4).director);

                        correctChoice = 3;
                        dbC4.close();

                        modifiedQuestion = (quesList.get(4)).replace("'starName'", starsList.get(randomStarIndexC4).fname + " " + starsList.get(randomStarIndexC4).lname);
                        break;
                    case 5:
                        //"Which star appears in both movies 'movieTitle1' and 'movieTitle2'?"
                        randomChoice1 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice2 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice3 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice4 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;

                        int randomMovieIndex1C5 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        int randomMovieIndex2C5 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        DbAdapter dbC5 = new DbAdapter(ctx);
                        String answerNameC5 = dbC5.case5(moviesList.get(randomMovieIndex1C5).title, moviesList.get(randomMovieIndex2C5).title);

                        choice1.setText(starsList.get(randomChoice1).fname + starsList.get(randomChoice1).lname);
                        choice2.setText(answerNameC5);
                        choice3.setText(starsList.get(randomChoice3).fname + starsList.get(randomChoice3).lname);
                        choice4.setText(starsList.get(randomChoice4).fname + starsList.get(randomChoice4).lname);

                        correctChoice = 2;
                        dbC5.close();

                        modifiedQuestion = ((quesList.get(5)).replace("'movieTitle1'", moviesList.get(randomMovieIndex1C5).title)).replace("'movieTitle2'", moviesList.get(randomMovieIndex2C5).title);
                        break;
                    case 6:
                        //"Which star did not appear in the same movie with the star 'starName'?"
                        //modifiedQuestion = ("STILL WORKING ON");
                        DbAdapter dbC6 = new DbAdapter(ctx);
                        String answerNameC6 = dbC6.case6();
                        String[] names = answerNameC6.split(",");

                        int randomStarIndex6 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;

                        choice1.setText(names[1]);
                        choice2.setText(names[2]);
                        choice3.setText(names[3]);
                        choice4.setText(starsList.get(randomStarIndex6).fname + " " + starsList.get(randomStarIndex6).lname);


                        correctChoice = 4;
                        dbC6.close();

                        modifiedQuestion = ("Which star did not appear in the same movie with the star "+ names[0] + " ?");

                        break;
                    case 7:
                        //"Who directed the star 'starName' in year 'movieYear'?"
                        randomChoice1 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice2 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice3 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;
                        randomChoice4 = rand.nextInt(((starsList.size() - 1) - 0) + 1) + 0;

                        int randomStarIndexC7 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;
                        int randomMovieIndexC7 = rand.nextInt(((moviesList.size() - 1) - 0) + 1) + 0;

                        DbAdapter dbC7 = new DbAdapter(ctx);
                        String answerNameC7 = dbC7.case7(starsList.get(randomStarIndexC7).fname, starsList.get(randomStarIndexC7).lname, moviesList.get(randomMovieIndexC7).year);

                        choice1.setText(answerNameC7);
                        choice2.setText(starsList.get(randomChoice2).fname + starsList.get(randomChoice1).lname);
                        choice3.setText(starsList.get(randomChoice3).fname + starsList.get(randomChoice3).lname);
                        choice4.setText(starsList.get(randomChoice4).fname + starsList.get(randomChoice4).lname);

                        correctChoice = 1;
                        dbC7.close();

                        modifiedQuestion = ((quesList.get(7)).replace("'starName'", starsList.get(randomStarIndexC7).fname + " " + starsList.get(randomStarIndexC7).lname)).replace("'movieYear'", moviesList.get(randomMovieIndexC7).year);
                        break;
                }
                questionText.setText(modifiedQuestion);

                //reset text each time
                answerText.setText("");
            }


        });

        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String answerThing = "WRONG\n The answer was: " + Integer.toString(correctChoice);
                //Check if the answer radio is checked or not and ++ the score
                if(((RadioButton)choice1).isChecked() && correctChoice == 1) {
                    score++;
                    answerThing = "CORRECT";
                }
                else if(((RadioButton)choice2).isChecked() && correctChoice == 2){
                    score++;
                    answerThing = "CORRECT";
                }
                else if(((RadioButton)choice3).isChecked() && correctChoice == 3) {
                    score++;
                    answerThing = "CORRECT";
                }
                else if(((RadioButton)choice4).isChecked() && correctChoice == 4) {
                    score++;
                    answerThing = "CORRECT";
                }

                answerText.setText(answerThing);
                scoreText.setText(Integer.toString(score) + " out of " + Integer.toString(count + 1));

                //Make submit button invisible so user can't submit twice.
                submitButton.setVisibility(View.GONE);

                //change the answer text to the actual answer.

            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                pauseButton.setVisibility(View.GONE);
                unpauseButton.setVisibility(View.VISIBLE);

                questionText.setVisibility(View.GONE);

                choice1.setVisibility(View.GONE);
                choice2.setVisibility(View.GONE);
                choice3.setVisibility(View.GONE);
                choice4.setVisibility(View.GONE);

                submitButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);

                answerText.setText("PAUSED");

                paused = true;
                mPause = SystemClock.uptimeMillis();

//                while(paused){
//                    SystemClock.sleep(100);
//                }
            }
        });

        unpauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                unpauseButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);

                answerText.setVisibility(View.VISIBLE);
                questionText.setVisibility(View.VISIBLE);

                choice1.setVisibility(View.VISIBLE);
                choice2.setVisibility(View.VISIBLE);
                choice3.setVisibility(View.VISIBLE);
                choice4.setVisibility(View.VISIBLE);

                submitButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);

                answerText.setText("");

                mPause = SystemClock.uptimeMillis() - mPause;
                mStart += mPause;
                paused = false;
            }
        });

        Log.i("onCreate", "7897897");
        created = true;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(created) {
            Log.i("onPause", "123123");
            paused = true;
            mPause = SystemClock.uptimeMillis();
            getIntent().putExtra("mPause", mPause);
            Log.i("Value of mPause: ", Long.toString(mPause));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mPause = getIntent().getLongExtra("mPause", 0);
        Log.i("Value of mPause: ", Long.toString(mPause));
        if(created) {
            Log.i("onResume", "4564745");

            mPause = now - mPause;
            Log.i("Value of mPause: ", Long.toString(now));
            mStart += mPause;
            paused = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("pauseTime", mPause);
        Log.i("onSaveInstanceStat", "asdasda");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v("onRestoreInstanceState", "Inside of onRestoreInstanceState");
        //startTime = (Calendar) savedInstanceState.getSerializable("starttime");
    }

    public void onChoicesClicked(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.choice1:
                if (checked)
                    break;
            case R.id.choice2:
                if (checked)
                    break;
            case R.id.choice3:
                if (checked)
                    break;
            case R.id.choice4:
                if (checked)
                    break;
        }
    }
}

package com.example.acchan.quizapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this.getApplicationContext();
        // Retrieve the button, change its value and add an event listener
        final Button startButton = (Button) this.findViewById(R.id.startButton);
        final Button statsButton = (Button) this.findViewById(R.id.statisticsButton);
        final TextView temp = (TextView) this.findViewById(R.id.title);
        final TextView stats = (TextView) this.findViewById(R.id.statsView);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//                startButton.setText("Just testing");
//                temp.setText("New Title");
                Intent intent = new Intent(MainActivity.this, QuizScreen.class);
                startActivity(intent);
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DbAdapter statsData = new DbAdapter(ctx);
                int toShow = statsData.getStatsCurrentScore();
                String DisplayStats;
                DisplayStats = "Current Score: " + toShow;
                toShow = statsData.getStatsCurrentTotal();
                DisplayStats += " out of " + toShow + "\n";

                toShow = statsData.getStatsOverallScore();
                DisplayStats += "Total Score: " + toShow;
                toShow = statsData.getStatsOverallTotal();
                DisplayStats += " out of " + toShow;

                stats.setText(DisplayStats);
                statsData.close();
            }
        });
    }
}

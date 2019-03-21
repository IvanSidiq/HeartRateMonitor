package com.heartrate.heartratemonitor;


import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Random;

public class TMT extends AppCompatActivity{

    // Encapsulating the data just to be safe...
    private int collected = 0;
    private int screenWidth = 300;
    private int screenHeight = 300;
    public int angka = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmt);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        TextView collectedView = (TextView) findViewById(R.id.collectedTV);
        collectedView.setText("Collected: " + collected);
        Button btn = (Button) findViewById(R.id.changePlace);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View paramView){
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                // Based on position of our candy:
                Random random = new Random();
                // Understand nextInt(N) will go from 0 -> N-1, also are you trying to control where it can go?
                float candyX = (float) random.nextInt(screenWidth - 50);
                float candyY = (float) random.nextInt(screenHeight - 50);
                // I didn't write it, but you need to check these float values if they   exceed the screen width and the screen length. */
                // Sout to check coordinates
                System.out.println(candyX + " : " + candyY);

                // To change margins:
                Button button = (Button) findViewById(R.id.changePlace);
                button.setX(candyX);
                button.setY(candyY);
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.tmt);
                remoteViews.setTextViewText(R.id.changePlace, String.valueOf(angka));
                angka++;
            }
        });
    }
}

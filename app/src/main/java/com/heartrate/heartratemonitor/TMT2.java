package com.heartrate.heartratemonitor;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TMT2 extends AppCompatActivity{

    private int collected = 0;
    private boolean boolA = false;
    private boolean boolB = false;
    private boolean boolC = false;
    private boolean boolD = false;
    private boolean boolE = false;
    private boolean boolF = false;
    private boolean boolG = false;
    private boolean boolH = false;
    private boolean boolI = false;
    private boolean boolJ = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmt2);

        TextView collectedView = (TextView) findViewById(R.id.collectedTV);
        collectedView.setText("Collected: " + collected);
        Button next =(Button) findViewById(R.id.next);
        next.setVisibility(View.GONE);

        Button a =(Button) findViewById(R.id.a);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                Button a =(Button) findViewById(R.id.a);
                a.setBackgroundResource(R.drawable.colorchange);
                boolA=true;
            }
        });

        Button b =(Button) findViewById(R.id.b);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA==true){
                    Button b =(Button) findViewById(R.id.b);
                    b.setBackgroundResource(R.drawable.colorchange);

                    boolB=true;
                }
            }
        });

        Button c =(Button) findViewById(R.id.c);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB){
                    Button c =(Button) findViewById(R.id.c);
                    c.setBackgroundResource(R.drawable.colorchange);

                    boolC=true;
                }
            }
        });

        Button d =(Button) findViewById(R.id.d);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB&&boolC){
                    Button d =(Button) findViewById(R.id.d);
                    d.setBackgroundResource(R.drawable.colorchange);

                    boolD=true;
                }
            }
        });

        Button e =(Button) findViewById(R.id.e);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB&&boolC&&boolD){
                    Button e =(Button) findViewById(R.id.e);
                    e.setBackgroundResource(R.drawable.colorchange);

                    boolE=true;
                }
            }
        });

        Button f =(Button) findViewById(R.id.f);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB&&boolC&&boolD&&boolE){
                    Button f =(Button) findViewById(R.id.f);
                    f.setBackgroundResource(R.drawable.colorchange);

                    boolF=true;
                }
            }
        });

        Button g =(Button) findViewById(R.id.g);
        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB&&boolC&&boolD&&boolE&&boolF){
                    Button g =(Button) findViewById(R.id.g);
                    g.setBackgroundResource(R.drawable.colorchange);

                    boolG=true;
                }
            }
        });

        Button h =(Button) findViewById(R.id.h);
        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB&&boolC&&boolD&&boolE&&boolF&&boolG){
                    Button h =(Button) findViewById(R.id.h);
                    h.setBackgroundResource(R.drawable.colorchange);

                    boolH=true;
                }
            }
        });

        Button i =(Button) findViewById(R.id.i);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB&&boolC&&boolD&&boolE&&boolF&&boolG&&boolH){
                    Button i =(Button) findViewById(R.id.i);
                    i.setBackgroundResource(R.drawable.colorchange);

                    boolI=true;
                }
            }
        });

        Button j =(Button) findViewById(R.id.j);
        j.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collected += 1;
                TextView collectedView = (TextView) findViewById(R.id.collectedTV);
                collectedView.setText("Collected: " + collected);

                if(boolA&&boolB&&boolC&&boolD&&boolE&&boolF&&boolG&&boolH&&boolI){
                    Button j =(Button) findViewById(R.id.j);
                    j.setBackgroundResource(R.drawable.colorchange);

                    boolJ=true;
                    Button next = (Button)findViewById(R.id.next);
                    next.setVisibility(View.VISIBLE);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmt3();
            }
        });
    }

    private void tmt3(){
        Intent intent = new Intent(this, TMT3.class);
        startActivity(intent);
    }
}

package com.robart.basiccob;

import java.util.Random;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int ans_ok = 0, ans_ko = 0, ans_hlp = 0;

    Random Number;
    int eq_a, eq_op, eq_b, eq_r, usr_val;
    int eq_tp, helped;   // type 0=eq_a , 1=eq_b , 2 = eq_r
    String pre_str="", usr_str="", post_str="";
    // TextView textView = null;
    MediaPlayer snd_yes, snd_no;

    private void PrepareExample() {
        Number = new Random();
        eq_tp = Number.nextInt(3);
        eq_a = Number.nextInt(100);
        eq_r = Number.nextInt(100);
        helped = 0;
        if (eq_a<=eq_r) {
            eq_b = eq_r-eq_a;
            eq_op = 1;
        } else {
            eq_b = eq_a-eq_r;
            eq_op = -1;
        }
        String op = (eq_op < 0) ? " - " : " + ";
        int unval = 0;
        switch (eq_tp) {
            case 0:  // eq_a is unknown
                pre_str = "";
                unval = eq_a;
                post_str = op + Integer.toString(eq_b) + " = " + Integer.toString(eq_r);
                break;
            case 1:  // eq_b is unknown
                pre_str = Integer.toString(eq_a) + op;
                unval = eq_b;
                post_str = " = " + Integer.toString(eq_r);
                break;
            case 2:  // eq_r is unknown
                pre_str = Integer.toString(eq_a) + op + Integer.toString(eq_b) + " = ";
                unval = eq_r;
                post_str = "";
                break;
            default: // error
                Log.d("PrepEx","Bad eq.type");
                PrepareExample();
                break;
        }
        if (unval<10)
            usr_str = ".";
        else if (unval<100)
            usr_str = "..";
        else
            usr_str = "...";
        usr_val = -1;

    }

    private void set_buttons(int drawid)
    {
        int n = usr_str.length();
        int reqres = 0;
        switch (eq_tp) {
            case 0: reqres = eq_a; break;
            case 1: reqres = eq_b; break;
            case 2: reqres = eq_r; break;
        }
        if (n>2) {
            int v = reqres/100;
            String bid = "btn_00" + Integer.toString(v);
            int id = getResources().getIdentifier(bid, "id", getPackageName());
            Log.d("help_100","val="+v+" -> "+bid+" => "+id);
            if (id > 0) {
                Button btn = findViewById(id);
                btn.setBackgroundResource(drawid);
            }
        }
        if (n>1) {
            int v = (reqres/10)%10;
            String bid = "btn_0" + Integer.toString(v);
            int id = getResources().getIdentifier(bid, "id", getPackageName());
            Log.d("help_10","val="+v+" -> "+bid+" => "+id);
            if (id > 0) {
                Button btn = findViewById(id);
                btn.setBackgroundResource(drawid);
            }
        }
        if (n>0) {
            int v = reqres%10;
            String bid = "btn_" + Integer.toString(v);
            int id = getResources().getIdentifier(bid, "id", getPackageName());
            Log.d("help_1","val="+v+" -> "+bid+" => "+id);
            if (id > 0) {
                Button btn = findViewById(id);
                btn.setBackgroundResource(drawid);
            }
        }
    }

    private void update_results() {
        TextView ress = findViewById(R.id.results);
        if (ress!=null) {
            ress.setText("ok: " + ans_ok + "   | X: " + ans_ko + "   | ?: " + ans_hlp);
        }
    }

    public void digit_click(View view) {
        String vid = "";
        if (view.getId() != View.NO_ID) {
            vid = view.getResources().getResourceName(view.getId());
        }
        Log.d("BTN"," 0xx" + view.toString() + " -> " + vid);

        int i = vid.lastIndexOf('/');
        if (i<0) return;
        String snum = vid.substring(i+5);
        int exp = snum.length();
        if (exp<1 || exp>3) return; // only 1-999
        String sval = snum.substring(exp-1);
        int val = Integer.parseInt(sval);

        int l = usr_str.length();

        Log.d("CLICK","i=" + i + " , snum=" + snum + " , exp=" + exp + " , sval=" + sval + " , val=" + val + " , l=" + l);

        if (exp>l) return;

        TextView tv = findViewById(R.id.eqVal);
        if (tv!=null) {
            usr_str = usr_str.substring(0,l-exp) + sval +  usr_str.substring(l-exp+1);
            Log.d("CLCK", "usr_str : "+ usr_str);
            try {
                usr_val = Integer.parseInt(usr_str);
            } catch (Exception e) {
                // do nothing
                usr_val = -1;
            }

            //Log.d("MAIN-BTN"," ... " + tv.toString());
            Log.d("MAIN-USTR",usr_str);
            tv.setText(usr_str);

        }
    }

    public void check_result(View view) {
        String resinfo = "";
        //textView = findViewById(R.id.equation);
        if (usr_str.contains(".")) {
            return;
        }
        int reqres = 0;
        switch (eq_tp) {
            case 0: reqres = eq_a; break;
            case 1: reqres = eq_b; break;
            case 2: reqres = eq_r; break;
        }
        if (reqres==usr_val) {
            resinfo = "Správně";
            snd_yes.start();
            ans_ok++;

            set_buttons(R.drawable.mybutton);

            PrepareExample();

            TextView textView = findViewById(R.id.eqPre);
            if (textView!=null)
                textView.setText(pre_str);
            textView = findViewById(R.id.eqVal);
            if (textView!=null)
                textView.setText(usr_str);
            textView = findViewById(R.id.eqPost);
            if (textView!=null)
                textView.setText(post_str);

        } else {
            resinfo = "Chyba";
            snd_no.start();
            ans_ko++;
        }
        Snackbar.make(view, resinfo, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        update_results();
    }

    public void show_result(View view) {
        if (helped!=0) return;
        set_buttons(R.drawable.helpbutton);
        helped = 1;
        ans_hlp++;
        update_results();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrepareExample();

        TextView textView = findViewById(R.id.eqPre);
        if (textView!=null)
            textView.setText(pre_str);
        textView = findViewById(R.id.eqVal);
        if (textView!=null)
            textView.setText(usr_str);
        textView = findViewById(R.id.eqPost);
        if (textView!=null)
            textView.setText(post_str);

        snd_yes = MediaPlayer.create(getApplicationContext(), R.raw.clapping);
        snd_no = MediaPlayer.create(getApplicationContext(), R.raw.moo);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        FloatingActionButton fab = findViewById(R.id.fab_next);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check_result(v);
            }
        });
        fab = findViewById(R.id.fab_help);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show_result(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

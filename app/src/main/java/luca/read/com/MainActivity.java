package luca.read.com;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView tvContent;

    private Button btnTehllim;
    private Button btnHatikun;
    private Button btnParashat;
    private Button btnAbout;

    private Button btnPlay;
    private Button btnPause;

    private Button btnSpeedUp;
    private Button btnSpeedDown;

    private Button btnFontUp;
    private Button btnFontDown;

    private EditText edtSpeed;
    private EditText edtFontSize;

    private ScrollView svContent;

    private static int SCROLL_INTERVAL = 20;
    private static int nSpeed = 10;

    private static int nFontSize = 18;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            svContent.smoothScrollTo(svContent.getScrollY(), svContent.getScrollY() + nSpeed);

            int diff = (tvContent.getBottom() - (svContent.getHeight() + svContent.getScrollY()));

            // if diff is zero, then the bottom has been reached
            if (diff <= 0) {
                handler.removeMessages(0);
                Toast.makeText(MainActivity.this, "End", Toast.LENGTH_SHORT).show();

                return;
            }

            handler.postDelayed(this, SCROLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvContent = (TextView) findViewById(R.id.tv_content);
        btnTehllim = (Button) findViewById(R.id.btn_tehllim);
        btnHatikun = (Button) findViewById(R.id.btn_hatikkun);
        btnParashat = (Button) findViewById(R.id.btn_parshat);
        btnAbout = (Button) findViewById(R.id.btn_about);

        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPause = (Button) findViewById(R.id.btn_pause);

        btnSpeedUp = (Button) findViewById(R.id.btn_speed_up);
        btnSpeedDown = (Button) findViewById(R.id.btn_speed_down);

        btnFontUp = (Button) findViewById(R.id.btn_font_up);
        btnFontDown = (Button) findViewById(R.id.btn_font_down);

        edtSpeed = (EditText) findViewById(R.id.edt_speed);
        edtFontSize = (EditText) findViewById(R.id.edt_font_size);

        svContent = (ScrollView) findViewById(R.id.sv_content);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                nFontSize);

        btnTehllim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                svContent.scrollTo(0, 0);
                readText("Tehillim.txt");
            }
        });

        btnHatikun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                svContent.scrollTo(0, 0);
                readText("Hatikun.txt");
            }
        });

        btnParashat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                svContent.scrollTo(0, 0);
                readText("Parashat.txt");
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                svContent.scrollTo(0, 0);
                readText("About.txt");
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.post(runnable);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
            }
        });

        btnFontUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nFontSize >= 100) {
                    return;
                }

                nFontSize++;
                edtFontSize.setText(String.valueOf(nFontSize));
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        nFontSize);
            }
        });

        btnFontDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nFontSize <= 10) {
                    return;
                }

                nFontSize--;

                edtFontSize.setText(String.valueOf(nFontSize));
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        nFontSize);
            }
        });

        btnSpeedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nSpeed >= 50) {
                    return;
                }
                nSpeed++;

                edtSpeed.setText(String.valueOf(nSpeed));
            }
        });

        btnSpeedDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nSpeed <= 5) {
                    return;
                }
                nSpeed--;

                edtSpeed.setText(String.valueOf(nSpeed));
            }
        });

        readText("Tehillim.txt");
    }

    private void readText(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(filename)));

            // do reading, usually loop until end of file reading
            String text = "";
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                text = text + mLine + "\n";
            }

            tvContent.setText(text);
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }
}

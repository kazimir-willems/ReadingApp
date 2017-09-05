package luca.read.com;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
    private Button btnPeek;
    private Button btnAbout;

    private Button btnPlay;
    private Button btnPause;

    private Button btnSpeedUp;
    private Button btnSpeedDown;

    private Button btnFontUp;
    private Button btnFontDown;

    private TextView edtSpeed;
    private TextView edtFontSize;
    private TextView tvStatus;

    private HorizontalScrollView svContent;

    private long lStartTime = 0;
    private long lBestTime = 0;
    private int nType = 0;

    private static int SCROLL_INTERVAL = 20;
    private static int nSpeed = 10;
    private static int nFontSize = 18;

    private static boolean bScrollFlag = true; //false; left to right, true: right to left
    private ProgressDialog progressDialog;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int diff = 0;

            if(!bScrollFlag) {
                svContent.smoothScrollTo(svContent.getScrollX() + nSpeed, 0);

                diff = (tvContent.getRight() - (svContent.getWidth() + svContent.getScrollX()));
            } else {
                svContent.smoothScrollTo(svContent.getScrollX() - nSpeed, 0);

                diff = svContent.getScrollX();
            }

            // if diff is zero, then the bottom has been reached
            if (diff <= 0) {
                long lElapsedTime = System.currentTimeMillis() - lStartTime;

                if (lBestTime == 0 || lElapsedTime < lBestTime) {
                    lBestTime = lElapsedTime;
                    saveBestTime(nType, lBestTime);
                }

                updateStatus(nSpeed, (int)(lElapsedTime / 1000), (int)(lBestTime / 1000));
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
        btnPeek = (Button) findViewById(R.id.btn_peek);
        btnAbout = (Button) findViewById(R.id.btn_about);

        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPause = (Button) findViewById(R.id.btn_pause);

        btnSpeedUp = (Button) findViewById(R.id.btn_speed_up);
        btnSpeedDown = (Button) findViewById(R.id.btn_speed_down);

        btnFontUp = (Button) findViewById(R.id.btn_font_up);
        btnFontDown = (Button) findViewById(R.id.btn_font_down);

        edtSpeed = (TextView) findViewById(R.id.edt_speed);
        edtFontSize = (TextView) findViewById(R.id.edt_font_size);
        tvStatus = (TextView) findViewById(R.id.tv_status);

        svContent = (HorizontalScrollView) findViewById(R.id.sv_content);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                nFontSize);

        progressDialog = new ProgressDialog(MainActivity.this);

        btnTehllim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                nType = 0;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("Tehillim1.txt");
                moveScroll();
            }
        });

        btnHatikun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                nType = 1;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("Hatikun.txt");
                moveScroll();
            }
        });

        btnParashat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                nType = 2;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("Parashat.txt");
                moveScroll();
            }
        });

        btnPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                nType = 2;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("PeekShira.txt");
                moveScroll();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = false;
                svContent.scrollTo(0, 0);
                nType = 3;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("About.txt");
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lStartTime = System.currentTimeMillis();
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
                if(nFontSize >= 60) {
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

        lBestTime = getBestTime(nType);
        updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
    }

    private void moveScroll() {
        Handler scrollHandler = new Handler();
        Runnable scrollRunnable = new Runnable() {
            @Override
            public void run() {
                svContent.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        };
        scrollHandler.postDelayed(scrollRunnable, 100L);

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
                text = text + mLine + " ";
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

    private long getBestTime(int type) {
        long lTime = 0;
        SharedPreferences shared = getSharedPreferences("time", 0);
        switch (type) {
            case 0:
                if (shared.contains("Tehillim"))
                    lTime = shared.getLong("Tehillim", lTime);
                break;
            case 1:
                if (shared.contains("Hatikun"))
                    lTime = shared.getLong("Hatikun", lTime);
                break;
            case 2:
                if (shared.contains("Parashat"))
                    lTime = shared.getLong("Parashat", lTime);
                break;
            case 3:
                if (shared.contains("About"))
                    lTime = shared.getLong("About", lTime);
                break;
            default:
                break;
        }


        return lTime;
    }

    private void saveBestTime(int type, long lTime) {
        SharedPreferences shared = getSharedPreferences("time", 0);
        SharedPreferences.Editor editor = shared.edit();

        switch (type) {
            case 0:
                editor.remove("Tehillim");
                editor.putLong("Tehillim", lTime);
                break;
            case 1:
                editor.remove("Hatikun");
                editor.putLong("Hatikun", lTime);
                break;
            case 2:
                editor.remove("Parashat");
                editor.putLong("Parashat", lTime);
                break;
            case 3:
                editor.remove("About");
                editor.putLong("About", lTime);
                break;
            default:
                break;
        }

        editor.commit();
    }

    private void updateStatus(int speed, int elapsed, int best) {
        StringBuffer sbStatus = new StringBuffer();
        String elapsedTime = (elapsed / 60) + "m " + (elapsed % 60) + "s";
        String bestTime = (best / 60) + "m " + (best % 60) + "s";
        sbStatus.append("Speed: " + speed + "\n");
        sbStatus.append("Time: " + elapsedTime + "\n");
        sbStatus.append("Best: " + bestTime + "\n");
        tvStatus.setText(sbStatus.toString());
    }

    public class ShowContentTask extends AsyncTask<String, Void, Boolean> {

//        ProgressDialog progressDialog;
        MainActivity parent;

        public ShowContentTask(MainActivity parent) {
            this.parent = parent;
        }

        @Override
        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(parent);
//            progressDialog.setMessage("Loading...");
//            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String filename = params[0];
            readText(filename);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            moveScroll();
        }
    }
}

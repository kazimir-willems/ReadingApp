package luca.read.com;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView tvHorizontalContent;
    private TextView tvVerticalContent;

    private Button btnTehllim;
    private Button btnHatikun;
    private Button btnParashat;
    private Button btnPeek;
    private Button btnAbout;

    private Button btnMode;
    private Button btnPlay;
    private Button btnPause;

    private Button btnSpeedUp;
    private Button btnSpeedDown;

    private Button btnFontUp;
    private Button btnFontDown;

    private TextView edtSpeed;
    private TextView edtFontSize;
    private TextView tvMode;
    private TextView tvStatus;

    private HorizontalScrollView svHorizontalContent;
    private ScrollView svVerticalContent;

    private long lStartTime = 0;
    private long lBestTime = 0;
    private int nType = 0;

    private static int SCROLL_INTERVAL = 20;
    private static int nSpeed = 10;
    private static int nFontSize = 18;

    private final static int TEHILLIM_PARTS = 15;
    private static int CURRENT_PART = 1;

    private static boolean bTehillim = false;
    private static boolean bScrollFlag = true; //false; left to right, true: right to left
    private static boolean bMode = false; //false; Horizontal, true: Vertical
    private ProgressDialog progressDialog;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int diff = 0;

            if(!bScrollFlag) {
                svHorizontalContent.smoothScrollTo(svHorizontalContent.getScrollX() + nSpeed, 0);

                diff = (tvHorizontalContent.getRight() - (svHorizontalContent.getWidth() + svHorizontalContent.getScrollX()));
            } else {
                svHorizontalContent.smoothScrollTo(svHorizontalContent.getScrollX() - nSpeed, 0);

                diff = svHorizontalContent.getScrollX();
            }

            // if diff is zero, then the bottom has been reached
            if(!bTehillim) {
                if (diff <= 0) {
                    long lElapsedTime = System.currentTimeMillis() - lStartTime;

                    if (lBestTime == 0 || lElapsedTime < lBestTime) {
                        lBestTime = lElapsedTime;
                        saveBestTime(nType, lBestTime);
                    }

                    updateStatus(nSpeed, (int) (lElapsedTime / 1000), (int) (lBestTime / 1000));
                    handler.removeMessages(0);
                    Toast.makeText(MainActivity.this, "End", Toast.LENGTH_SHORT).show();

                    btnMode.setEnabled(true);
                    btnPlay.setEnabled(true);
                    return;
                }
            } else {
                if(CURRENT_PART < TEHILLIM_PARTS) {
                    if (diff <= tvHorizontalContent.getWidth() / 20) {
                        CURRENT_PART++;

                        String filename = "Tehillim" + CURRENT_PART + ".txt";
                        readText(filename);
                    }
                } else {
                    if (diff <= 0) {
                        long lElapsedTime = System.currentTimeMillis() - lStartTime;

                        if (lBestTime == 0 || lElapsedTime < lBestTime) {
                            lBestTime = lElapsedTime;
                            saveBestTime(nType, lBestTime);
                        }

                        updateStatus(nSpeed, (int) (lElapsedTime / 1000), (int) (lBestTime / 1000));
                        handler.removeMessages(0);
                        Toast.makeText(MainActivity.this, "End", Toast.LENGTH_SHORT).show();

                        btnMode.setEnabled(true);
                        btnPlay.setEnabled(true);
                        return;
                    }
                }
            }

            handler.postDelayed(this, SCROLL_INTERVAL);
        }
    };

    Handler verticalHandler = new Handler();
    Runnable verticalRunnable = new Runnable() {
        @Override
        public void run() {
            svVerticalContent.smoothScrollTo(svVerticalContent.getScrollY(), svVerticalContent.getScrollY() + nSpeed);

            int diff = (tvVerticalContent.getBottom() - (svVerticalContent.getHeight() + svVerticalContent.getScrollY()));

            // if diff is zero, then the bottom has been reached
            if(!bTehillim) {
                if (diff <= 0) {
                    long lElapsedTime = System.currentTimeMillis() - lStartTime;

                    if (lBestTime == 0 || lElapsedTime < lBestTime) {
                        lBestTime = lElapsedTime;
                        saveBestTime(nType, lBestTime);
                    }

                    updateStatus(nSpeed, (int) (lElapsedTime / 1000), (int) (lBestTime / 1000));
                    handler.removeMessages(0);
                    Toast.makeText(MainActivity.this, "End", Toast.LENGTH_SHORT).show();

                    btnMode.setEnabled(true);
                    btnPlay.setEnabled(true);
                    return;
                }
            } else {
                if(CURRENT_PART < TEHILLIM_PARTS) {
                    if (diff <= tvVerticalContent.getHeight() / 20) {
                        CURRENT_PART++;

                        String filename = "Tehillim" + CURRENT_PART + ".txt";
                        readText(filename);
                    }
                } else {
                    if (diff <= 0) {
                        long lElapsedTime = System.currentTimeMillis() - lStartTime;

                        if (lBestTime == 0 || lElapsedTime < lBestTime) {
                            lBestTime = lElapsedTime;
                            saveBestTime(nType, lBestTime);
                        }

                        updateStatus(nSpeed, (int) (lElapsedTime / 1000), (int) (lBestTime / 1000));
                        handler.removeMessages(0);
                        Toast.makeText(MainActivity.this, "End", Toast.LENGTH_SHORT).show();

                        btnMode.setEnabled(true);
                        btnPlay.setEnabled(true);

                        return;
                    }
                }
            }

            handler.postDelayed(this, SCROLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences shared = getSharedPreferences("time", 0);
        if (shared.contains("speed"))
            nSpeed = shared.getInt("speed", 10);
        if (shared.contains("font"))
            nFontSize = shared.getInt("font", 18);
        if (shared.contains("mode"))
            bMode = shared.getBoolean("mode", false);

        tvHorizontalContent = (TextView) findViewById(R.id.tv_horizontal_content);
        tvVerticalContent = (TextView) findViewById(R.id.tv_vertical_content);

        btnTehllim = (Button) findViewById(R.id.btn_tehllim);
        btnHatikun = (Button) findViewById(R.id.btn_hatikkun);
        btnParashat = (Button) findViewById(R.id.btn_parshat);
        btnPeek = (Button) findViewById(R.id.btn_peek);
        btnAbout = (Button) findViewById(R.id.btn_about);

        btnMode = (Button) findViewById(R.id.btn_scroll_mode);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPause = (Button) findViewById(R.id.btn_pause);

        btnSpeedUp = (Button) findViewById(R.id.btn_speed_up);
        btnSpeedDown = (Button) findViewById(R.id.btn_speed_down);

        btnFontUp = (Button) findViewById(R.id.btn_font_up);
        btnFontDown = (Button) findViewById(R.id.btn_font_down);

        edtSpeed = (TextView) findViewById(R.id.edt_speed);
        edtFontSize = (TextView) findViewById(R.id.edt_font_size);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvMode = (TextView) findViewById(R.id.tv_scroll_mode);

        svHorizontalContent = (HorizontalScrollView) findViewById(R.id.sv_horizontal_content);
        svVerticalContent = (ScrollView) findViewById(R.id.sv_vertical_content);
        tvHorizontalContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                nFontSize);
        tvVerticalContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, nFontSize);

        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bMode = !bMode;
                if(bMode) {
                    btnMode.setText("Horizontal");
                    tvMode.setText("Mode:\nVertical");
                    svVerticalContent.setVisibility(View.VISIBLE);
                    svHorizontalContent.setVisibility(View.GONE);

                    SharedPreferences shared = getSharedPreferences("time", 0);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.remove("mode");
                    editor.putBoolean("mode", bMode);

                    editor.commit();
                } else {
                    btnMode.setText("Vertical");
                    tvMode.setText("Mode:\nHorizontal");
                    svVerticalContent.setVisibility(View.GONE);
                    svHorizontalContent.setVisibility(View.VISIBLE);

                    SharedPreferences shared = getSharedPreferences("time", 0);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.remove("mode");
                    editor.putBoolean("mode", bMode);

                    editor.commit();
                }
            }
        });

        btnTehllim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                bTehillim = true;
                CURRENT_PART = 1;
                nType = 0;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("Tehillim1.txt");
            }
        });

        btnHatikun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                bTehillim = false;
                nType = 1;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("Hatikun.txt");
            }
        });

        btnParashat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                bTehillim = false;
                nType = 2;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("Parashat.txt");
            }
        });

        btnPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = true;
                bTehillim = false;
                nType = 2;
                lBestTime = getBestTime(nType);
                updateStatus(nSpeed, 0, (int)(lBestTime / 1000));
                readText("PeekShira.txt");
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                bScrollFlag = false;
                bTehillim = false;
                svHorizontalContent.scrollTo(0, 0);
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
                if(!bMode) {
                    handler.post(runnable);
                    btnMode.setEnabled(false);
                } else {
                    verticalHandler.post(verticalRunnable);
                    btnMode.setEnabled(false);
                }

                btnPlay.setEnabled(false);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(0);
                verticalHandler.removeMessages(0);

                btnMode.setEnabled(true);
                btnPlay.setEnabled(true);
            }
        });

        btnFontUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nFontSize >= 60) {
                    return;
                }

                nFontSize++;

                SharedPreferences shared = getSharedPreferences("time", 0);
                SharedPreferences.Editor editor = shared.edit();
                editor.remove("font");
                editor.putInt("font", nFontSize);

                editor.commit();
                edtFontSize.setText(String.valueOf(nFontSize));
                tvHorizontalContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        nFontSize);
                tvVerticalContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
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
                SharedPreferences shared = getSharedPreferences("time", 0);
                SharedPreferences.Editor editor = shared.edit();
                editor.remove("font");
                editor.putInt("font", nFontSize);

                editor.commit();
                edtFontSize.setText(String.valueOf(nFontSize));
                tvHorizontalContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                        nFontSize);
                tvVerticalContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,
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
                SharedPreferences shared = getSharedPreferences("time", 0);
                SharedPreferences.Editor editor = shared.edit();
                editor.remove("speed");
                editor.putInt("speed", nSpeed);

                editor.commit();
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
                SharedPreferences shared = getSharedPreferences("time", 0);
                SharedPreferences.Editor editor = shared.edit();
                editor.remove("speed");
                editor.putInt("speed", nSpeed);

                editor.commit();
                edtSpeed.setText(String.valueOf(nSpeed));
            }
        });

        if(bMode) {
            btnMode.setText("Horizontal");
            tvMode.setText("Mode:\nVertical");
            svVerticalContent.setVisibility(View.VISIBLE);
            svHorizontalContent.setVisibility(View.GONE);
        } else {
            btnMode.setText("Vertical");
            tvMode.setText("Mode:\nHorizontal");
            svVerticalContent.setVisibility(View.GONE);
            svHorizontalContent.setVisibility(View.VISIBLE);
        }

        edtFontSize.setText(String.valueOf(nFontSize));
        edtSpeed.setText(String.valueOf(nSpeed));

        lBestTime = getBestTime(nType);
        updateStatus(nSpeed, 0, (int)(lBestTime / 1000));

        btnTehllim.performClick();
    }

    private void moveScroll() {
        Handler scrollHandler = new Handler();
        Runnable scrollRunnable = new Runnable() {
            @Override
            public void run() {
//                if(bMode) {
                    svVerticalContent.scrollTo(0, 0);
//                } else {
                    if (bScrollFlag) {
                        svHorizontalContent.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    } else {
                        svHorizontalContent.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    }
//                }
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
                text = text + mLine + "\n";
            }

            tvHorizontalContent.setText(text);
            tvVerticalContent.setText(text);
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

        moveScroll();
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

}

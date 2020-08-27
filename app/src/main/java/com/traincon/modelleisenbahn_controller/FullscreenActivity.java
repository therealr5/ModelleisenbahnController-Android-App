package com.traincon.modelleisenbahn_controller;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class FullscreenActivity extends AppCompatActivity {
    final public Button[] menuButtons = new Button[11];

    private boolean isMenuOpen = false;
    private boolean isPresetMenuOpen = false;
    private boolean isSwitchMenuOpen = false;
    private boolean isSectionMenuOpen = false;
    private BoardManager boardManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_main);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        @SuppressWarnings("deprecation") String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        Intent intent = getIntent();
        String devId = intent.getStringExtra("deviceId");
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);
        TextView localDevIdTextView = findViewById(R.id.localDevIdTextView);
        TextView localIpTextView = findViewById(R.id.localIpTextView);
        TextView runningDevIdTextView = findViewById(R.id.runningDevIdTextView);
        TextView boardIpTextView = findViewById(R.id.boardIpTextView);
        localDevIdTextView.setText("Gerätenummer: "+devId);
        localIpTextView.setText("Eigene Ip: "+ ipAddress);
        runningDevIdTextView.setText("Aktives Gerät: "+"-");
        boardIpTextView.setText("Ip der Platine: "+host);

        createTabLayout();
        int[] menuButtonIdArray = new int[]{R.id.mainMenuButton, R.id.presetsMenuButton, R.id.switchMenuButton, R.id.switchSetStandardActionbutton, R.id.switchSetToCenterActionButton, R.id.switchCalibrateActionButton, R.id.sectionMenuButton, R.id.sectionSetStandardActionbutton, R.id.sectionsAllOffActionbutton, R.id.reconnectActionButton, R.id.lightActionButton};
        createMenuButtons(menuButtonIdArray);

        boardManager = new BoardManager(devId, host, port);
        boardManager.connect();
    }

    private void createTabLayout() {
        //Tablayout einrichten
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab firsttab = tabLayout.newTab();
        firsttab.setText("Fahrtsteuerung");
        tabLayout.addTab(firsttab);
        TabLayout.Tab secondtab = tabLayout.newTab();
        secondtab.setText("Interaktiver gleisplan");
        tabLayout.addTab(secondtab);
        final Fragment[] fragment = {null};

        //Am Anfang das fragment_controller anzeigen
        fragment[0] = new ControllerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.simpleFrameLayout, Objects.requireNonNull(fragment[0]));
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        //Bei änderungen im Tablayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragment[0] = new ControllerFragment();
                        break;
                    case 1:
                        fragment[0] = new ScreenFragment(boardManager, getScreenRatio());
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.simpleFrameLayout, Objects.requireNonNull(fragment[0]));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void createMenuButtons(final int[] idArray) {
        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i] = findViewById(idArray[i]);
            setMenuButtonSize(menuButtons[i], getScreenRatio());
            if (i > 0) {
                menuButtons[i].setVisibility(View.INVISIBLE);
            }
        }

        //Hauptmenu
        menuButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMenuOpen) {
                    menuButtons[1].setVisibility(View.VISIBLE);
                    menuButtons[9].setVisibility(View.VISIBLE);
                    menuButtons[10].setVisibility(View.VISIBLE);
                    isMenuOpen = true;
                } else {
                    //Menu Schließen
                    for (int i = 1; i < idArray.length; i++) {
                        menuButtons[i].setVisibility(View.INVISIBLE);
                    }

                    isMenuOpen = false;
                }
            }
        });

        //Presets
        menuButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPresetMenuOpen) {
                    menuButtons[0].setVisibility(View.INVISIBLE);
                    menuButtons[2].setVisibility(View.VISIBLE);
                    menuButtons[6].setVisibility(View.VISIBLE);
                    menuButtons[9].setVisibility(View.INVISIBLE);
                    menuButtons[10].setVisibility(View.INVISIBLE);
                    isPresetMenuOpen = true;
                } else {
                    menuButtons[0].setVisibility(View.VISIBLE);
                    menuButtons[2].setVisibility(View.INVISIBLE);
                    menuButtons[6].setVisibility(View.INVISIBLE);
                    menuButtons[9].setVisibility(View.VISIBLE);
                    menuButtons[10].setVisibility(View.VISIBLE);
                    isPresetMenuOpen = false;
                }
            }
        });

        //Weichen
        menuButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSwitchMenuOpen) {
                    menuButtons[1].setVisibility(View.INVISIBLE);
                    menuButtons[3].setVisibility(View.VISIBLE);
                    menuButtons[4].setVisibility(View.VISIBLE);
                    menuButtons[5].setVisibility(View.VISIBLE);
                    menuButtons[6].setVisibility(View.INVISIBLE);
                    isSwitchMenuOpen = true;
                } else {
                    menuButtons[1].setVisibility(View.VISIBLE);
                    menuButtons[3].setVisibility(View.INVISIBLE);
                    menuButtons[4].setVisibility(View.INVISIBLE);
                    menuButtons[5].setVisibility(View.INVISIBLE);
                    menuButtons[6].setVisibility(View.VISIBLE);
                    isSwitchMenuOpen = false;
                }

            }
        });

        //Weichen 3 Runden
        menuButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menu Schließen
                for (int i = 1; i < idArray.length; i++) {
                    menuButtons[i].setVisibility(View.INVISIBLE);
                }
                menuButtons[0].setVisibility(View.VISIBLE);
                boardManager.switchPreset_3r();
                isMenuOpen = false;
            }
        });

        //Weichen Alle auf Mitte
        menuButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menu Schließen
                for (int i = 1; i < idArray.length; i++) {
                    menuButtons[i].setVisibility(View.INVISIBLE);
                }
                menuButtons[0].setVisibility(View.VISIBLE);
                boardManager.switchSetToCenter();
                isMenuOpen = false;
            }
        });

        //Weichen Nachstellen
        menuButtons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menu Schließen
                for (int i = 1; i < idArray.length; i++) {
                    menuButtons[i].setVisibility(View.INVISIBLE);
                }
                menuButtons[0].setVisibility(View.VISIBLE);
                boardManager.switchCalibrate();
                isMenuOpen = false;
            }
        });

        //Gleisabschnitte
        menuButtons[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSectionMenuOpen) {
                    menuButtons[1].setVisibility(View.INVISIBLE);
                    menuButtons[2].setVisibility(View.INVISIBLE);
                    menuButtons[7].setVisibility(View.VISIBLE);
                    menuButtons[8].setVisibility(View.VISIBLE);
                    isSectionMenuOpen = true;
                } else {
                    menuButtons[1].setVisibility(View.VISIBLE);
                    menuButtons[2].setVisibility(View.VISIBLE);
                    menuButtons[7].setVisibility(View.INVISIBLE);
                    menuButtons[8].setVisibility(View.INVISIBLE);
                    isSectionMenuOpen = false;
                }

            }
        });

        // Gleisabschnitte 3 Runden
        menuButtons[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menu Schließen
                for (int i = 1; i < idArray.length; i++) {
                    menuButtons[i].setVisibility(View.INVISIBLE);
                }
                menuButtons[0].setVisibility(View.VISIBLE);
                boardManager.sectionPreset_3r();
                isMenuOpen = false;
            }
        });

        //Gleisabschnitte Alle ausschalten
        menuButtons[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menu Schließen
                for (int i = 1; i < idArray.length; i++) {
                    menuButtons[i].setVisibility(View.INVISIBLE);
                }
                menuButtons[0].setVisibility(View.VISIBLE);
                boardManager.sectionsAllOff();
                isMenuOpen = false;
            }
        });

        // Neu verbinden
        menuButtons[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menu Schließen
                for (int i = 1; i < idArray.length; i++) {
                    menuButtons[i].setVisibility(View.INVISIBLE);
                }
                boardManager.connect();
                isMenuOpen = false;
            }
        });

        //Licht
        menuButtons[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Menu Schließen
                for (int i = 1; i < idArray.length; i++) {
                    menuButtons[i].setVisibility(View.INVISIBLE);
                }
                boardManager.setLight();
                isMenuOpen = false;
            }
        });

    }

    private void setMenuButtonSize(Button menuButton, String screenRatio) {
        if ("16:9".equals(screenRatio)) {
            menuButton.getLayoutParams().height = 70;
        } else {
            menuButton.getLayoutParams().height = 90;
        }
        menuButton.getLayoutParams().width = 153;

    }

    @NonNull
    private String getScreenRatio() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        for (int i = 1000; i > 0; i--) {
            if (width % i == 0 && height % i == 0) {
                width = width / i;
                height = height / i;
            }
        }
        return width +":"+height;
    }
}
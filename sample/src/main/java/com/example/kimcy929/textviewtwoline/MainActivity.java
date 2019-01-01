package com.example.kimcy929.textviewtwoline;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.RelativeLayout;

import com.kimcy929.localeutils.LocaleUtils;
import com.kimcy929.textviewtwoline.TextViewTwoLine;

public class MainActivity extends AppCompatActivity {

    public MainActivity() {
        //See MyApp class to config the language
        LocaleUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_line);

        showBackArrow();

        final TextViewTwoLine textViewTwoLine = findViewById(R.id.textViewTwoLine);

        Button btnChangeText = findViewById(R.id.btnChangeText);

        btnChangeText.setOnClickListener(v -> {
            textViewTwoLine.setTextTitle(getString(R.string.fix_for_nexus));
            textViewTwoLine.setTextDescription(getString(R.string.fix_for_nexus_description));

            textViewTwoLine.setLeftDrawableCompat(R.drawable.ic_smartphone_black_24dp);

            //textViewTwoLine.setText(null, "");
        });

        RelativeLayout btnHideVideo = findViewById(R.id.btnHideVideo);
        RelativeLayout btnTranslate = findViewById(R.id.btnTranslate);

        btnHideVideo.setOnClickListener(v -> {

        });

        btnTranslate.setOnClickListener(v -> {

        });

        TextViewTwoLine btnSystemSettings = findViewById(R.id.btnSystemSettings);
        btnSystemSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, FakeSettingsActivity.class));
        });
    }

    private void showBackArrow() {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
    }
}

package com.example.kimcy929.textviewtwoline;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class FakeSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fake_settings);

        showBackArrow();

        LinearLayout systemSettingsLayout = findViewById(R.id.systemSettingsLayout);

        for (int i = 0; i < systemSettingsLayout.getChildCount(); i++) {
            View view = systemSettingsLayout.getChildAt(i);
            view.setOnClickListener(onClickListener);
        }
    }

    private View.OnClickListener onClickListener = v -> {

    };

    @SuppressWarnings("ConstantConditions")
    private void showBackArrow() {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}

package com.example.emergencyalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox vibrateCheckBox;
    private CheckBox flashlightCheckBox;
    private CheckBox notificationCheckBox;
    private String preferenceFile = "com.example.emergencyAlarm.";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        vibrateCheckBox = (CheckBox) findViewById(R.id.vibrate_checkbox);
        flashlightCheckBox = (CheckBox) findViewById(R.id.flashlight_checkbox);
        notificationCheckBox = (CheckBox) findViewById(R.id.notification_checkbox);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(preferenceFile+getString(R.string.app_name), 0); // 0 - for private mode

        vibrateCheckBox.setChecked(pref.getBoolean(StaticKeys.VIBRATE_KEY,false));
        flashlightCheckBox.setChecked(pref.getBoolean(StaticKeys.FLASH_SCREEN_KEY,false));
        notificationCheckBox.setChecked(pref.getBoolean(StaticKeys.FLASH_LIGHT_KEY,false));

    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(preferenceFile+getString(R.string.app_name), 0);  // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.vibrate_checkbox:
                if (checked)
                    editor.putBoolean(StaticKeys.VIBRATE_KEY, true);
            else
                    editor.putBoolean(StaticKeys.VIBRATE_KEY, false);
                break;
            case R.id.flashlight_checkbox:
                if (checked)
                    editor.putBoolean(StaticKeys.FLASH_SCREEN_KEY, true);
            else
                    editor.putBoolean(StaticKeys.FLASH_SCREEN_KEY, false);
                break;
            case R.id.notification_checkbox:
                if (checked)
                    editor.putBoolean(StaticKeys.FLASH_LIGHT_KEY, true);
            else
                    editor.putBoolean(StaticKeys.FLASH_LIGHT_KEY, false);
                break;
        }

        editor.commit();

    }
}

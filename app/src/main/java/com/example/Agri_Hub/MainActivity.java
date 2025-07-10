package com.example.Agri_Hub;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_USER_ROLE = "userRole";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Farmer Login Click Event
        Button farmerLogin = findViewById(R.id.farmerLogin);
        farmerLogin.setOnClickListener(v -> {
            saveUserRole("farmer"); // Save role before login
            Intent intent = new Intent(MainActivity.this, FarmerLoginActivity.class);
            startActivity(intent);
        });

        // User Login Click Event
        TextView userLogin = findViewById(R.id.userLogin);
        userLogin.setOnClickListener(v -> {
            saveUserRole("buyer"); // Save role before login
            Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
            startActivity(intent);
        });

        Button languageBtn =findViewById(R.id.languageBtn);
        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage();
            }
        });

    }

    private void changeLanguage() {
        final String language[] ={"French", "हिंदी", "ગુજરાતી", "ଓଡ଼ିଆ", "తెలుగు", "العربية", "বাংলা","English"};
        AlertDialog.Builder mBuilder =new AlertDialog.Builder(this);
        mBuilder.setTitle("Select Language");
        mBuilder.setSingleChoiceItems(language, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    setLocale("fr");
                    recreate();
                }else if(which == 1){
                    setLocale("hi");
                    recreate();
                }else if(which == 2){
                    setLocale("gu");
                    recreate();
                }else if(which == 3){
                    setLocale("or");
                    recreate();
                }else if(which == 4){
                    setLocale("te");
                    recreate();
                }else if(which == 5){
                    setLocale("ar");
                    recreate();
                }else if(which == 6){
                    setLocale("bn");
                    recreate();
                }else if(which == 7){
                    setLocale("");
                    recreate();
                }
            }
        });
        mBuilder.create();
        mBuilder.show();
    }

    private void setLocale(String language) {
        Locale locale =new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration =new Configuration();
        configuration.locale =locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        //save the current language
        SharedPreferences preferences =getSharedPreferences("Settings",MODE_PRIVATE);
        SharedPreferences.Editor langEdit =preferences.edit();
        langEdit.putString("app_lang",language);
        langEdit.apply();
    }

    // Save user role to SharedPreferences
    private void saveUserRole(String role) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    private void loadLocale(){
        SharedPreferences preferences =getSharedPreferences("Settings",MODE_PRIVATE);
        String language =preferences.getString("app_lang","");
        setLocale(language);
    }
}

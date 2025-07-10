package com.example.Agri_Hub;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class ForgotPasswordForUserActivity extends LanguageBaseActivity {

    private ProgressBar pb3;
    private EditText forEmail;
    private final static String TAG = "ForgotPasswordForUserActivity";
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_for_farmer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the Toolbar as the ActionBar

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Forget Password");
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        forEmail = findViewById(R.id.edittext_reset_email);
        pb3 = findViewById(R.id.pb);
        Button resetBtn = findViewById(R.id.button_reset_password);


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forEmail.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(ForgotPasswordForUserActivity.this,"Please enter your registered email id",Toast.LENGTH_LONG).show();
                    forEmail.setError("Email is required!");
                    forEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordForUserActivity.this, "Please re-enter your valid email", Toast.LENGTH_LONG).show();
                    forEmail.setError("Valid email is required");
                    forEmail.requestFocus();
                }
                else {
                    pb3.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });

    }



    private void resetPassword(String email) {
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordForUserActivity.this, "If the email is registered, then you'll receive a password reset link in your mail box.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPasswordForUserActivity.this, UserLoginActivity.class);
                    //check stack to prevent coming back to the ForgotPasswordActivity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    try
                    {
                        throw Objects.requireNonNull(task.getException());
                    }  catch (FirebaseAuthInvalidUserException e) {
                        forEmail.setError("Invalid user or user does not exist☹️!Please register again.");
                        forEmail.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e) {
                        // Handle case where the email format is invalid
                        forEmail.setError("Invalid email format. Please enter a valid email.");
                        forEmail.requestFocus();
                    }
                    catch (FirebaseNetworkException e) {
                        // Handle network-related issues
                        Toast.makeText(ForgotPasswordForUserActivity.this, "Network error! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }catch(Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordForUserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                pb3.setVisibility(View.GONE);
            }
        });

    }

    //create action menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //It will back to the previous page
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
//       else if (id == R.id.menu_update_profile) {
//            Intent intent = new Intent(ForgotPasswordForFarmerActivity.this, UpdateProfileActivity.class);
//            startActivity(intent);
//        }
        else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(ForgotPasswordForUserActivity.this, "Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ForgotPasswordForUserActivity.this, MainActivity.class);
            //check stack to prevent coming back to the UserprofileActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(ForgotPasswordForUserActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
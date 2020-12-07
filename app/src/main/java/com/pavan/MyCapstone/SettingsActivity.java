package com.pavan.MyCapstone;

import  android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.change_email_button)
     Button btnChangeEmail;
    @BindView(R.id.change_password_button)
     Button btnChangePassword;
    @BindView(R.id.sending_pass_reset_button)
     Button btnSendResetEmail;
    @BindView(R.id.remove_user_button)
     Button btnRemoveUser;
    @BindView(R.id.changeEmail)
     Button changeEmail;
    @BindView(R.id.changePass)
     Button changePassword;
    @BindView(R.id.send)
     Button sendEmail;
    @BindView(R.id.remove)
     Button remove;
    @BindView(R.id.old_email)
     EditText oldEmail;
    @BindView(R.id.new_email)
     EditText newEmail;
    @BindView(R.id.resetpassword)
     EditText password;
    @BindView(R.id.newPassword)
     EditText newPassword;
    @BindView(R.id.progressBar)
     ProgressBar progressBar;
    @BindView(R.id.signedinText)
     TextView signedText;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    public static String errorstring="Enter email";
    public static String errorpass="Password too short, enter minimum 6 characters";
    public static String errorenterpass="Enter password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getIntent().getStringExtra(MainActivity.MENUNAME));
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };



        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);



        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        signedText.append(user.getEmail());

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.emailupdated), Toast.LENGTH_LONG).show();
                                        signOut();

                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.failtoupdateemail), Toast.LENGTH_LONG).show();

                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError(errorstring);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError(errorpass);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.passupdated), Toast.LENGTH_SHORT).show();
                                            signOut();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getString(R.string.passupdtaefail), Toast.LENGTH_SHORT).show();

                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError(errorenterpass);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.resetpasswordsent), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.failtosentresetemail), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                } else {
                    oldEmail.setError(errorstring);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.profiledeleted), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                                        finish();

                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.failedtodelete), Toast.LENGTH_SHORT).show();

                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });



    }


    public  void signOut() {
        auth.signOut();

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}


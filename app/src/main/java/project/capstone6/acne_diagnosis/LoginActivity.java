package project.capstone6.acne_diagnosis;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;


public class LoginActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 10005;
    public static final int GOOGLE_SING_IN_CODE = REQUEST_CODE;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;


    // Google sign in
    private SignInButton google_signin_button;
    private Button signInButton;
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;

    // Facebook sign in
    private LoginButton fb_loginButton;

    // Fingerprint sign in
    private Button biometricButton;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    // text fields and button for sign up
    private EditText etLoginEmail, etLoginPwd;
    private Button btnLogin;

    // text view to turn to sign up page/ reset password
    private TextView turnToReg, forgetPwd;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        // initialize Facebook SDK
        //FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.sdkInitialize(LoginActivity.this);

        google_signin_button = findViewById(R.id.google_signin_button);
        signInButton = findViewById(R.id.googleSignIn);

        fb_loginButton = findViewById(R.id.fbLogInButton);

        biometricButton = findViewById(R.id.btnFingerAuth);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPwd = findViewById(R.id.etLoginPwd);
        btnLogin = findViewById(R.id.btnLogin);
        turnToReg = findViewById(R.id.turnToReg);
        forgetPwd = findViewById(R.id.forgetPwd);

        // check user log in/out status, if the user is logged in, directly go to Result activity
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        // -----------------------------------Google Authentication---------------------------------
        // change button text
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == signInButton) {
                    google_signin_button.performClick();
                }
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleSignInClient.asGoogleApiClient());
                startActivityForResult(intent, GOOGLE_SING_IN_CODE);
            }
        });

        // google sign in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("520981538611-b1e1cdgu0mhcem9i4g96rrm7cch95618.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // check if the user is exit when use google sign in
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            startActivity(new Intent(this, Result.class));
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = googleSignInClient.getSignInIntent();
                startActivityForResult(sign, GOOGLE_SING_IN_CODE);
            }
        });

        // -----------------------------------Facebook Authentication-------------------------------
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        fb_loginButton.setReadPermissions("email", "public_profile");
        fb_loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        // check facebook authentication status
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    firebaseAuth.signOut();
                }
            }
        };

        // -----------------------------------Fingerprint Log in-----------------------------
        // biometric initialization
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.i("Biometric Authentication", "Fingerprint login error: " + errString);
                Toast.makeText(LoginActivity.this, "Fingerprint login error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.i("Biometric Authentication", "Fingerprint login succeed!");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.i("Biometric Authentication", "Fingerprint login failed.");
                Toast.makeText(LoginActivity.this, "Fingerprint login failed", Toast.LENGTH_SHORT).show();
            }
        });

        // set up bio auth dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Please use fingerprint to login")
                .setNegativeButtonText("Use another login method")
                .build();

        // fingerprint login button listener to show login dialot
        biometricButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
        // -----------------------------------Email Log in------------------------------------
        // log in with email
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etLoginEmail.getText().toString().trim();
                String password = etLoginPwd.getText().toString().trim();

                // error msgs
                if (TextUtils.isEmpty(email)) {
                    etLoginEmail.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etLoginPwd.setError("Password is Required.");
                }
                if (password.length() < 6) {
                    etLoginPwd.setError("Password must be more than 6 characters.");
                }

                // authenticate the user
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // -----------------------------------To Register---------------------------------
        // turn to register page
        turnToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        // -----------------------------------Reset Password---------------------------------
        forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText resetEmail = new EditText(view.getContext());

                AlertDialog.Builder pwdResetDialog = new AlertDialog.Builder(view.getContext());
                pwdResetDialog.setTitle("Reset Password?");
                pwdResetDialog.setMessage("Enter your email to receive reset link");
                pwdResetDialog.setView(resetEmail);

                // extract the email and send reset link
                pwdResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String email = resetEmail.getText().toString();

                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "Reset link sent to you email", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Reset link is not sent, please try again" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                // close the dialog if user press negative button
                pwdResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                pwdResetDialog.create().show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    // check activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SING_IN_CODE) {
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount signInAccount = signInTask.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "Your Google account is connected to our application", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Sign in with Facebook
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("failed", task.getException().toString());
                            updateUI(null);
                        }
                    }
                });
    }

    // if log in then go to home page
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
        }
    }


}
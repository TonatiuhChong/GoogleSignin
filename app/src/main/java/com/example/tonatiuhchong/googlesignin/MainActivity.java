package com.example.tonatiuhchong.googlesignin;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity  {

    FirebaseAuth mAuth;
    private SignInButton ingresarG;

    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ingresarG=(SignInButton)findViewById(R.id.BtnSignGoogle);
        mAuth=FirebaseAuth.getInstance();


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(MainActivity.this, " Connection to Google Sign in failed", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();


        ingresarG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });



        }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                if(account!=null){
                    String personName=account.getDisplayName();
                    String personGivenName=account.getGivenName();
                    String personFamilyName=account.getFamilyName();
                    String personEmail=account.getEmail();
                    String personId=account.getId();
                    Uri personPhoto=account.getPhotoUrl();
                    Datos(personGivenName,personId,personName,personEmail,personPhoto,personFamilyName);

                }
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait,while we are getting your auth result...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Can't get Auth Result", Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    private void Datos(String personGivenName, String personId,String personName, String personEmail,Uri personPhoto,String personFamilyName){
        String nombre=personName;
        String Email=personEmail;
        String Familia=personFamilyName;
        String Id= personId;
        String Given=personGivenName;
        Uri fotito=personPhoto;



    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(this,MenuActivity.class));

        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Intent paso= new Intent(MainActivity.this,MenuActivity.class);

                            paso.putExtra("nombreCuenta",acct.getDisplayName());
                            paso.putExtra("EmailC",acct.getEmail());
                            paso.putExtra("Fotografia",acct.getPhotoUrl());
                            startActivity(paso);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Not Authenticated, try again", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }





}

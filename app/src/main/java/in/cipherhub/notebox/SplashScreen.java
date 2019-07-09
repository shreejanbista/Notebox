package in.cipherhub.notebox;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import in.cipherhub.notebox.SignIn.EmailVerification;
import in.cipherhub.notebox.SignIn.LogIn;
import in.cipherhub.notebox.SignIn.SignUp;
import in.cipherhub.notebox.Utils.EmailVerificationListener;
import in.cipherhub.notebox.Utils.Internet;

public class SplashScreen extends AppCompatActivity {

    private String TAG = "SplashScreenOXET";
    private int RC_SIGN_IN = 1234;

    FirebaseUser user = null;
    GoogleSignInClient mGoogleSignInClient;

    Button googleSignin_B;
    FrameLayout signInContainer_FL;
    ImageView finalLogo_IV;
    TextView cipherHub_TV, by_TV;
    View blueBg_v, whiteBg_V;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        googleSignin_B = findViewById(R.id.googleSignin_B);
        signInContainer_FL = findViewById(R.id.signInContainer_FL);
        finalLogo_IV = findViewById(R.id.finalLogo_IV);
        cipherHub_TV = findViewById(R.id.cipherHub_TV);
        by_TV = findViewById(R.id.by_TV);
        blueBg_v = findViewById(R.id.blueBg_V);
        whiteBg_V = findViewById(R.id.whiteBg_V);

        user = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseAuth.getInstance().signOut();

        // For Google Signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignin_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new Internet(getBaseContext()).isAvailable())
                    startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                blueBg_v.setTranslationX(getWindow().getDecorView().getWidth() >> 1);
            }
        }, 100);

        if (user == null) {
            // if user exists then let the visibility of login screen be 'GONE'

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    splashScreenCloseAnim(false);
                }
            }, 2000);
        }


        swapFragment(new LogIn(), false);
    }
    //    private void splashScreenOpenAnim() {
//
//        // Below animation takes total of 2100ms to complete
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                blueBg_v.animate().translationX(getWindow().getDecorView().getWidth() >> 1).setDuration(1000);
//                cipherHub_TV.animate().alpha(1).setDuration(1000);
//                by_TV.animate().alpha(1).setDuration(1000);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finalLogo_IV.animate().scaleX(1).setDuration(1000);
//                        finalLogo_IV.animate().scaleY(1).setDuration(1000);
//
//                    }
//                }, 1000);
//            }
//        }, 100);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    // for google signin
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed", e);
            }
        }
    }

    // for google signin
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success!
                            Toast.makeText(SplashScreen.this, "Google Signin Success!", Toast.LENGTH_SHORT).show();
                            closeSignIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SplashScreen.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void splashScreenCloseAnim(final Boolean completeClose) {
        // Below animation takes total of 2000ms to complete

        finalLogo_IV.animate().scaleX(0).setDuration(1000);
        finalLogo_IV.animate().scaleY(0).setDuration(1000);
        by_TV.animate().alpha(0).setDuration(1000);
        cipherHub_TV.animate().alpha(0).setDuration(1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                blueBg_v.animate().translationX(getWindow().getDecorView().getWidth()).setDuration(700);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (completeClose) {
                            closeSignIn();
                        } else {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }
                    }
                }, 1000);
            }
        }, 1000);
    }

    public void closeSignIn(){
        startActivity(new Intent(SplashScreen.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, 0);
    }

    public void swapFragment(Fragment fragment, Boolean addBackStack) {
        if (!addBackStack)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.signInContainer_FL, fragment)
                    .commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.signInContainer_FL, fragment)
                    .addToBackStack(null)
                    .commit();
    }

}

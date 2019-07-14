package in.cipherhub.notebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.cipherhub.notebox.Registration.EmailVerification;
import in.cipherhub.notebox.Registration.FillDetails;
import in.cipherhub.notebox.Registration.LogIn;
import in.cipherhub.notebox.Registration.SignIn;
import in.cipherhub.notebox.Utils.Internet;

public class SplashScreen extends AppCompatActivity {

    private String TAG = "SplashScreenOXET";

    FirebaseUser user = null;
    FirebaseAuth firebaseAuth;

    ImageView finalLogo_IV;
    TextView cipherHub_TV, by_TV;
    View blueBg_v, whiteBg_V;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Hide the actionbar and set FULLSCREEN flag - for design
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // instantiate views other then the one which are inside fragments
        // those cannot be instantiated here
        finalLogo_IV = findViewById(R.id.finalLogo_IV);
        cipherHub_TV = findViewById(R.id.cipherHub_TV);
        by_TV = findViewById(R.id.by_TV);
        blueBg_v = findViewById(R.id.blueBg_V);
        whiteBg_V = findViewById(R.id.whiteBg_V);

        // Get the last user which signed in
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // user has not logged in open registration page
        if (user == null) {
            splashScreenCloseAnim(false);
        }
        // user has already logged in open Home page after inflating everything necessary
        else {
            pullFromFirebase();
        }
    }


    private void pullFromFirebase() {

        // close splash screen when the pull is done
        splashScreenCloseAnim(true);
    }


    private void splashScreenCloseAnim(final Boolean byPassRegistration) {

        finalLogo_IV.animate().scaleX(0).setDuration(1000);
        finalLogo_IV.animate().scaleY(0).setDuration(1000);
        by_TV.animate().alpha(0).setDuration(1000);
        cipherHub_TV.animate().alpha(0).setDuration(1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // slide blue background to complete screen width
                blueBg_v.animate().translationX(getWindow().getDecorView().getWidth() >> 1).setDuration(700);

                // wait for above animation to end and little extra time for smooth feel
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent;
                        if (byPassRegistration) {
                            // open Main Home page
                            intent = new Intent(SplashScreen.this, MainActivity.class);
                        } else {
                            // open registration page
                            intent = new Intent(SplashScreen.this, SignIn.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, 0);
                    }
                }, 1000);
            }
        }, 1200);
    }
}

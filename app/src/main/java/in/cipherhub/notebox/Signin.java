package in.cipherhub.notebox;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

public class Signin extends Fragment implements View.OnClickListener {

    private String TAG = "SigninOXET";
    private int RC_SIGN_IN = 1000;
    int currentPage = 0;
    String[] template = new String[]{"email", "password", "fullName"};

    Button signin_B, googleSignin_B, changePage_B;
    ConstraintLayout templateParent_CL;
    ConstraintLayout email_L, password_L, fullName_L;
    ImageButton closeBottomTemplate_IB;
    EditText email_ET, password_ET, fullName_ET;
    TextView email_TV, password_TV, fullName_TV;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);

        // parent(outer most) layout
        templateParent_CL = rootView.findViewById(R.id.templateParent_CL);

        // create instances of included layouts // 'L' stands for layout
        fullName_L = rootView.findViewById(R.id.fullName_L);
        email_L = rootView.findViewById(R.id.email_L);
        password_L = rootView.findViewById(R.id.password_L);

        // access views inside included views // check fragment_signin.xml for better understanding
        fullName_TV = fullName_L.findViewById(R.id.child_TV);
        email_TV = email_L.findViewById(R.id.child_TV);
        password_TV = password_L.findViewById(R.id.child_TV);
        fullName_ET = fullName_L.findViewById(R.id.child_ET);
        email_ET = email_L.findViewById(R.id.child_ET);
        password_ET = password_L.findViewById(R.id.child_ET);

        changePage_B = rootView.findViewById(R.id.changePage_B);
        closeBottomTemplate_IB = rootView.findViewById(R.id.closeBottomTemplate_IB);
        googleSignin_B = rootView.findViewById(R.id.googleSignin_B);
        signin_B = rootView.findViewById(R.id.signin_B);

        // register onClickListener to required views
        changePage_B.setOnClickListener(this);
        googleSignin_B.setOnClickListener(this);
        signin_B.setOnClickListener(this);
        closeBottomTemplate_IB.setOnClickListener(this);

        fullName_TV.setText("Full Name:");
        email_TV.setText("E-mail:");
        password_TV.setText("Password:");
        fullName_ET.setHint("full name");
        email_ET.setHint("e-mail");
        password_ET.setHint("password");

        // set input type for EditText of email and password
        email_ET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password_ET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // For Google Signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        // To add Google icon in Google button
        googleSignin_B.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null
                , getResources().getDrawable(R.drawable.ic_google_icon), null);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.closeBottomTemplate_IB:
                // close bottom template on sharp down arrow click
                ((MainActivity) getActivity()).removeBottomTemplate();
                break;

            case R.id.changePage_B:
                if(changePage_B.getText().toString().toLowerCase().contains("login")){
                    // change to login page // make fullName_L visibility GONE

                    fullName_L.setVisibility(View.GONE);
                    signin_B.setText("Login");

                    changePage_B.setText("< Signup");
                } else {
                    // change to sign up page // make fullName_L visible

                    fullName_L.setVisibility(View.VISIBLE);
                    signin_B.setText("Signup");

                    changePage_B.setText("< Login");
                }
                break;

            case R.id.googleSignin_B:
                // Google signin
                email_ET.setText("");
                password_ET.setText("");

                if (haveNetworkConnection())
                    startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
                break;

            case R.id.signin_B:
                // Go to next page
                final String filledEmail = email_ET.getText().toString();
                final String filledPassword = password_ET.getText().toString();
                if (haveNetworkConnection())
                    if (!TextUtils.isEmpty(filledEmail)
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(filledEmail).matches()
                            && filledPassword.length() >= 8)
                        mAuth.createUserWithEmailAndPassword(filledEmail, filledPassword)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(getActivity(), "Signup Success!", Toast.LENGTH_SHORT).show();
                                            ((MainActivity) getActivity()).removeBottomTemplate();
                                        } else {
                                            if (task.getException() != null)
                                                try {
                                                    if (task.getException().getMessage().contains("email address is already in use"))
                                                        mAuth.signInWithEmailAndPassword(filledEmail, filledPassword)
                                                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // Sign in success, update UI with the signed-in user's information
                                                                            Toast.makeText(getActivity(), "Login Success!", Toast.LENGTH_SHORT).show();
                                                                            ((MainActivity) getActivity()).removeBottomTemplate();
                                                                        } else {
                                                                            // If sign in fails, display a message to the user.
                                                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                                            Toast.makeText(getActivity(), "Authentication failed.",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                } catch (Exception e) {
                                                    Log.e(TAG, String.valueOf(e));
                                                }
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(getActivity(), "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    else if (filledPassword.length() < 8)
                        Toast.makeText(getActivity(), "Password should be greater than '8' characters", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Invalid E-mail or Password!!", Toast.LENGTH_SHORT).show();

                break;
        }
    }

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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success!
                            Toast.makeText(getActivity(), "Google Signin Success!", Toast.LENGTH_SHORT).show();
                            ((MainActivity) getActivity()).removeBottomTemplate();


                            /* Uid is the ID mentioned next to every user in Firebase console
                             * and we'll be using this ID as key to store user details in Database*/
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(templateParent_CL, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        if(!haveConnectedWifi && !haveConnectedMobile)
            Snackbar.make(templateParent_CL, "No Internet Connection!", Snackbar.LENGTH_SHORT).show();

        return haveConnectedWifi || haveConnectedMobile;
    }
}

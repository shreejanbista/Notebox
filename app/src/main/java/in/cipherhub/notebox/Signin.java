package in.cipherhub.notebox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import in.cipherhub.notebox.Utils.Internet;
import io.grpc.okhttp.internal.Util;

public class Signin extends Fragment implements View.OnClickListener {

    private String TAG = "SigninOXET";
    private int RC_SIGN_IN = 1000;

    Button signin_B, googleSignin_B, changePage_B;
    ConstraintLayout templateParent_CL;
    ConstraintLayout email_L, password_L, fullName_L;
    ImageButton closeBottomTemplate_IB;
    EditText email_ET, password_ET, fullName_ET;
    TextView email_TV, password_TV, fullName_TV;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user = null;
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

        // TODO: remove below lines after testing
        fullName_ET.setText("Arshdeep Singh");
        email_ET.setText("gogl.arshdeep@gmail.com");
        password_ET.setText("agjmptw1#");

        // set input type for EditText of email and password
        email_ET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password_ET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

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
                if (changePage_B.getText().toString().toLowerCase().contains("login")) {
                    // change to login page // make fullName_L visibility GONE

                    fullName_L.setVisibility(View.GONE);
                    signin_B.setText("LogIn");

                    changePage_B.setText("< Signup");
                } else {
                    // change to sign up page // make fullName_L visible

                    fullName_L.setVisibility(View.VISIBLE);
                    signin_B.setText("Signup");

                    changePage_B.setText("< LogIn");
                }
                break;

            case R.id.googleSignin_B:
                // Google signin
                email_ET.setText("");
                password_ET.setText("");

                if (new Internet(getActivity()).isAvailable())
                    startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
                break;

            case R.id.signin_B:
                // Go to next page
                final String filledFullName = fullName_ET.getText().toString();
                final String filledEmail = email_ET.getText().toString();
                final String filledPassword = password_ET.getText().toString();

                if (new Internet(getActivity()).isAvailable())
                    // have internet to use signup service
                    if (!TextUtils.isEmpty(filledEmail)
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(filledEmail).matches()
                            && filledPassword.length() >= 8)
                        // email and password are valid
                        if (signin_B.getText().toString().startsWith("S"))
                            // write code for Signup
                            if (filledFullName.matches("[a-zA-z]+([ '-][a-zA-Z]+)*"))
                                // Valid full name
                                mAuth.createUserWithEmailAndPassword(filledEmail, filledPassword)
                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Toast.makeText(getActivity(), "Signup Success!", Toast.LENGTH_SHORT).show();

                                                    mAuth.getCurrentUser().sendEmailVerification()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d(TAG, "sent email");
                                                            }
                                                        }
                                                    });
                                                    updateAfterSignup(filledFullName);
                                                } else {
                                                    if (task.getException() != null)
                                                        try {
                                                            if (task.getException().getMessage().contains("email address is already in use"))
                                                                Toast.makeText(getActivity(), "Signup Failed!\nUser E-mail already exists!", Toast.LENGTH_SHORT).show();
                                                        } catch (Exception e) {
                                                            Log.e(TAG, String.valueOf(e));
                                                        }
                                                    else
                                                        // If sign in fails, display a message to the user.
                                                        Toast.makeText(getActivity(), "Authentication failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            else if (!filledFullName.matches("[a-zA-z]+([ '-][a-zA-Z]+)*"))
                                Toast.makeText(getActivity(), "Invalid Full Name!", Toast.LENGTH_SHORT).show();
                            else if (filledPassword.length() < 8)
                                Toast.makeText(getActivity(), "Password should be greater than '8' characters", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(), "Invalid E-mail or Password!!", Toast.LENGTH_SHORT).show();
                        else
                            // write code for LogIn
                            mAuth.signInWithEmailAndPassword(filledEmail, filledPassword)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Toast.makeText(getActivity(), "Signin Success!", Toast.LENGTH_SHORT).show();
                                                updateUI();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                Toast.makeText(getActivity(), "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            break;
        }
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success!
                            Toast.makeText(getActivity(), "Google Signin Success!", Toast.LENGTH_SHORT).show();

                            updateAfterSignup(acct.getDisplayName());

                            ((MainActivity) getActivity()).removeBottomTemplate();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(templateParent_CL, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateAfterSignup(final String fullName) {
        user = mAuth.getCurrentUser();
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("full_name", fullName);
        userDetails.put("branch", "");
        userDetails.put("course", "");
        userDetails.put("institute", "");

        /* Uid is the ID mentioned next to every user in Firebase console Authentication section
         * and this ID is used as key to store user details in Database section */
        db.collection("users").document(user.getUid()).set(userDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        updateUI();
    }

    private void updateUI(){
        ((MainActivity) getActivity()).removeBottomTemplate();
    }
}

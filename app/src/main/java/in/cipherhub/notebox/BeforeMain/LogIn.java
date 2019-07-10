package in.cipherhub.notebox.BeforeMain;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import in.cipherhub.notebox.R;
import in.cipherhub.notebox.Utils.Internet;

public class LogIn extends Fragment implements View.OnClickListener {

    private String TAG = "LogInOXET";

    Button logIn_B;
    EditText email_ET, password_ET;
    TextView forgotPassword_TV, signUp_TV;
    View email_V, password_V;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        logIn_B = rootView.findViewById(R.id.logIn_B);
        email_ET = rootView.findViewById(R.id.email_ET);
        password_ET = rootView.findViewById(R.id.password_ET);
        forgotPassword_TV = rootView.findViewById(R.id.forgotPassword_TV);
        signUp_TV = rootView.findViewById(R.id.signUp_TV);
        email_V = rootView.findViewById(R.id.email_V);
        password_V = rootView.findViewById(R.id.password_V);

        logIn_B.setOnClickListener(this);
        forgotPassword_TV.setOnClickListener(this);
        signUp_TV.setOnClickListener(this);

        email_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!email_ET.getText().toString().equals(""))
                    email_V.setBackgroundColor(getResources().getColor(R.color.colorAppTheme));
                else
                    email_V.setBackgroundColor(getResources().getColor(R.color.colorGray_AAAAAA));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!password_ET.getText().toString().equals(""))
                    password_V.setBackgroundColor(getResources().getColor(R.color.colorAppTheme));
                else
                    password_V.setBackgroundColor(getResources().getColor(R.color.colorGray_AAAAAA));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgotPassword_TV:
                ((SplashScreen) getActivity()).changeFragment(new ForgotPassword(), true);
                break;

            case R.id.signUp_TV:
                ((SplashScreen) getActivity()).changeFragment(new SignUp(), true);
                break;

            case R.id.logIn_B:
                // write to login using email and password

                String filledEmail = email_ET.getText().toString();
                String filledPassword = password_ET.getText().toString();

                if (new Internet(getActivity()).isAvailable())
                    // have internet to use signup service

                    if (!TextUtils.isEmpty(filledEmail)
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(filledEmail).matches()
                            && filledPassword.length() >= 8)
                        // email address is valid and password is greater than 8-digits

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(filledEmail, filledPassword)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(getActivity(), "LogIn Success!", Toast.LENGTH_SHORT).show();
                                            ((SplashScreen) getActivity()).doneWithSignIn();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            if (task.getException() != null)
                                                try {
                                                    if (task.getException().getMessage().contains("no user record"))
                                                        Toast.makeText(getActivity(), "Login Failed!\nUser E-mail does not exists!", Toast.LENGTH_SHORT).show();
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

                    else if (filledPassword.length() < 8)
                        Toast.makeText(getActivity(), "Password should be greater than '8' characters", Toast.LENGTH_SHORT).show();

                    else
                        Toast.makeText(getActivity(), "Invalid E-mail or Password!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

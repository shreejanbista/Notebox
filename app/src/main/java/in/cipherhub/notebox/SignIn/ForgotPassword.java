package in.cipherhub.notebox.SignIn;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;

import in.cipherhub.notebox.R;
import in.cipherhub.notebox.SplashScreen;
import in.cipherhub.notebox.Utils.Internet;

public class ForgotPassword extends Fragment {

    private String TAG = "ForgotPasswordOXET";

    TextView back_B;
    View email_V;
    EditText email_ET;
    Button send_B;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        back_B = rootView.findViewById(R.id.back_B);
        email_ET = rootView.findViewById(R.id.email_ET);
        email_V = rootView.findViewById(R.id.email_V);
        send_B = rootView.findViewById(R.id.send_B);

        back_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        send_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filledEmail = email_ET.getText().toString();

                if (new Internet(getActivity()).isAvailable())
                    // have internet to use signup service

                    if (!TextUtils.isEmpty(filledEmail)
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(filledEmail).matches())
                        // email is valid

                        FirebaseAuth.getInstance().sendPasswordResetEmail(filledEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            getActivity().onBackPressed();
                                            Log.d(TAG, "E-mail sent for password recovery");
                                            Toast.makeText(getActivity(), "Check your email to change the password!"
                                                    , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    else
                        Toast.makeText(getActivity(), "Invalid E-mail address!", Toast.LENGTH_SHORT).show();

            }
        });


        return rootView;
    }
}

package in.cipherhub.notebox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Signin extends Fragment {

    private String TAG = "SigninOXET";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);

        Button googleSignin_B = rootView.findViewById(R.id.googleSignin_B);
        final Button changeToSignup_B = rootView.findViewById(R.id.changeToSignup_B);
        final Button nextTemplate_B = rootView.findViewById(R.id.nextTemplate_B);
        EditText email_ET = rootView.findViewById(R.id.email_ET);
        final TextView fullName_TV = rootView.findViewById(R.id.fullName_TV);
        final EditText fullName_ET = rootView.findViewById(R.id.fullName_ET);

        changeToSignup_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changeToSignup_B.getText().toString().toLowerCase().contains("signup")) {
                    fullName_TV.setVisibility(View.VISIBLE);
                    fullName_ET.setVisibility(View.VISIBLE);
                    changeToSignup_B.setText("< Login");
                    nextTemplate_B.setText("Next");
                } else if (changeToSignup_B.getText().toString().toLowerCase().contains("login")) {
                    fullName_TV.setVisibility(View.GONE);
                    fullName_ET.setVisibility(View.GONE);
                    changeToSignup_B.setText("Signup >");
                    nextTemplate_B.setText("Login");
                }
            }
        });

        googleSignin_B.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null
                , getResources().getDrawable(R.drawable.ic_google_icon), null);

        return rootView;
    }
}

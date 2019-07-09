package in.cipherhub.notebox.SignIn;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import in.cipherhub.notebox.MainActivity;
import in.cipherhub.notebox.R;
import in.cipherhub.notebox.SplashScreen;
import in.cipherhub.notebox.Utils.EmailVerificationListener;

public class EmailVerification extends Fragment {

    Handler mHandler;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mHandler = new Handler();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        startRepeatingTask();

        return inflater.inflate(R.layout.fragment_email_verification, container, false);

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                user.reload();
                if (user.isEmailVerified()) {
                    Toast.makeText(getActivity(), "You email has been verified!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, 1000);
            }
        }
    };

    public void startRepeatingTask() {
        mStatusChecker.run();
    }


    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRepeatingTask();
    }
}

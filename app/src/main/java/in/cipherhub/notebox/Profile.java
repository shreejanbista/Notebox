package in.cipherhub.notebox;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

import in.cipherhub.notebox.registration.SignIn;


public class Profile extends Fragment {

    FirebaseStorage storage;
    StorageReference httpsReference;
    Button signout_b;
    private FirebaseAuth mAuth;

    String TAG = "ProfileOX";
    Button reportbutton, sharebutton, feedbackbutton, aboutbutton;

    FirebaseAuth auth;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
//        signout_b = rootView.findViewById(R.id.signin_B);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        Button signOut_btn = rootView.findViewById(R.id.signOut_btn);

        signOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                getActivity().finish();
                startActivity(new Intent(getContext(), SignIn.class));
            }
        });

        sharebutton = rootView.findViewById(R.id.share_b);
        reportbutton = rootView.findViewById(R.id.report_b);
        feedbackbutton = rootView.findViewById(R.id.feedback_b);
        aboutbutton = rootView.findViewById(R.id.about_b);

    //Rating bar
        RatingBar ratingBar=rootView.findViewById(R.id.ratingBar);
        ratingBar.setRating(5.0f);


        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
                String app_url = "cipherhub.ml";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));

            }
        });

        reportbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");

                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"onecipherhub@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Report your issues.");
                i.putExtra(Intent.EXTRA_TEXT   , "We will contact you soon. Please write in details.");
                //startActivity(Intent.createChooser(i, "Choose an Email client :"));

                try {
                    startActivity(Intent.createChooser(i, "Report your issues."));
                } catch (android.content.ActivityNotFoundException ex) {
                    //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        feedbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");

                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"onecipherhub@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Share your valuable feedback.");
                i.putExtra(Intent.EXTRA_TEXT   , "Your feedback is highly appreciated and will help us to improve.");
                //startActivity(Intent.createChooser(i, "Choose an Email client :"));

                try {
                    startActivity(Intent.createChooser(i, "Feedback"));
                } catch (android.content.ActivityNotFoundException ex) {
                    //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        aboutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAbout(rootView);

            }
        });



            /* all the commented is for downloading from the firebase */

//        storage = FirebaseStorage.getInstance();
//
//        httpsReference = FirebaseStorage.getInstance().getReference();

//        Button download_pdf = view.findViewById(R.id.download_pdf);
//        download_pdf.setOnClickListener(this);

        return rootView;
    }


    //About button
    private void showAbout(View v) {
        ViewGroup viewGroup = v.findViewById(android.R.id.content);
        int width = v.getWidth();
        int height = v.getHeight();


        View view = LayoutInflater.from(getContext()).inflate(R.layout.about_profile, viewGroup, false);

        Button button = view.findViewById(R.id.web_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Page opened",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

}



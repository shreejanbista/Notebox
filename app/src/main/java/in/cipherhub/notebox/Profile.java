package in.cipherhub.notebox;

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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

public class Profile extends Fragment implements View.OnClickListener {

    FirebaseStorage storage;
    StorageReference httpsReference;

    String TAG = "ProfileOX";
    Button reportbutton, sharebutton, feedbackbutton, aboutbutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharebutton = view.findViewById(R.id.share_b);
        reportbutton = view.findViewById(R.id.report_b);
        feedbackbutton = view.findViewById(R.id.feedback_b);
        aboutbutton = view.findViewById(R.id.about_b);


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
                startActivity(Intent.createChooser(i, "Choose an Email client :"));

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
                startActivity(Intent.createChooser(i, "Choose an Email client :"));

                try {
                    startActivity(Intent.createChooser(i, "Feedback"));
                } catch (android.content.ActivityNotFoundException ex) {
                    //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();

                }

            }
        });



            /* all the commented is for downloading from the firebase */

//        storage = FirebaseStorage.getInstance();
//
//        httpsReference = FirebaseStorage.getInstance().getReference();

//        Button download_pdf = view.findViewById(R.id.download_pdf);
//        download_pdf.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //case R.id.report_b:

//            case R.id.download_pdf:
//
//                // Create our main directory for storing files
//                File directPath = new File(Environment.getExternalStorageDirectory() + "/Notebox");
//                if (!directPath.exists()) {
//                    boolean mkdir = directPath.mkdir();
//                    if (!mkdir) {
//                        Log.e(TAG, "Directory creation failed.");
//                    }
//                }
//
//                Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/notebox-1559903149503.appspot.com/o/CSE%2FPYTHON%2FNumpy%20Exercises%20(Unit-1).pdf?alt=media&token=daa8d108-24c4-482b-b4c0-d5a6fb9e15df");
//
//                DownloadManager mgr = (DownloadManager) Objects.requireNonNull(
//                        getActivity()).getSystemService(Context.DOWNLOAD_SERVICE);
//
//                DownloadManager.Request request = new DownloadManager.Request(uri);
//                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                        .setAllowedOverRoaming(false)
//                        // can be named dynamically once we have database ready
////                        .setTitle("test1.pdf")
////                        .setDescription("Something useful? Maybe.")
//                        .setDestinationInExternalPublicDir("/Notebox", "test.pdf")
//                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
//                mgr.enqueue(request);

        }
    }
}

package in.cipherhub.notebox.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.cipherhub.notebox.PDFViewer;
import in.cipherhub.notebox.R;
import in.cipherhub.notebox.adapters.AdapterRecentViews;

public class Explore extends Fragment {

    View rootView;

    private static final String TAG = "Explore";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference httpsReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_explore, container, false);


        /*=============================== DOWNLOADING AND VIEWING PDF CODE ====================================*/

        httpsReference = storage.getReferenceFromUrl(
                "https://firebasestorage.googleapis.com/v0/b/notebox-4f384.appspot.com/" +
                        "o/D3INJYGQIHcJhX24SnrgJBXVaSH3%2FU1_CN2_CSE_BE_NMIT?alt=media&token=f808ef30-eb6b-40e9-ba1b-bc18a2ad5d11");

        try {
            // saved to cache directory
            final File localFile = File.createTempFile("something", ".pdf", getActivity().getCacheDir());

            Log.i(TAG, String.valueOf(localFile));
            Log.i(TAG, String.valueOf(getActivity().getCacheDir()));

            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    // Local temp file has been created
                    Toast.makeText(getActivity(), "Downloaded!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), PDFViewer.class);
                    intent.putExtra("file_name", String.valueOf(localFile));
                    startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "Failed to download!", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*============= END OF -> DOWNLOADING AND VIEWING PDF CODE ==================*/


        return rootView;
    }
}

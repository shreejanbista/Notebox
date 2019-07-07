package in.cipherhub.notebox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Upload extends Fragment implements View.OnClickListener{

//    private StorageReference mStorageRef;

    private String TAG = "uploadOXET";

    Button button, button2, button3, button4, button5;

    private int REQUEST_PDF_PATH = 1000;

    private Uri filePath;

    StorageReference storageRef;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select Picture         "), REQUEST_PDF_PATH);

//        Uri file = Uri.fromFile(new File("data/data/file-path/file-name"));
//        Log.d("file", file.getPath());
//
//
//        StorageReference riversRef = storageRef.child("firebase-storage");
//
//        UploadTask uploadTask = riversRef.putFile(file);
//
//// Register observers to listen for when the download is done or if it fails
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//                Log.d("uploadFail", "" + exception);
//
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
////                sendNotification("upload backup", 1);
//
////                Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                Log.d("downloadUrl", "done uploading");
//            }
//        });


        //BUTTON:
        button = rootView.findViewById(R.id.unitOne_B);
        button2 = rootView.findViewById(R.id.unitTwo_B);
        button3 = rootView.findViewById(R.id.unitThree_B);
        button4 = rootView.findViewById(R.id.unitFour_B);
        button5 = rootView.findViewById(R.id.unitFive_B);

        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);




        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PDF_PATH && resultCode == -1
                && data != null && data.getData() != null){

            filePath = data.getData();

//            Uri file = Uri.fromFile(new File(filePath));
//            Log.d("file", file.getPath());

            StorageReference riversRef = storageRef.child("notes/nmit/this.pdf");

            UploadTask uploadTask = riversRef.putFile(filePath);

    // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("uploadFail", "" + exception);

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
    //                sendNotification("upload backup", 1);

    //                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Log.d("downloadUrl", "done uploading");
                }
            });
        }
    }


    public void onClick(View v) {
        Button button = rootView.findViewById(v.getId());

        Button[] buttons = new Button[]{this.button, button2, button3, button4, button5};
        for(Button thisButton : buttons){
            thisButton.setBackground(getResources().getDrawable(R.drawable.bg_br_radius_gray_smaller));
            thisButton.setTextColor(getResources().getColor(R.color.colorGray_AAAAAA));
        }

        button.setBackground(getResources().getDrawable(R.drawable.bg_apptheme_pill_5));
        button.setTextColor(getResources().getColor(R.color.colorWhite_FFFFFF));
    }
}

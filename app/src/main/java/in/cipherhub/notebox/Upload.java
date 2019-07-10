package in.cipherhub.notebox;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.nio.charset.Charset;

public class Upload extends Fragment implements View.OnClickListener {

//    private StorageReference mStorageRef;

    private String TAG = "uploadOXET";

    Button selectPDF_B, unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B;
    ConstraintLayout signin_CL;
    TextView pdfName_TV, pdfSize_TV;

    private int REQUEST_PDF_PATH = 1000;

    StorageReference storageRef;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        selectPDF_B = rootView.findViewById(R.id.selectPDF_B);
        unitOne_B = rootView.findViewById(R.id.unitOne_B);
        unitTwo_B = rootView.findViewById(R.id.unitTwo_B);
        unitThree_B = rootView.findViewById(R.id.unitThree_B);
        unitFour_B = rootView.findViewById(R.id.unitFour_B);
        unitFive_B = rootView.findViewById(R.id.unitFive_B);
        signin_CL = rootView.findViewById(R.id.signin_CL);
        pdfName_TV = rootView.findViewById(R.id.pdfName_TV);
        pdfSize_TV = rootView.findViewById(R.id.pdfSize_TV);

        selectPDF_B.setOnClickListener(this);
        unitOne_B.setOnClickListener(this);
        unitTwo_B.setOnClickListener(this);
        unitThree_B.setOnClickListener(this);
        unitFour_B.setOnClickListener(this);
        unitFive_B.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View v) {
        Button buttonClicked = rootView.findViewById(v.getId());

        if (buttonClicked == selectPDF_B) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_PDF_PATH);
        } else {

            Button[] allButtons = new Button[]{unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B};
            for (Button button : allButtons) {
                button.setBackground(getResources().getDrawable(R.drawable.bg_br_radius_gray_smaller));
                button.setTextColor(getResources().getColor(R.color.colorGray_AAAAAA));
            }

            buttonClicked.setBackground(getResources().getDrawable(R.drawable.bg_apptheme_pill_5));
            buttonClicked.setTextColor(getResources().getColor(R.color.colorWhite_FFFFFF));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PDF_PATH && resultCode == -1
                && data != null && data.getData() != null) {

            Uri fileUri = data.getData();

            pdfName_TV.setText(getFileDetails(fileUri)[0]);
            pdfSize_TV.setText(getFileDetails(fileUri)[1]);

            signin_CL.animate().alpha(1).setDuration(500);

            String userInstitute = "Nitte Meenakshi Institute of Technology";
            String userCourse = "Bachelors in Engineering";
            String userBranch = "Computer Science and Engineering";

            Log.d(TAG, generateAbbreviation(userInstitute));

//            StorageReference riversRef = storageRef.child("notes/nmit_560064/be/cse/14cs63/this.pdf");
//
//            UploadTask uploadTask = riversRef.putFile(fileUri);
//
//            // Register observers to listen for when the download is done or if it fails
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    Log.d("uploadFail", "" + exception);
//
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                    //                sendNotification("upload backup", 1);
//
//                    //                Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                    Log.d("downloadUrl", "done uploading");
//                }
//            });
        }
    }

    public String generateAbbreviation(String fullForm) {
        String abbreviation = "";

        for (int i = 0; i < fullForm.length(); i++) {
            char temp = fullForm.charAt(i);
            abbreviation += Character.isUpperCase(temp) ? temp : "";
        }
        return abbreviation;
    }

    public String[] getFileDetails(Uri uri) {
        String[] result = new String[]{null, null};
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result[0] = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Double fileSize = Double.parseDouble(cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE)));
                    result[1] = String.valueOf(fileSize / (1024 * 1024)).substring(0, 5) + " MB";
                }
            }
        }
        if (result[0] == null) {
            result[0] = uri.getPath();
            if (result[0] != null && result[0].contains("/")) {
                int cut = result[0].lastIndexOf('/');
                if (cut != -1) {
                    result[0] = result[0].substring(cut + 1);
                }
            }
        }
        return result;
    }
}

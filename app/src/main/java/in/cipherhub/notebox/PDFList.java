package in.cipherhub.notebox;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import in.cipherhub.notebox.adapters.AdapterPDFList;
import in.cipherhub.notebox.models.ItemDataBranchSelector;
import in.cipherhub.notebox.models.ItemPDFList;
import in.cipherhub.notebox.utils.Internet;

import static android.content.Context.MODE_PRIVATE;

public class PDFList extends AppCompatActivity implements View.OnClickListener {

  private String TAG = "PDFListOXET";
  String userLikedPDFs = "", userDislikedPDFs = "";
  int likedCount = 0, dislikedCount = 0, totalRating = 0;

  Button unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B;
  Button[] allButtons;
  List<ItemPDFList> pdfList = new ArrayList<>();
  List<ItemPDFList> filteredPDFList = new ArrayList<>();
  ItemPDFList openedPDFItem;

  FirebaseFirestore db;
  FirebaseAuth firebaseAuth;
  FirebaseUser user;
  FirebaseStorage storage = FirebaseStorage.getInstance();
  StorageReference httpsReference;

  SharedPreferences localDB, localBookmarkDB, localBookmarkDBBoolean, localLikeDBBoolean, localDislikeDBBoolean;
  SharedPreferences.Editor localBookmarkDBEditor, localBookmarkDBBooleanEditor, localLikeDBBooleanEditor, localDislikeDBBooleanEditor;
  JSONObject userObject, subject, pdf;

  RecyclerView PDFList_RV;
  AdapterPDFList adapterPDFList;
  DocumentReference pdfDocRef;
  Bundle extras;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pdflist);

    getSupportActionBar().hide();

    db = FirebaseFirestore.getInstance();
    firebaseAuth = FirebaseAuth.getInstance();
    user = firebaseAuth.getCurrentUser();
    localDB = getSharedPreferences("localDB", MODE_PRIVATE);
    localBookmarkDB = getSharedPreferences("localBookmarkDB", MODE_PRIVATE);
    localBookmarkDBEditor = localBookmarkDB.edit();
    localBookmarkDBBoolean = getSharedPreferences("localBookmarkDBBoolean", MODE_PRIVATE);
    localBookmarkDBBooleanEditor = localBookmarkDBBoolean.edit();
    localLikeDBBoolean = getSharedPreferences("localLikeDBBoolean", MODE_PRIVATE);
    localLikeDBBooleanEditor = localLikeDBBoolean.edit();
    localDislikeDBBoolean = getSharedPreferences("localDislikeDBBoolean", MODE_PRIVATE);
    localDislikeDBBooleanEditor = localDislikeDBBoolean.edit();

    extras = getIntent().getExtras();

    TextView subjectName_TV = findViewById(R.id.subjectName_TV);
    final TextView subjectAbbreviation_TV = findViewById(R.id.subjectAbbreviation_TV);
    unitOne_B = findViewById(R.id.unitOne_B);
    unitTwo_B = findViewById(R.id.unitTwo_B);
    unitThree_B = findViewById(R.id.unitThree_B);
    unitFour_B = findViewById(R.id.unitFour_B);
    unitFive_B = findViewById(R.id.unitFive_B);
    PDFList_RV = findViewById(R.id.PDFList_RV);
    EditText pdfSearch_ET = findViewById(R.id.pdfSearch_ET);

    pdfSearch_ET.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        List<ItemPDFList> filteredList = new ArrayList<>();

        for (ItemPDFList s : pdfList) {
          //new array list that will hold the filtered data
          //if the existing elements contains the search input
          if (s.getName().toLowerCase().contains(editable.toString().toLowerCase())
                  || s.getBy().toLowerCase().contains(editable.toString().toLowerCase())
                  || s.getAuthor().toLowerCase().contains(editable.toString().toLowerCase())) {
            filteredList.add(s);
          }
        }
        filteredPDFList = filteredList;
        adapterPDFList.filterList(filteredList);
      }
    });

    allButtons = new Button[]{unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B};

    for (Button button : allButtons) {
      button.setOnClickListener(this);
    }

    subjectName_TV.setText(extras.getString("subjectName"));
    subjectAbbreviation_TV.setText(extras.getString("subjectAbbreviation"));

    adapterPDFList = new AdapterPDFList(pdfList);

    adapterPDFList.setOnItemClickListener(new AdapterPDFList.OnItemClickListener() {
      @Override
      public void onItemClick(int position) {
        if (!filteredPDFList.isEmpty()) {
          openedPDFItem = filteredPDFList.get(position);
        } else if (!pdfList.isEmpty()) {
          openedPDFItem = pdfList.get(position);
        }
        buildDialog();
      }
    });

    PDFList_RV.setAdapter(adapterPDFList);
    PDFList_RV.setLayoutManager(new LinearLayoutManager(PDFList.this));

    // set PDF document reference for whole page
    try {
      userObject = new JSONObject(localDB.getString("user", "Error Fetching..."));

      final String subject_doc_name = extras.getString("subjectAbbreviation").toLowerCase()
              + "_" + generateAbbreviation(userObject.getString("branch")).toLowerCase()
              + "_" + generateAbbreviation(userObject.getString("course")).toLowerCase();

      pdfDocRef = db.collection("institutes")
              .document(userObject.getString("institute"))
              .collection("subject_notes")
              .document(subject_doc_name);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    // it will update all the PDFs if any one of them changes
    // then filters complete list
    pdfDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
      @Override
      public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (documentSnapshot != null) {
          pdfList = new ArrayList<>();

          subject = new JSONObject(documentSnapshot.getData());

          Iterator<String> pdfs = subject.keys();
          while (pdfs.hasNext()) {
            String pdfName = pdfs.next();
            try {
              pdf = subject.getJSONObject(pdfName);
              likedCount = pdf.getInt("likes");
              dislikedCount = pdf.getInt("dislikes");
              totalRating = likedCount - dislikedCount;
              pdfList.add(new ItemPDFList(
                      pdfName
                      , pdf.getString("by")
                      , pdf.getString("author")
                      , pdf.getString("date")
                      , pdf.getInt("shares")
                      , pdf.getInt("downloads")
                      , totalRating
              ));
            } catch (JSONException e1) {
              Log.d(TAG, "error: " + e1);
            }
          }
          adapterPDFList.filterList(pdfList);
        }
      }
    });
  }


  @Override
  public void onClick(View view) {
    Button buttonClicked = findViewById(view.getId());

    for (Button button : allButtons) {
      button.setBackground(getResources().getDrawable(R.drawable.bg_br_radius_gray_smaller));
      button.setTextColor(getResources().getColor(R.color.colorGray_777777));
    }

    buttonClicked.setBackground(getResources().getDrawable(R.drawable.bg_apptheme_pill_5));
    buttonClicked.setTextColor(getResources().getColor(R.color.colorWhite_FFFFFF));

    pdfList.clear();

    Iterator<String> pdfs = subject.keys();
    while (pdfs.hasNext()) {
      String pdfName = pdfs.next();
      if (pdfName.startsWith("U" + buttonClicked.getText().toString())) {
        try {
          JSONObject pdf = subject.getJSONObject(pdfName);
          likedCount = pdf.getInt("likes");
          dislikedCount = pdf.getInt("dislikes");
          totalRating = likedCount - dislikedCount;
          pdfList.add(new ItemPDFList(
                  pdfName
                  , pdf.getString("by")
                  , pdf.getString("author")
                  , pdf.getString("date")
                  , pdf.getInt("shares")
                  , pdf.getInt("downloads")
                  , totalRating
          ));
          Log.d(TAG, "pdfList: " + pdfList);
        } catch (JSONException e1) {
          Log.d(TAG, "error: " + e1);
        }
      }
      adapterPDFList.filterList(pdfList);
    }
  }


  private void buildDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);

    View dialogView = getLayoutInflater().inflate(R.layout.dialog_pdf, null);
    Dialog dialog = new Dialog(this, R.style.DialogBottomAnimation);
    dialog.getWindow().setGravity(Gravity.BOTTOM);
    dialog.setContentView(dialogView);
    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogBottomAnimation;
    dialog.show();

    TextView pdfName_TV = dialogView.findViewById(R.id.pdfName_TV);
    TextView byValue_TV = dialogView.findViewById(R.id.byValue_TV);
    TextView authorValue_TV = dialogView.findViewById(R.id.authorValue_TV);
    TextView sharesCount_TV = dialogView.findViewById(R.id.sharesCount_TV);
    TextView downloadsCount_TV = dialogView.findViewById(R.id.downloadsCount_TV);
    TextView date_TV = dialogView.findViewById(R.id.date_TV);
    final TextView rating_TV = dialogView.findViewById(R.id.rating_TV);
    Button download_B = dialogView.findViewById(R.id.download_B);

    Button sharePDF_B = dialogView.findViewById(R.id.sharePDF_B);
    final Button bookmark_B = dialogView.findViewById(R.id.bookmark_B);
    final Button like_IB = dialogView.findViewById(R.id.like_IB);
    final Button dislike_IB = dialogView.findViewById(R.id.dislike_IB);

    userLikedPDFs = "";
    userDislikedPDFs = "";

    final DocumentReference userDocRef = db.collection("users").document(user.getUid());
    Log.d(TAG, user.getUid());

    userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
      @Override
      public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (documentSnapshot != null) {
          try {
            JSONArray likedArray = new JSONObject(documentSnapshot.getData()).getJSONArray("liked");
            JSONArray dislikedArray = new JSONObject(documentSnapshot.getData()).getJSONArray("disliked");

            userLikedPDFs = String.valueOf(likedArray);
            if (userLikedPDFs.contains(openedPDFItem.getName())) {
//              like_IB.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLightAppTheme)));
//              like_IB.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLightAppTheme)));
              like_IB.setCompoundDrawablesRelativeWithIntrinsicBounds(null
                      , getDrawable(R.drawable.ic_thumb_up_black_24dp), null, null);
            } else {
              like_IB.setCompoundDrawablesRelativeWithIntrinsicBounds(null
                      , getDrawable(R.drawable.ic_thumb_up_fill_aaaaaa), null, null);
            }
            like_IB.setText(String.valueOf(likedCount));

            userDislikedPDFs = String.valueOf(dislikedArray);
            if (userDislikedPDFs.contains(openedPDFItem.getName())) {
              dislike_IB.setCompoundDrawablesRelativeWithIntrinsicBounds(null
                      , getDrawable(R.drawable.ic_thumb_down_black_24dp), null, null);
            } else {
              dislike_IB.setCompoundDrawablesRelativeWithIntrinsicBounds(null
                      , getDrawable(R.drawable.ic_thumb_down_fill_aaaaaa), null, null);
            }
            dislike_IB.setText(String.valueOf(dislikedCount));
            totalRating = likedCount - dislikedCount;
            rating_TV.setText(String.valueOf(totalRating));

          } catch (JSONException e1) {
            e1.printStackTrace();
          }
        } else {
          Log.e(TAG, "document snapshot is null!");
        }
      }
    });

    pdfName_TV.setText(openedPDFItem.getName());
    byValue_TV.setText(openedPDFItem.getBy());
    authorValue_TV.setText(openedPDFItem.getAuthor());
    date_TV.setText(openedPDFItem.getDate());
    sharesCount_TV.setText(String.valueOf(openedPDFItem.getTotalShares()));
    downloadsCount_TV.setText(String.valueOf(openedPDFItem.getTotalDownloads()));
    rating_TV.setText(String.valueOf(totalRating));

    if (localBookmarkDBBoolean.getBoolean(openedPDFItem.getName(), false)) {
      bookmark_B.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_bookmark_red_fill)
              , null, null, null);
    }

    sharePDF_B.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(PDFList.this, "Feature coming soon...", Toast.LENGTH_SHORT).show();
      }
    });

    bookmark_B.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (localBookmarkDBBoolean.getBoolean(openedPDFItem.getName(), false)) {
          // if already bookmarked
          bookmark_B.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_bookmark_gray_border)
                  , null, null, null);

          localBookmarkDBEditor.remove(openedPDFItem.getName()).apply();
          localBookmarkDBBooleanEditor.remove(openedPDFItem.getName()).apply();
          Toast.makeText(PDFList.this, openedPDFItem.getName() + " removed from bookmarks!", Toast.LENGTH_SHORT).show();
        } else {
          // if not bookmarked
          bookmark_B.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_bookmark_red_fill)
                  , null, null, null);
          Map<String, Object> pdfDetails = new HashMap<>();
          pdfDetails.put("name", "\"" + openedPDFItem.getName() + "\"");
          pdfDetails.put("by", "\"" + openedPDFItem.getBy() + "\"");
          pdfDetails.put("author", "\"" + openedPDFItem.getAuthor() + "\"");
          pdfDetails.put("date", "\"" + openedPDFItem.getDate() + "\"");
          pdfDetails.put("shares", "\"" + openedPDFItem.getTotalShares() + "\"");
          pdfDetails.put("downloads", "\"" + openedPDFItem.getTotalDownloads() + "\"");
          pdfDetails.put("rating", "\"" + openedPDFItem.getRating() + "\"");
          try {
            pdfDetails.put("url", "\"" + subject.getJSONObject(openedPDFItem.getName()).getString("url") + "\"");
          } catch (JSONException e) {
            Log.e(TAG, String.valueOf(e));
          }
          localBookmarkDBEditor.putString(openedPDFItem.getName(), String.valueOf(pdfDetails)).apply();
          localBookmarkDBBooleanEditor.putBoolean(openedPDFItem.getName(), true).apply();
          Toast.makeText(PDFList.this, openedPDFItem.getName() + " added to your bookmarks!", Toast.LENGTH_SHORT).show();
        }
      }
    });

    download_B.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        /*=============================== DOWNLOADING AND VIEWING PDF CODE ====================================*/
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(PDFList.this);
        progressDialog.setTitle("Downloading File");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
          httpsReference = storage.getReferenceFromUrl(subject.getJSONObject(openedPDFItem.getName()).getString("url"));
        } catch (JSONException e) {
          e.printStackTrace();
        }

        try {
          // saved to cache directory
          final File localFile = File.createTempFile(openedPDFItem.getName(), ".pdf", getCacheDir());

          Log.i(TAG, String.valueOf(localFile));
          Log.i(TAG, String.valueOf(getCacheDir()));

          httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

              // Local temp file has been created
              Toast.makeText(PDFList.this, openedPDFItem.getName() + " download complete!"
                      , Toast.LENGTH_SHORT).show();

              progressDialog.dismiss();
              Intent intent = new Intent(PDFList.this, PDFViewer.class);
              intent.putExtra("file_name", String.valueOf(localFile));
              startActivity(intent);
            }
          }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
              progressDialog.dismiss();
              Toast.makeText(PDFList.this, "Failed to download!", Toast.LENGTH_SHORT).show();
            }
          }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
              // percentage in progress dialog
              double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
              progressDialog.setMessage("File: " + openedPDFItem.getName() + "\n" + "Downloaded " + ((int) progress) + "%");
            }
          });

        } catch (IOException e) {
          e.printStackTrace();
        }

        /*============= END OF -> DOWNLOADING AND VIEWING PDF CODE ==================*/

      }
    });
    final Map<String, Object> pdfDetails = new HashMap<>();
    pdfDetails.put("author", openedPDFItem.getAuthor());
    pdfDetails.put("by", openedPDFItem.getBy());
    pdfDetails.put("date", openedPDFItem.getDate());
    pdfDetails.put("downloads", openedPDFItem.getTotalDownloads());
    pdfDetails.put("name", openedPDFItem.getName());
    pdfDetails.put("shares", openedPDFItem.getTotalShares());
    pdfDetails.put("likes", likedCount);
    pdfDetails.put("dislikes", dislikedCount);
    try {
      pdfDetails.put("url", subject.getJSONObject(openedPDFItem.getName()).getString("url"));
    } catch (JSONException e) {
      e.printStackTrace();
    }


    like_IB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!userLikedPDFs.contains(openedPDFItem.getName())) {
          likedCount++;
          pdfDetails.put("likes", likedCount);
          // if user had previously disliked the PDF then remove dislike
          if (userDislikedPDFs.contains(openedPDFItem.getName())) {
            dislikedCount--;
            pdfDetails.put("dislikes", dislikedCount);
            // update the user dislikes array
            userDocRef.update("disliked", FieldValue.arrayRemove(openedPDFItem.getName()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Un-disliked & updated");
                      }
                    });
          }
          // update the user liked array
          userDocRef.update("liked", FieldValue.arrayUnion(openedPDFItem.getName()))
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      userDocRef.update("disliked", FieldValue.arrayRemove(openedPDFItem.getName()))
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                  Log.d(TAG, "Un-disliked & updated");
                                }
                              });
                      Log.d(TAG, "Liked & updated");
                    }
                  });
        } else {
          // if previously liked and user wants to remove the like
          likedCount--;
          pdfDetails.put("likes", likedCount);
          // update user likes in the user section
          userDocRef.update("liked", FieldValue.arrayRemove(openedPDFItem.getName()))
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      Log.d(TAG, "Un-liked & updated");
                    }

                  });
        }
        // update in the notes section for likes
        pdfDocRef.update(openedPDFItem.getName(), pdfDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Like updated in notes section");
                  }
                })
                .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Failed to update likes in notes section");
                  }
                });
      }
    });

    dislike_IB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!userDislikedPDFs.contains(openedPDFItem.getName())) {
          // if user wants to dislike the PDF
          dislikedCount++;
          pdfDetails.put("dislikes", dislikedCount);
          if (userLikedPDFs.contains(openedPDFItem.getName())) {
            // if user had previously liked the PDF then his like should be removed
            likedCount--;
            pdfDetails.put("likes", likedCount);
            userDocRef.update("liked", FieldValue.arrayRemove(openedPDFItem.getName()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Un-liked & updated");
                      }
                    });
          }
          // add this PDF to user disliked PDFs
          userDocRef.update("disliked", FieldValue.arrayUnion(openedPDFItem.getName()))
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      userDocRef.update("liked", FieldValue.arrayRemove(openedPDFItem.getName()))
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                  Log.d(TAG, "Un-liked & updated");
                                }
                              });
                      Log.i(TAG, "Disliked & updated");
                    }
                  });
        } else {
          // if user wants to remove dislike from this PDF
          dislikedCount--;
          pdfDetails.put("dislikes", dislikedCount);
          // remove dislike from user disliked PDFs
          userDocRef.update("disliked", FieldValue.arrayRemove(openedPDFItem.getName()))
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      Log.d(TAG, "Un-disliked & updated");
                    }
                  });
        }
        // update dislike count in notes section
        pdfDocRef.update(openedPDFItem.getName(), pdfDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Dislike updated in notes section");
                  }
                })
                .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Failed to update dislikes in notes section");
                  }
                });
      }
    });

//    like_IB.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        // initialize variables for local use
//        final int afterRating;
//        boolean likeThisPDF;
//
//        // inflate all the PDF details to change just one value #beingCheap
//        Map<String, Object> pdfDetails = new HashMap<>();
//        pdfDetails.put("name", openedPDFItem.getName());
//        pdfDetails.put("by", openedPDFItem.getBy());
//        pdfDetails.put("author", openedPDFItem.getAuthor());
//        pdfDetails.put("date", openedPDFItem.getDate());
//        pdfDetails.put("shares", openedPDFItem.getTotalShares());
//        pdfDetails.put("downloads", openedPDFItem.getTotalDownloads());
//        try {
//          pdfDetails.put("url", subject.getJSONObject(openedPDFItem.getName()).getString("url"));
//          pdfDetails.put("liked_users", subject.getJSONObject(openedPDFItem.getName()).getJSONArray("liked_users"));
//        } catch (JSONException e) {
//          Log.e(TAG, String.valueOf(e));
//        }
//
//        if (likedUsers.toString().contains(user.getUid())) {
//          // UNLIKE code here
//
//          like_IB.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray_AAAAAA)));
//          afterRating = openedPDFItem.getRating() - 1;
//          likeThisPDF = false;
//        } else {
//          // LIKE code here
//
//          like_IB.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLightAppTheme)));
//          afterRating = openedPDFItem.getRating() + 1;
//          likeThisPDF = true;
//        }
//
//        pdfDetails.put("rating", afterRating);
//
//        // update new rating in the cloud
//        final boolean finalLike = likeThisPDF;
//        pdfDocRef.update(openedPDFItem.getName(), pdfDetails)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                  @Override
//                  public void onSuccess(Void aVoid) {
//                    rating_TV.setText(String.valueOf(afterRating));
//                    localLikeDBBooleanEditor.putBoolean(openedPDFItem.getName(), finalLike);
//                    Toast.makeText(PDFList.this, "Updated!", Toast.LENGTH_SHORT).show();
//                  }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                  @Override
//                  public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(PDFList.this, "Failed to update likes!", Toast.LENGTH_SHORT).show();
//                  }
//                });
//      }
//    });

//    dislike_IB.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        like_IB.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray_AAAAAA)));
//        dislike_IB.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLightAppTheme)));
//        int afterRating = openedPDFItem.getRating() - 1;
//        rating_TV.setText(String.valueOf(afterRating));
//
//        Map<String, Object> pdfDetails = new HashMap<>();
//        pdfDetails.put("name", openedPDFItem.getName());
//        pdfDetails.put("by", openedPDFItem.getBy());
//        pdfDetails.put("author", openedPDFItem.getAuthor());
//        pdfDetails.put("date", openedPDFItem.getDate());
//        pdfDetails.put("shares", openedPDFItem.getTotalShares());
//        pdfDetails.put("downloads", openedPDFItem.getTotalDownloads());
//        pdfDetails.put("rating", afterRating);
//        pdfDocRef.update(openedPDFItem.getName(), pdfDetails)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                  @Override
//                  public void onSuccess(Void aVoid) {
//                  }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                  @Override
//                  public void onFailure(@NonNull Exception e) {
//
//                    Toast.makeText(PDFList.this, "Failed to update likes!", Toast.LENGTH_SHORT).show();
//                  }
//                });
//      }
//    });
  }


  public String generateAbbreviation(String fullForm) {
    StringBuilder abbreviation = new StringBuilder();

    for (int i = 0; i < fullForm.length(); i++) {
      char temp = fullForm.charAt(i);
      abbreviation.append(Character.isUpperCase(temp) ? temp : "");
    }
    return abbreviation.toString();
  }
}

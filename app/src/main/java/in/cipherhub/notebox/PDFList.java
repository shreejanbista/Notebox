package in.cipherhub.notebox;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.cipherhub.notebox.adapters.AdapterPDFList;
import in.cipherhub.notebox.models.ItemDataBranchSelector;
import in.cipherhub.notebox.models.ItemPDFList;
import in.cipherhub.notebox.utils.Internet;

public class PDFList extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "PDFListOXET";

    Button selectPDF_B, upload_button, unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B;
    Button[] allButtons;
    final List<ItemPDFList> pdfList = new ArrayList<>();

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    SharedPreferences localDB;
    JSONObject userObject;
    JSONObject subject;

    RecyclerView PDFList_RV;
    AdapterPDFList adapterPDFList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdflist);

        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        localDB = getSharedPreferences("localDB", MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();

        TextView subjectName_TV = findViewById(R.id.subjectName_TV);
        TextView subjectAbbreviation_TV = findViewById(R.id.subjectAbbreviation_TV);
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
                adapterPDFList.filterList(filteredList);
            }
        });

        allButtons = new Button[]{unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B};

        for (Button button : allButtons) {
            button.setOnClickListener(this);
        }

        subjectName_TV.setText(extras.getString("subjectName"));
        subjectAbbreviation_TV.setText(extras.getString("subjectAbbreviation"));

        final String[] units = new String[]{"u1", "u2", "u3", "u4", "u5"};

        adapterPDFList = new AdapterPDFList(pdfList);
        PDFList_RV.setAdapter(adapterPDFList);
        PDFList_RV.setLayoutManager(new LinearLayoutManager(PDFList.this));

        try {
            userObject = new JSONObject(localDB.getString("user", "Error Fetching..."));

            if (new Internet(this).isAvailable()) {

                final String subject_doc_name = extras.getString("subjectAbbreviation").toLowerCase()
                        + "_" + generateAbbreviation(userObject.getString("course")).toLowerCase()
                        + "_" + generateAbbreviation(userObject.getString("branch")).toLowerCase();

                db.collection("institutes")
                        .document(userObject.getString("institute"))
                        .collection("subject_notes")
                        .document(subject_doc_name)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            subject = new JSONObject(task.getResult().getData());
                            try {
                                for (String unit : units) {
                                    JSONArray unitsArray = subject.getJSONArray(unit);
                                    if (unitsArray.length() > 0)
                                        for (int i = 0; i < unitsArray.length(); i++) {
                                            JSONObject pdf = unitsArray.getJSONObject(i);
                                            pdfList.add(new ItemPDFList(
                                                    pdf.getString("name")
                                                    , pdf.getString("by")
                                                    , pdf.getString("author")
                                                    , pdf.getString("date")
                                                    , pdf.getInt("total_shares")
                                                    , pdf.getInt("total_downloads")
                                                    , pdf.getDouble("rating")
                                            ));
                                        }
                                }
                                adapterPDFList.filterList(pdfList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error fetching JSON: " + e);
                                Toast.makeText(PDFList.this, "Couldn't fetch PDF now, Try again later..."
                                        , Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.d(TAG, "Error fetching PDF list: " + task.getException());
                            Toast.makeText(PDFList.this, "Couldn't fetch PDF now, Try again later..."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String generateAbbreviation(String fullForm) {
        StringBuilder abbreviation = new StringBuilder();

        for (int i = 0; i < fullForm.length(); i++) {
            char temp = fullForm.charAt(i);
            abbreviation.append(Character.isUpperCase(temp) ? temp : "");
        }
        return abbreviation.toString();
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

        try {

            JSONArray unitsArray = subject.getJSONArray("u" + buttonClicked.getText().toString());
            Log.d(TAG, "unitsArray: " + unitsArray);
            Log.d(TAG, "button clicked: " + buttonClicked.getText().toString());
            pdfList.clear();
            for (int i = 0; i < unitsArray.length(); i++) {

                JSONObject pdf = unitsArray.getJSONObject(i);
                Log.d(TAG, String.valueOf(pdf));
                pdfList.add(new ItemPDFList(
                        pdf.getString("name")
                        , pdf.getString("by")
                        , pdf.getString("author")
                        , pdf.getString("date")
                        , pdf.getInt("total_shares")
                        , pdf.getInt("total_downloads")
                        , pdf.getDouble("rating")
                ));
            }

            adapterPDFList.filterList(pdfList);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Error fetching JSON: " + e);
            Toast.makeText(PDFList.this, "Couldn't fetch PDF now, Try again later..."
                    , Toast.LENGTH_SHORT).show();
        }
    }
}

package in.cipherhub.notebox;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import in.cipherhub.notebox.adapters.AdapterPDFList;
import in.cipherhub.notebox.models.ItemPDFList;

public class PDFList extends AppCompatActivity implements View.OnClickListener {

	private String TAG = "PDFListOXET";

	Button selectPDF_B, upload_button, unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B;
	Button[] allButtons;
	List<ItemPDFList> pdfList = new ArrayList<>();
	List<ItemPDFList> filteredPDFList = new ArrayList<>();
	ItemPDFList openedPDFItem;

	FirebaseFirestore db;
	FirebaseAuth firebaseAuth;
	FirebaseUser user;
	SharedPreferences localDB, localBookmarkDB, localBookmarkDBBoolean;
	SharedPreferences.Editor localBookmarkDBEditor, localBookmarkDBBooleanEditor;
	JSONObject userObject;
	JSONObject subject;

	RecyclerView PDFList_RV;
	AdapterPDFList adapterPDFList;
	JSONObject pdf;
	DocumentReference pdfDocRef;
	Bundle extras;



	FirebaseStorage storage = FirebaseStorage.getInstance();
	StorageReference httpsReference;

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
		localBookmarkDBBoolean = getSharedPreferences("localBookmarkDBBoolean", MODE_PRIVATE);
		localBookmarkDBEditor = localBookmarkDB.edit();
		localBookmarkDBBooleanEditor = localBookmarkDBBoolean.edit();

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

		pdfDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				if (documentSnapshot.exists()) {
					pdfList = new ArrayList<>();

					subject = new JSONObject(documentSnapshot.getData());
//                    Log.d(TAG, String.valueOf(subject));
					Iterator<String> pdfs = subject.keys();
					while (pdfs.hasNext()) {
						String pdfName = pdfs.next();
						try {
							JSONObject pdf = subject.getJSONObject(pdfName);
							pdfList.add(new ItemPDFList(
									pdfName,
									pdf.getString("by"),
									pdf.getString("author"),
									pdf.getString("date"),
									pdf.getInt("shares"),
									pdf.getInt("downloads"),
									pdf.getDouble("rating"),
									pdf.getString("url")
							));
							adapterPDFList.filterList(pdfList);

						} catch (JSONException e1) {
							Log.d(TAG, "error: " + e1);
						}
					}
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
					pdfList.add(new ItemPDFList(
							pdfName, pdf.getString("by"),
							pdf.getString("author"),
							pdf.getString("date"),
							pdf.getInt("shares"),
							pdf.getInt("downloads"),
							pdf.getDouble("rating"),
							pdf.getString("url")
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

		final TextView rating_TV = dialogView.findViewById(R.id.rating_TV);
		TextView date_TV = dialogView.findViewById(R.id.date_TV);
		Button sharePDF_B = dialogView.findViewById(R.id.sharePDF_B);
		Button download_B = dialogView.findViewById(R.id.download_B);

		final Button bookmark_B = dialogView.findViewById(R.id.bookmark_B);

		pdfName_TV.setText(openedPDFItem.getName());
		byValue_TV.setText(openedPDFItem.getBy());
		authorValue_TV.setText(openedPDFItem.getAuthor());
		date_TV.setText(openedPDFItem.getDate());
		sharesCount_TV.setText(String.valueOf(openedPDFItem.getTotalShares()));
		downloadsCount_TV.setText(String.valueOf(openedPDFItem.getTotalDownloads()));
		rating_TV.setText(String.valueOf(openedPDFItem.getRating()));

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


		download_B.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Testing", Toast.LENGTH_SHORT).show();
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

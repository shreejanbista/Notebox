package in.cipherhub.notebox;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivityOX";

    Button home_B, explore_B, upload_B, profile_B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        home_B = findViewById(R.id.home_B);
        explore_B = findViewById(R.id.explore_B);
        upload_B = findViewById(R.id.upload_B);
        profile_B = findViewById(R.id.profile_B);

        home_B.setOnClickListener(this);
        explore_B.setOnClickListener(this);
        upload_B.setOnClickListener(this);
        profile_B.setOnClickListener(this);

        applyInitButState(new Button[]{home_B, explore_B, upload_B, profile_B});
        customButtonRadioGroup(home_B);
    }

    public void applyInitButState(Button[] buttons) {
        for (Button button : buttons) {
            // below line gives the name of icon with respect to button name
            int buttonIconDrawableId = getResources().getIdentifier(
                    "icon_" + button.getText().toString().toLowerCase() + "_but_unfocused",
                    "drawable", getPackageName());
            // set icons only on the top // icons position is adjusted from XML
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    getResources().getDrawable(buttonIconDrawableId), null, null);
            button.setTextColor(getResources().getColor(R.color.colorGray_AAAAAA));
        }
    }

    @Override
    public void onClick(View view) {
        Button buttonClicked = findViewById(view.getId());
        /* Below line should be changed to switch condition if more buttons are introduced
         * registered with setOnClickListener(this) in onCreate method of this activity. */
        customButtonRadioGroup(buttonClicked);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1000);
    }

    public void customButtonRadioGroup(Button buttonClicked) {
        String buttonClickedTitle = buttonClicked.getText().toString().toLowerCase();
        Fragment fragment;

        applyInitButState(new Button[]{home_B, explore_B, upload_B, profile_B});

        int buttonClickedIconDrawableId = getResources().getIdentifier(
                "icon_" + buttonClickedTitle + "_but_focused",
                "drawable", getPackageName());
        buttonClicked.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                getResources().getDrawable(buttonClickedIconDrawableId), null, null);
        buttonClicked.setTextColor(getResources().getColor(R.color.colorAppTheme));

        try {
            fragment = (Fragment) (Class.forName(getPackageName() + "." + buttonClickedTitle).newInstance());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            fragment = new home();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.mainPagesContainer_FL, fragment)
                .commit();
    }
}

/* 1. User profile
 * 2. upload
 * 3. fetch */
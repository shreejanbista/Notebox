package in.cipherhub.notebox;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivityOXET";

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

        // Home Page is launched onCreate to make it default
        customButtonRadioGroup(home_B);
    }

    @Override
    public void onClick(View view) {
        Button buttonClicked = findViewById(view.getId());
        /* Below line should be changed to switch condition if more buttons are introduced
         * registered with setOnClickListener(this) in onCreate method of this activity. */
        customButtonRadioGroup(buttonClicked);
    }

    // Make all choices in Bottom Navigation Bar as unfocused
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

    // select one choice in Bottom Navigation Bar and commit fragment transaction
    public void customButtonRadioGroup(Button buttonClicked) {
        String buttonClickedTitle = buttonClicked.getText().toString();
        Fragment fragment;

        applyInitButState(new Button[]{home_B, explore_B, upload_B, profile_B});

        int buttonClickedIconDrawableId = getResources().getIdentifier(
                "icon_" + buttonClickedTitle.toLowerCase() + "_but_focused",
                "drawable", getPackageName());
        buttonClicked.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                getResources().getDrawable(buttonClickedIconDrawableId), null, null);
        buttonClicked.setTextColor(getResources().getColor(R.color.colorAppTheme));

        try {
            fragment = (Fragment) (Class.forName(getPackageName() + "." + buttonClickedTitle).newInstance());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            fragment = new Home();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.mainPagesContainer_FL, fragment)
                .commit();
    }

    public void temp() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
                .replace(R.id.signinTemplateContainer_FL, new Signin())
                .addToBackStack(null)
                .commit();
//
//        final View semiDarkBg_V = findViewById(R.id.semiDarkBg_V);
//        semiDarkBg_V.animate().alpha(0.2f).setDuration(700);
//
//        semiDarkBg_V.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                semiDarkBg_V.animate().alpha(0).setDuration(700);
//                getSupportFragmentManager().popBackStack();
//            }
//        });
    }
}

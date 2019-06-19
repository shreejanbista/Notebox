package in.cipherhub.notebox;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    }

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
            fragment = (Fragment) (Class.forName("in.cipherhub.notebox." + buttonClickedTitle).newInstance());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            fragment = new HomePage();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.mainPagesContainer_FL, fragment)
                .commit();
    }
}

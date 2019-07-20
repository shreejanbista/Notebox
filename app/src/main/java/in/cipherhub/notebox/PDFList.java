package in.cipherhub.notebox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PDFList extends AppCompatActivity implements View.OnClickListener {

    Button selectPDF_B, upload_button, unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B;
    Button[] allButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdflist);

        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();

        TextView subjectName_TV = findViewById(R.id.subjectName_TV);
        TextView subjectAbbreviation_TV = findViewById(R.id.subjectAbbreviation_TV);
        unitOne_B = findViewById(R.id.unitOne_B);
        unitTwo_B = findViewById(R.id.unitTwo_B);
        unitThree_B = findViewById(R.id.unitThree_B);
        unitFour_B = findViewById(R.id.unitFour_B);
        unitFive_B = findViewById(R.id.unitFive_B);
        RecyclerView PDFList_RV = findViewById(R.id.PDFList_RV);

        allButtons = new Button[]{unitOne_B, unitTwo_B, unitThree_B, unitFour_B, unitFive_B};

        for (Button button : allButtons){
            button.setOnClickListener(this);
        }

        subjectName_TV.setText(extras.getString("subjectName"));
        subjectAbbreviation_TV.setText(extras.getString("subjectAbbreviation"));

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
    }


}

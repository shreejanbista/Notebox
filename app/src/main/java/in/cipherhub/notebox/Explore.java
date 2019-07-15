package in.cipherhub.notebox;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import in.cipherhub.notebox.adapters.AdapterRecentViews;

public class Explore extends Fragment implements View.OnClickListener{

    Button institutes_b, courses_b, branches_b, subjects_b;
    View rootView;
    public static final String FRAGMENT_PDF_RENDERER_BASIC = "PDF_RENDERER";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_explore, container, false);

       /* if (savedInstanceState == null) {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new PdfRenderer())
                        .commit();
            }
        }*/

        final ConstraintLayout subjectsLayout_CL = rootView.findViewById(R.id.subjectsLayout_CL);
        final ImageButton searchIconInSearchBar_IB = rootView.findViewById(R.id.searchIconInSearchBar_IB);
        final EditText subjectsSearch_ET = rootView.findViewById(R.id.subjectsSearch_ET);
        final ConstraintLayout recentViewsLayout_CL = rootView.findViewById(R.id.recentViewsLayout_CL);
        RecyclerView recentViews_RV = rootView.findViewById(R.id.recentViews_RV);



        List<AdapterRecentViews.recentViewsItemData> recentViews = new ArrayList<>();
        recentViews.add(new AdapterRecentViews.recentViewsItemData(
                "CN2", "CSE", "10:10PM", "Computer Networks - 2"
        ));
        recentViews.add(new AdapterRecentViews.recentViewsItemData(
                "OS", "CSE", "09:11AM", "Operating System"
        ));
        recentViews.add(new AdapterRecentViews.recentViewsItemData(
                "MDS", "ECE", "03:02PM", "Most Difficult Subject"
        ));
        recentViews.add(new AdapterRecentViews.recentViewsItemData(
                "DS", "ECE", "07:54AM", "Data Structures using C++"
        ));

        AdapterRecentViews recentViewsAdapter = new AdapterRecentViews(recentViews);
        recentViews_RV.setAdapter(recentViewsAdapter);
        recentViews_RV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));



        //BUTTON::
//        institutes_b = rootView.findViewById(R.id.button_institutes);
//        courses_b = rootView.findViewById(R.id.button_courses);
//        branches_b = rootView.findViewById(R.id.button_branches);
//        subjects_b = rootView.findViewById(R.id.button_subjects);
//
//
//        institutes_b.setOnClickListener(this);
//        courses_b.setOnClickListener(this);
//        branches_b.setOnClickListener(this);
//        subjects_b.setOnClickListener(this);
//


        return rootView;
    }



    public void onClick(View v) {
        Button button = rootView.findViewById(v.getId());

        Button[] buttons = new Button[]{this.institutes_b, courses_b, branches_b, subjects_b};
        for(Button thisButton : buttons){
            thisButton.setBackground(getResources().getDrawable(R.drawable.bg_br_radius_gray_smaller));
            thisButton.setTextColor(getResources().getColor(R.color.colorGray_AAAAAA));
        }

        button.setBackground(getResources().getDrawable(R.drawable.bg_apptheme_pill_5));
        button.setTextColor(getResources().getColor(R.color.colorWhite_FFFFFF));
    }
}

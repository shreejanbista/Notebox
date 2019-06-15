package in.cipherhub.notebox;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment {

    adapterHomeSubjects homeSubjectAdapter;
    List<dataHomeSubjectsItem> homeSubjects;

    private String TAG = "homeOX";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        final ConstraintLayout subjectsLayout_CL = rootView.findViewById(R.id.subjectsLayout_CL);
        final ConstraintLayout recentViewsLayout_CL = rootView.findViewById(R.id.recentViewsLayout_CL);
        final EditText subjectsSearch_ET = rootView.findViewById(R.id.subjectsSearch_ET);
        final ImageButton searchIconInSearchBar_IB = rootView.findViewById(R.id.searchIconInSearchBar_IB);
        RecyclerView recentViews_RV = rootView.findViewById(R.id.recentViews_RV);
        RecyclerView homeSubjects_RV = rootView.findViewById(R.id.homeSubjects_RV);
        ImageButton bookmark_IB = rootView.findViewById(R.id.bookmark_IB);

        bookmark_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookmarkActivity.class));
            }
        });

        subjectsSearch_ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(subjectsSearch_ET.isFocused()) {
                    subjectsLayout_CL.animate().translationY(-recentViewsLayout_CL.getHeight()).setDuration(500);
                    searchIconInSearchBar_IB.setImageDrawable(getResources().getDrawable(R.drawable.icon_down_arrow));
                } else {    // when click on background root Constraint Layout
                    subjectsLayout_CL.animate().translationY(0).setDuration(500);
                    searchIconInSearchBar_IB.setImageDrawable(getResources().getDrawable(R.drawable.icon_search));

                    // to hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(subjectsSearch_ET.getWindowToken(), 0);
                }
            }
        });

        List<adapterRecentViews.recentViewsItemData> recentViews = new ArrayList<>();
        recentViews.add(new adapterRecentViews.recentViewsItemData(
                "CN2", "CSE", "10:10PM", "Computer Networks - 2"
        ));
        recentViews.add(new adapterRecentViews.recentViewsItemData(
                "OS", "CSE", "09:11AM", "Operating System"
        ));
        recentViews.add(new adapterRecentViews.recentViewsItemData(
                "MDS", "ECE", "03:02PM", "Most Difficult Subject"
        ));
        recentViews.add(new adapterRecentViews.recentViewsItemData(
                "DS", "ECE", "07:54AM", "Data Structures using C++"
        ));

        adapterRecentViews recentViewsAdapter = new adapterRecentViews(recentViews);
        recentViews_RV.setAdapter(recentViewsAdapter);
        recentViews_RV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        homeSubjects = new ArrayList<>();
        homeSubjects.add(new dataHomeSubjectsItem(
                "CN2", "Computer Networks - 2", "last update: 12 June, 2019", false
        ));
        homeSubjects.add(new dataHomeSubjectsItem(
                "DS", "Data Structures with Cpp", "last update: 12 January, 2014", true
        ));
        homeSubjects.add(new dataHomeSubjectsItem(
                "MDS", "Most Difficult Subjects", "last update: 21 December, 2012", false
        ));
        homeSubjects.add(new dataHomeSubjectsItem(
                "GT", "Graph Theory", "last update: 13 March, 2015", false
        ));

        homeSubjectAdapter = new adapterHomeSubjects(homeSubjects);
        homeSubjects_RV.setAdapter(homeSubjectAdapter);
        homeSubjects_RV.setLayoutManager(new LinearLayoutManager(getActivity()));

        subjectsSearch_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        return rootView;
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        List<dataHomeSubjectsItem> filteredList = new ArrayList<>();

        //looping through existing elements
        for (dataHomeSubjectsItem s : homeSubjects) {
            //if the existing elements contains the search input
            if (s.subName.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filteredList.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        homeSubjectAdapter.filterList(filteredList);
    }
}

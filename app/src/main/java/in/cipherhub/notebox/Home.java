package in.cipherhub.notebox;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.cipherhub.notebox.Adapters.AdapterHomeSubjects;
import in.cipherhub.notebox.Adapters.AdapterRecentViews;
import in.cipherhub.notebox.Models.DataHomeSubjectsItem;

public class Home extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    AdapterHomeSubjects homeSubjectAdapter;
    List<DataHomeSubjectsItem> homeSubjects;

    private String TAG = "homeOX";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        final ConstraintLayout subjectsLayout_CL = rootView.findViewById(R.id.subjectsLayout_CL);
        final ConstraintLayout recentViewsLayout_CL = rootView.findViewById(R.id.recentViewsLayout_CL);
        final EditText subjectsSearch_ET = rootView.findViewById(R.id.subjectsSearch_ET);
        final ImageButton searchIconInSearchBar_IB = rootView.findViewById(R.id.searchIconInSearchBar_IB);
        Button signin_B = rootView.findViewById(R.id.signin_B);
        RecyclerView recentViews_RV = rootView.findViewById(R.id.recentViews_RV);
        RecyclerView homeSubjects_RV = rootView.findViewById(R.id.homeSubjects_RV);
        ImageButton bookmark_IB = rootView.findViewById(R.id.bookmark_IB);
//        final View signinTemplate_BT = rootView.findViewById(R.id.signinTemplate_BT);

        if (user == null) {
            homeSubjects_RV.setVisibility(View.GONE);
            subjectsSearch_ET.setFocusable(false);
        } else {
            homeSubjects_RV.setVisibility(View.VISIBLE);
            subjectsSearch_ET.setFocusable(true);
        }

        signin_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).temp();
            }
        });

        bookmark_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), BookmarkActivity.class));
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

        homeSubjects = new ArrayList<>();
        homeSubjects.add(new DataHomeSubjectsItem(
                "CN2", "Computer Networks - 2", "last update: 12 June, 2019", false
        ));
        homeSubjects.add(new DataHomeSubjectsItem(
                "DS", "Data Structures with Cpp", "last update: 12 January, 2014", true
        ));
        homeSubjects.add(new DataHomeSubjectsItem(
                "MDS", "Most Difficult Subjects", "last update: 21 December, 2012", false
        ));
        homeSubjects.add(new DataHomeSubjectsItem(
                "GT", "Graph Theory", "last update: 13 March, 2015", false
        ));

        homeSubjectAdapter = new AdapterHomeSubjects(homeSubjects);
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
        List<DataHomeSubjectsItem> filteredList = new ArrayList<>();

        for (DataHomeSubjectsItem s : homeSubjects) {
        //new array list that will hold the filtered data
            //if the existing elements contains the search input
            if (s.subName.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(s);
            }
        }
        homeSubjectAdapter.filterList(filteredList);
    }

    @Override
    public void onClick(View view) {

    }
}

// TODO: recycler view heights stays short after keyboard is hidden... solve it :)
// TODO: when letter length exceeds, use dots to end the sentence...

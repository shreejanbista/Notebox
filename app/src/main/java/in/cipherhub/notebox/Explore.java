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

public class Explore extends Fragment {

    View rootView;

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

        return rootView;
    }
}

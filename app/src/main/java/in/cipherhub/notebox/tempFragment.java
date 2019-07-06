package in.cipherhub.notebox;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.cipherhub.notebox.Adapters.adapterHomeSubjects;
import in.cipherhub.notebox.Models.DataHomeSubjectsItem;

public class tempFragment extends Fragment {

    adapterHomeSubjects homeSubjectAdapter;
    List<DataHomeSubjectsItem> homeSubjects;

    private String TAG = "homeOX";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.item_recent_views , container, false);

        return rootView;
    }
}

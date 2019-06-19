package in.cipherhub.notebox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Explore extends Fragment {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "PDF_RENDERER";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        if (savedInstanceState == null) {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new PdfRenderer())
                        .commit();
            }
        }

        return view;
    }
}

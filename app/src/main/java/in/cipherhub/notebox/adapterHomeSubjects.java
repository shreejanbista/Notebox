package in.cipherhub.notebox;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class adapterHomeSubjects extends RecyclerView.Adapter<adapterHomeSubjects.homeSubjectsItemViewHolder> {
    private List<dataHomeSubjectsItem> list;

    public adapterHomeSubjects(List<dataHomeSubjectsItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public adapterHomeSubjects.homeSubjectsItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new adapterHomeSubjects.homeSubjectsItemViewHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.home_subjects_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull adapterHomeSubjects.homeSubjectsItemViewHolder homeSubjectsItemViewHolder, int i) {

        homeSubjectsItemViewHolder.subAbb_TV.setText(list.get(i).subAbb);
        homeSubjectsItemViewHolder.subName_TV.setText(list.get(i).subName);
        homeSubjectsItemViewHolder.lastUpdate_TV.setText(list.get(i).lastUpdate);
    }

    void filterList(List<dataHomeSubjectsItem> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class homeSubjectsItemViewHolder extends RecyclerView.ViewHolder {
        TextView subAbb_TV, subName_TV, lastUpdate_TV;
        ImageButton subBookmark_IB;
        homeSubjectsItemViewHolder(@NonNull View itemView) {
            super(itemView);

            subAbb_TV = itemView.findViewById(R.id.subAbb_TV);
            subName_TV = itemView.findViewById(R.id.subName_TV);
            lastUpdate_TV = itemView.findViewById(R.id.lastUpdate_TV);
            subBookmark_IB = itemView.findViewById(R.id.subBookmark_IB);
        }
    }
}


package in.cipherhub.notebox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class adapterRecentViews extends RecyclerView.Adapter<adapterRecentViews.recentViewsItemViewHolder>{

    List<adapterRecentViews.recentViewsItemData> list;

    public adapterRecentViews(List<adapterRecentViews.recentViewsItemData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public recentViewsItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new recentViewsItemViewHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.recent_views_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull recentViewsItemViewHolder recentViewsItemViewHolder, int i) {

        recentViewsItemViewHolder.subAbb_TV.setText(list.get(i).subAbb);
        recentViewsItemViewHolder.branchAbb_TV.setText(list.get(i).branchAbb);
        recentViewsItemViewHolder.time_TV.setText(list.get(i).time);
        recentViewsItemViewHolder.subName_TV.setText(list.get(i).subName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class recentViewsItemViewHolder extends RecyclerView.ViewHolder {
        TextView subAbb_TV, branchAbb_TV, time_TV, subName_TV;
        recentViewsItemViewHolder(@NonNull View itemView) {
            super(itemView);

            subAbb_TV = itemView.findViewById(R.id.subAbb_TV);
            branchAbb_TV = itemView.findViewById(R.id.branchAbb_TV);
            time_TV = itemView.findViewById(R.id.time_TV);
            subName_TV = itemView.findViewById(R.id.subName_TV);
        }
    }

    static class recentViewsItemData {

        private String subAbb, branchAbb, time, subName;

        recentViewsItemData(String subAbb, String branchAbb, String time, String subName) {
            this.subAbb = subAbb;
            this.branchAbb = branchAbb;
            this.time = time;
            this.subName = subName;
        }
    }
}

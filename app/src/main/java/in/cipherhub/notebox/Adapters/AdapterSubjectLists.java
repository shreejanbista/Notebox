package in.cipherhub.notebox.Adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.cipherhub.notebox.Models.ItemSubjectsModel;
import in.cipherhub.notebox.R;

public class AdapterSubjectLists extends RecyclerView.Adapter<AdapterSubjectLists.branchSelectorItemViewHolder>{

    private List<ItemSubjectsModel> list;
    private OnItemClickListener mListener;

    public AdapterSubjectLists(List<ItemSubjectsModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public branchSelectorItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new branchSelectorItemViewHolder(LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.subject_recycler_view, viewGroup, false), mListener);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull branchSelectorItemViewHolder branchSelectorItemViewHolder, int i) {

        branchSelectorItemViewHolder.lrg_text_view.setText(list.get(i).getSubjectName());
        branchSelectorItemViewHolder.short_text_view.setText(generateAbbreviation(list.get(i).getSubjectAbbColor()));
//        branchSelectorItemViewHolder.short_text_view.setTextColor(Color.parseColor(list.get(i).getBranchAbbColor()));

    }


    public void filterList(List<ItemSubjectsModel> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class branchSelectorItemViewHolder extends RecyclerView.ViewHolder {

        TextView short_text_view, lrg_text_view;
        ConstraintLayout itemSubjectSelector_CL;

        branchSelectorItemViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            itemSubjectSelector_CL = itemView.findViewById(R.id.itemSubjectSelector_CL);
            lrg_text_view = itemView.findViewById(R.id.lrg_text_view);
            short_text_view = itemView.findViewById(R.id.short_text_view);

            itemSubjectSelector_CL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }


    private String generateAbbreviation(String fullForm) {
        StringBuilder abbreviation = new StringBuilder();

        for (int i = 0; i < fullForm.length(); i++) {
            char temp = fullForm.charAt(i);
            abbreviation.append(Character.isUpperCase(temp) ? temp : "");
        }
        return abbreviation.toString();
    }
    
    
}

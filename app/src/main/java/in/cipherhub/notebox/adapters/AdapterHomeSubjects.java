package in.cipherhub.notebox.adapters;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.cipherhub.notebox.PDFList;
import in.cipherhub.notebox.R;
import in.cipherhub.notebox.models.ItemDataHomeSubjects;

public class AdapterHomeSubjects extends RecyclerView.Adapter<AdapterHomeSubjects.homeSubjectsItemViewHolder> {

	private List<ItemDataHomeSubjects> list;
	private AdapterBranchSelector.OnItemClickListener mListener;

	public AdapterHomeSubjects(List<ItemDataHomeSubjects> list) {
		this.list = list;
	}


	@NonNull
	@Override
	public AdapterHomeSubjects.homeSubjectsItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

		AdapterHomeSubjects.homeSubjectsItemViewHolder holder = new AdapterHomeSubjects.homeSubjectsItemViewHolder(LayoutInflater
				.from(viewGroup.getContext())
				.inflate(R.layout.item_home_subjects, viewGroup, false), mListener);

		return holder;
	}



	@Override
	public void onBindViewHolder(@NonNull AdapterHomeSubjects.homeSubjectsItemViewHolder homeSubjectsItemViewHolder, int i) {

		homeSubjectsItemViewHolder.subAbb_TV.setText(list.get(i).subAbb);
		homeSubjectsItemViewHolder.subName_TV.setText(list.get(i).subName);

		String lastUpdateDate = list.get(i).lastUpdate;
		if (lastUpdateDate.isEmpty()) {
			homeSubjectsItemViewHolder.lastUpdate_TV.setVisibility(View.GONE);
		} else {
			homeSubjectsItemViewHolder.lastUpdate_TV.setVisibility(View.VISIBLE);
			String last_update = "last update: " + lastUpdateDate;
			homeSubjectsItemViewHolder.lastUpdate_TV.setText(last_update);
		}
	}

	class homeSubjectsItemViewHolder extends RecyclerView.ViewHolder {

		TextView subAbb_TV, subName_TV, lastUpdate_TV;
		//        ImageButton subBookmark_IB;
		ConstraintLayout itemHomeSubjects_CL;


		homeSubjectsItemViewHolder(@NonNull View itemView, final AdapterBranchSelector.OnItemClickListener listener) {
			super(itemView);

			subAbb_TV = itemView.findViewById(R.id.subAbb_TV);
			subName_TV = itemView.findViewById(R.id.subName_TV);
			lastUpdate_TV = itemView.findViewById(R.id.lastUpdate_TV);
//            subBookmark_IB = itemView.findViewById(R.id.subBookmark_IB);
			itemHomeSubjects_CL = itemView.findViewById(R.id.itemHomeSubjects_CL);

			itemHomeSubjects_CL.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(view.getContext(), PDFList.class);
					intent.putExtra("subjectName", subName_TV.getText());
					intent.putExtra("subjectAbbreviation", subAbb_TV.getText());
					view.getContext().startActivity(intent);
//                    // code to send data out of adapter
//                    if(listener != null){
//                        int position = getAdapterPosition();
//                        if(position != RecyclerView.NO_POSITION){
//                            listener.onItemClick(position);
//                        }
//                    }
				}
			});
		}
	}

	public void filterList(List<ItemDataHomeSubjects> filteredList) {
		this.list = filteredList;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return list.size();
	}


	public interface OnItemClickListener {
		void onItemClick(int position);
	}

	public void setOnItemClickListener(AdapterBranchSelector.OnItemClickListener listener) {
		this.mListener = listener;
	}



}


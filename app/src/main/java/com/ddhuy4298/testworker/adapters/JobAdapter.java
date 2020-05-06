package com.ddhuy4298.testworker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ddhuy4298.testworker.databinding.ItemJobBinding;
import com.ddhuy4298.testworker.listener.JobClickedListener;
import com.ddhuy4298.testworker.models.Job;

import java.util.ArrayList;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobHolder> {

    private ArrayList<Job> data;
    private LayoutInflater inflater;
    private JobClickedListener listener;
    private boolean multipleCheck = false;

    public JobAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setData(ArrayList<Job> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setListener(JobClickedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJobBinding binding = ItemJobBinding.inflate(inflater, parent, false);
        return new JobHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JobHolder holder, int position) {
        holder.binding.setItem(data.get(position));
        if (listener != null) {
            holder.binding.setListener(listener);
        }
        holder.binding.image.setImageResource(data.get(position).getImage());
        if (multipleCheck) {
            holder.binding.checkbox.setChecked(data.get(position).isChecked());
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class JobHolder extends RecyclerView.ViewHolder {

        private ItemJobBinding binding;

        public JobHolder(@NonNull ItemJobBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setMultipleCheck(boolean multipleCheck) {
        this.multipleCheck = multipleCheck;
        if (!multipleCheck) {
            for (Job job : data) {
                job.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Job> getCheckedJob() {
        ArrayList<Job> checkedJob = new ArrayList<>();
        for (Job job : data) {
            if (job.isChecked()) {
                checkedJob.add(job);
            }
        }
        return checkedJob;
    }
}

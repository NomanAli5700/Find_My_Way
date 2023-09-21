package com.example.findmyway01;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.annotation.Nonnull;

public class EmployeeAdapterClass extends RecyclerView.Adapter<EmployeeAdapterClass.EmployeeViewHolder> {
    private List<EmployeeModelClass> employees;

    public EmployeeAdapterClass(List<EmployeeModelClass> employees) {
        this.employees = employees;
    }

    @Nonnull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_item_list, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull EmployeeViewHolder holder, int position) {
        EmployeeModelClass employee = employees.get(position);
        holder.bind(employee);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private TextView fname;
        private TextView lname;
        private TextView email;
        private TextView des;

        public EmployeeViewHolder(@Nonnull View itemView) {
            super(itemView);
            fname = itemView.findViewById(R.id.Vfname);
            lname = itemView.findViewById(R.id.Vlname);
            email = itemView.findViewById(R.id.Vemail);
            des = itemView.findViewById(R.id.Vdes);
        }

        public void bind(EmployeeModelClass employee) {
            fname.setText(employee.getFname_1());
            lname.setText(employee.getLname_1());
            email.setText(employee.getEmail());
            des.setText(employee.getDes_1());
        }
    }
}

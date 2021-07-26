package com.example.fintal.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fintal.Models.Register;
import com.example.fintal.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterAdapter extends RecyclerView.Adapter<RegisterAdapter.ViewHolder> implements Filterable {
    //Tag for debugging
    public static final String TAG = "RegisterAdapter";
    private Context context;
    private List<Register> registers;
    private List<Register> registersFull;

    private static final int EXPENSE_REGISTER = 123;
    private static final int INCOME_REGISTER = 321;

    public RegisterAdapter(Context context, List<Register> registers) {
        this.context = context;
        this.registers = registers;
        this.registersFull = new ArrayList<>();

    }

    //Clean all elements of the adapter
    public void clear() {
        registers.clear();
        notifyDataSetChanged();
    }

    //Add elements to the adapter's list
    public void addAll(List<Register> list) {
        registers.addAll(list);
        registersFull.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == EXPENSE_REGISTER) {
            View view = inflater.inflate(R.layout.item_expense, parent, false);
            return new ExpenseViewHolder(view);
        } else if (viewType == INCOME_REGISTER) {
            View view = inflater.inflate(R.layout.item_income, parent, false);
            return new IncomeViewHolder(view);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Register register = registers.get(position);
        holder.bind(register);
    }

    @Override
    public int getItemViewType(int position) {
        if (registerType(position)) {
            return INCOME_REGISTER;
        } else {
            return EXPENSE_REGISTER;
        }
    }

    @Override
    public int getItemCount() {
        return registers.size();
    }

    private boolean registerType(int position) {
        Register register = registers.get(position);
        return register.getType();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Register> filteredList = new ArrayList<>();
            Log.d(TAG ,Integer.toString(registersFull.size()));
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(registersFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Register item : registersFull) {
                    if (item.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            registers.clear();
            registers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        abstract void bind(Register register);
    }

    public class ExpenseViewHolder extends ViewHolder {
        private ImageView ivIcon;
        private TextView tvDescription;
        private TextView tvCategory;
        private TextView tvAmount;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        @Override
        void bind(Register register) {
            //Bind register data to the view elements
            tvDescription.setText(register.getDescription());
            tvAmount.setText("$" + register.getAmount().toString());
            tvCategory.setText((String) register.getCategory().get("name"));
        }
    }

    public class IncomeViewHolder extends ViewHolder {
        private ImageView ivIcon;
        private TextView tvDescription;
        private TextView tvCategory;
        private TextView tvAmount;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        @Override
        void bind(Register register) {
            //Bind register data to the view elements
            tvDescription.setText(register.getDescription());
            tvAmount.setText("$" + register.getAmount().toString());
            tvCategory.setText((String) register.getCategory().get("name"));
        }
    }
}

package com.example.fintal.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fintal.AccountDetailsActivity;
import com.example.fintal.Models.Account;
import com.example.fintal.Models.Transaction;
import com.example.fintal.R;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    //Tag for debugging
    public static final String TAG = "TransactionAdapter";
    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context,  List<Transaction> list) {
        this.context = context;
        this.transactions = list;
    }

    //clean all elements of adapter
    public void clear() {
        transactions.clear();
        notifyDataSetChanged();
    }

    //Add elements to adapter's list
    public void addAll(List<Transaction> list) {
        transactions.addAll(list);
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bank_transaction, parent, false);
        return new TransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAmount;
        TextView tvDescription;
        TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
            tvDescription = itemView.findViewById(R.id.tvTransactionDescription);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
        }

        public void bind(Transaction transaction) {
            //Bind the account data to the view elements
            //Format to two decimals
            String formattedValue = String.format("%.2f", transaction.amount);
            //Set if transaction is OUTFLOW or INFLOW
            if (transaction.type.equals("OUTFLOW")) {
                formattedValue = "-" + formattedValue;
            }
            tvAmount.setText("$" + formattedValue + transaction.currency);
            tvDescription.setText(transaction.description);
            tvDate.setText(transaction.valueDate);
        }
    }
}

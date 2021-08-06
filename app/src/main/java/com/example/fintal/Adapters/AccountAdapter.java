package com.example.fintal.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.example.fintal.R;

import org.parceler.Parcels;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    //Tag for debugging
    public static final String TAG = "AccountAdapter";
    private Context context;
    private List<Account> accounts;

    public AccountAdapter(Context context, List<Account> accounts) {
        this.context = context;
        this.accounts = accounts;
    }

    //Clean all elements of the adapter
    public void clear() {
        accounts.clear();
        notifyDataSetChanged();
    }

    //Add elements to the adapter's list
    public void addAll(List<Account> list) {
        accounts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bank_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.bind(account);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RelativeLayout cardContainer;
        ImageView ivBankLogo;
        TextView tvCardBalance;
        ConstraintLayout decorator;
        TextView tvAccountNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            ivBankLogo = itemView.findViewById(R.id.ivBankLogo);
            tvCardBalance = itemView.findViewById(R.id.tvCardBalance);
            decorator = itemView.findViewById(R.id.decorator);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            itemView.setOnClickListener(this);
        }

        public void bind(Account account) {
            //Bind the account data to the view elements
            //Format to two decimals
            String formattedValue = String.format("%.2f", account.balance);
            tvCardBalance.setText("$" + formattedValue + account.currency);
            tvAccountNumber.setText(account.accountNumber);
            if (account.category.equals("CREDIT_CARD")) {
                decorator.setBackgroundTintList(context.getResources().getColorStateList(R.color.color_credit));
            }
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Clicked");
            //Get item position
            int position = getAdapterPosition();
            //Validate the position exists
            if (position != RecyclerView.NO_POSITION) {
                //Get post at position
                Account account = accounts.get(position);
                //Create intent for activity
                Intent intent = new Intent(context, AccountDetailsActivity.class);
                intent.putExtra(Account.class.getSimpleName(), Parcels.wrap(account));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context);
                context.startActivity(intent, options.toBundle());
            }
        }
    }



}

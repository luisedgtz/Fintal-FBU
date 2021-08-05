package com.example.fintal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.fintal.Models.Register;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.parse.ParseFile;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class RegisterDetailsActivity extends AppCompatActivity {

    private Register register;
    private TextView tvDescription;
    private TextView tvCategory;
    private TextView tvAmount;
    private ImageView ivIcon;
    private ImageView ivTicket;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        //Get Views by id
        tvDescription = findViewById(R.id.tvDescriptionDetails);
        tvCategory = findViewById(R.id.tvCategoryDetails);
        tvAmount = findViewById(R.id.tvAmountDetails);
        ivIcon = findViewById(R.id.ivIconDetails);
        ivTicket = findViewById(R.id.ivTicket);
        btnBack = findViewById(R.id.btnBackDetails);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        //Get register from Parceler wrap and
        register = Parcels.unwrap(getIntent().getParcelableExtra(Register.class.getSimpleName()));

        //Bind register data to Views
        tvDescription.setText(register.getDescription());
        tvCategory.setText(register.getCategory().getString("name"));
        tvAmount.setText("$" + register.getAmount());
        ParseFile fileIcon = register.getCategory().getParseFile("iconFile");
        ParseFile ticketPhoto = register.getPhoto();
        if (fileIcon != null) {
            GlideToVectorYou.init().with(this).load(Uri.parse(fileIcon.getUrl()), ivIcon);
        }
        if (ticketPhoto != null) {
            Glide.with(this).load(ticketPhoto.getUrl()).into(ivTicket);
        }
    }
}
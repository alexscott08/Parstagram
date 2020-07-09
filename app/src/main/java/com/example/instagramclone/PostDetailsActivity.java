package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class PostDetailsActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private ImageView imgView;
    private TextView descriptionTextView;
    private TextView timestampText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        usernameTextView = findViewById(R.id.usernameTextView);
        imgView = findViewById(R.id.imgView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timestampText = findViewById(R.id.timestampText);

        usernameTextView.setText(getIntent().getStringExtra("KEY_USER"));
        descriptionTextView.setText(getIntent().getStringExtra("KEY_DESCRIPTION"));
        timestampText.setText(getIntent().getStringExtra("KEY_CREATED_KEY"));
        ParseFile image = Parcels.unwrap(getIntent().getParcelableExtra("KEY_IMAGE"));
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(imgView);
        }
    }
}
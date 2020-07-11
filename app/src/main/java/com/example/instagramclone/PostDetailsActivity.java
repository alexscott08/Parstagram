package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private ImageView imgView;
    private TextView descriptionTextView;
    private TextView timestampText;
    private ImageView profileImgView;
    private ParseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        usernameTextView = findViewById(R.id.usernameTextView);
        imgView = findViewById(R.id.imgView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        timestampText = findViewById(R.id.timestampText);
        profileImgView = findViewById(R.id.profileImgView);
        user = Parcels.unwrap(getIntent().getParcelableExtra("KEY_USER"));
        usernameTextView.setText(user.getUsername());
        // Bolds the username and concats description to string after
        SpannableStringBuilder str = new SpannableStringBuilder(user.getUsername() +
                " " + getIntent().getStringExtra("KEY_DESCRIPTION"));
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                user.getUsername().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionTextView.setText(str);
        timestampText.setText(getIntent().getStringExtra("KEY_CREATED_KEY"));
        ParseFile image = Parcels.unwrap(getIntent().getParcelableExtra("KEY_IMAGE"));
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(imgView);
        }
        // Gets profile pic of user who posted
        final ParseFile profilePic = user.getParseFile("profilePic");
        if (profilePic != null) {
            GlideApp.with(this)
                    .load(profilePic.getUrl())
                    .transform(new CircleCrop())
                    .into(profileImgView);
        } else {
            GlideApp.with(this)
                    .load(R.drawable.instagram_user_filled_24)
                    .transform(new CircleCrop())
                    .into(profileImgView);
        }
    }
}
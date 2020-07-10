package com.example.instagramclone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of Post
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;
        private ImageView imgView;
        private TextView descriptionTextView;
        private TextView timestampText;
        private ImageButton heartImgBtn;
        private ImageView profileImgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            imgView = itemView.findViewById(R.id.imgView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            timestampText = itemView.findViewById(R.id.timestampText);
            heartImgBtn = itemView.findViewById(R.id.heartImgBtn);
            profileImgView = itemView.findViewById(R.id.profileImgView);
        }

        public void bind(final Post post) {
            // Bind the post data to the view elements
            descriptionTextView.setText(post.getDescription());
            usernameTextView.setText(post.getUser().getUsername());
            DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm");
            final String strDate = dateFormat.format(post.getCreatedAt());
            timestampText.setText(strDate);
            final ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(imgView);
            }
            final ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePic");
            if (profilePic != null) {
                GlideApp.with(context)
                        .load(profilePic.getUrl())
                        .transform(new CircleCrop())
                        .into(profileImgView);
            } else {
                GlideApp.with(context)
                        .load(R.drawable.instagram_user_filled_24)
                        .transform(new CircleCrop())
                        .into(profileImgView);
            }
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("KEY_DESCRIPTION", post.getDescription());
                    intent.putExtra("KEY_CREATED_KEY", strDate);
                    intent.putExtra("KEY_USER", post.getUser().getUsername());
                    intent.putExtra("KEY_IMAGE", Parcels.wrap(image));
                    context.startActivity(intent);
                }
            });
//            heartImgBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    heartImgBtn.setImageResource(R.drawable.ufi_heart_active);
//                    final ParseObject likes = new ParseObject("likes");
//                    final ParseUser user = ParseUser.getCurrentUser();
//                    likes.put("likes", post);
//                    likes.saveInBackground(new SaveCallba
        }
    }
}
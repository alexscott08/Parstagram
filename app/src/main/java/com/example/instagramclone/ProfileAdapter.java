package com.example.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;
    public static final String TAG = "ProfileAdapter";

    public ProfileAdapter(Context context, List<Post> posts) {
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
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new ProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.imgView);
        }

        public void bind(final Post post) {
            // Bind the post's img to the view
            DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm");
            final String relativeDate = DateUtils.getRelativeTimeSpanString(post.getCreatedAt().getTime()) + "";
            final ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(imgView);
            }
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("KEY_DESCRIPTION", post.getDescription());
                    intent.putExtra("KEY_CREATED_KEY", relativeDate);
                    intent.putExtra("KEY_USER", Parcels.wrap(post.getUser()));
                    intent.putExtra("KEY_IMAGE", Parcels.wrap(image));
                    context.startActivity(intent);
                }
            });
        }
    }
}

package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagramclone.EndlessRecyclerViewScrollListener;
import com.example.instagramclone.LoginActivity;
import com.example.instagramclone.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.ProfileAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private RecyclerView postsRecyclerView;
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    private EndlessRecyclerViewScrollListener scrollListener;
    SwipeRefreshLayout swipeLayout;
    private Date oldestPost;
    private Button logOutBtn;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        logOutBtn = view.findViewById(R.id.logOutBtn);
        allPosts = new ArrayList<>();
        // Create adapter
        adapter = new ProfileAdapter(getContext(), allPosts);
        // set the adapter on the recycler view
        postsRecyclerView.setAdapter(adapter);
        // set the layout manager on the recycler view
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        RecyclerView.ItemDecoration itemDecoration = new
//                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        postsRecyclerView.addItemDecoration(itemDecoration);
        postsRecyclerView.setLayoutManager(gridLayoutManager);
        queryPosts();
        // Retain an instance so that you can call `resetState()` for fresh searches
//        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                // Triggered only when new data needs to be appended to the list
//                queryNewPosts();
//            }
//        };
//        // Adds the scroll listener to RecyclerView
//        postsRecyclerView.addOnScrollListener(scrollListener);
//        // Getting SwipeContainerLayout
        swipeLayout = view.findViewById(R.id.swipeContainer);
        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeLayout.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });

        // Scheme colors for animation
        swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();
            }
        });
    }

    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                // save received posts to list and notify adapter of new data
                adapter.clear();
                adapter.addAll(posts);
                if (adapter.getItemCount() > 0) {
                    oldestPost = posts.get(posts.size() - 1).getCreatedAt();
                }
            }
        });
    }

    // To append new data with endless scroll listener
    protected void queryNewPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date and ensures only old posts not currently shown are added
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()).
                whereGreaterThan("createdAt", oldestPost).orderByDescending(Post.KEY_CREATED_KEY);

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                // update adapter with posts list
                adapter.clear();
                adapter.addAll(posts);
                if (adapter.getItemCount() > 0) {
                    oldestPost = posts.get(posts.size() - 1).getCreatedAt();
                }
            }
        });
    }
    private void logOutUser() {
        Log.i(TAG, "Attempting to signout user");
        // Navigates to login activity if logout is successful
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(getContext(), "Issue with logout!", Toast.LENGTH_SHORT).show();
                    return;
                }
                goLoginActivity();
                Toast.makeText(getContext(), "You have successfully logged out!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        getContext().startActivity(intent);
    }

}

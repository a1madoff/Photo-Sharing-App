package com.example.instagramclone.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Comment;
import com.example.instagramclone.CommentsAdapter;
import com.example.instagramclone.ParseRelativeDate;
import com.example.instagramclone.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsFragment extends Fragment {
    public static final String TAG = "PostDetailsFragment";
    public static final int QUERY_LIMIT = 20;

    private Post post;
    protected CommentsAdapter adapter;
    protected List<Comment> allComments;

    private TextView tvUsername;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvCreatedAt;
    private ImageView ivProfPhoto;
    private RecyclerView rvComments;
    private EditText etComment;
    private Button btnComment;
    private TextView tvNumLikes;
    private Button btnLike;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    public PostDetailsFragment(Post post) {
        this.post = post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvUsername = view.findViewById(R.id.tvUsername);
        ivImage = view.findViewById(R.id.ivImage);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvCreatedAt = view.findViewById(R.id.tvCreatedAt);
        ivProfPhoto = view.findViewById(R.id.ivProfPhoto);
        rvComments = view.findViewById(R.id.rvComments);
        etComment = view.findViewById(R.id.etComment);
        btnComment = view.findViewById(R.id.btnComment);
        tvNumLikes = view.findViewById(R.id.tvNumLikes);
        btnLike = view.findViewById(R.id.btnLike);

        tvDescription.setText(post.getDescription());
        tvUsername.setText(post.getUser().getUsername());
        tvCreatedAt.setText(ParseRelativeDate.getRelativeTimeAgo(post.getCreatedAt().toString()));
        tvNumLikes.setText(String.format("%s likes", String.valueOf(post.getNumLikes())));

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: change so that checks to see if post is already likes
                int newNumLikes = post.getNumLikes() + 1;
                post.setNumLikes(newNumLikes);
                post.saveInBackground();
                tvNumLikes.setText(String.format("%s likes", String.valueOf(newNumLikes)));
            }
        });

        ivProfPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment userFragment = new GeneralUserProfileFragment(post.getUser());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, userFragment).commit();
            }
        });

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment userFragment = new GeneralUserProfileFragment(post.getUser());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, userFragment).commit();
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = etComment.getText().toString();

                Comment comment = new Comment();
                comment.setText(commentText);
                comment.setPost(post);
                comment.setCommenter(ParseUser.getCurrentUser());

                comment.saveInBackground();
                allComments.add(0, comment);
                adapter.notifyItemInserted(0);
                rvComments.smoothScrollToPosition(0);
            }
        });

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(getContext())
                    .load(post.getImage().getUrl())
                    .into(ivImage);
        }

        ParseFile profFile = post.getUser().getParseFile("profilePicture");
        if (profFile != null) {
            Glide.with(getContext())
                    .load(profFile.getUrl())
                    .circleCrop()
                    .into(ivProfPhoto);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.general_prof)
                    .into(ivProfPhoto);
        }

        allComments = new ArrayList<>();
        adapter = new CommentsAdapter(getContext(), allComments);
        rvComments.setAdapter(adapter);
        // Set layout manager to position the items
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        getAllComments();
    }

    private void getAllComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_POST);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                for (Comment comment: comments) {
                    Log.i(TAG, "Comment: " + comment.getText());
                }
                adapter.addAll(comments);
            }
        });
    }
}

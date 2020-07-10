package com.example.instagramclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.instagramclone.ParseRelativeDate;
import com.example.instagramclone.Post;
import com.example.instagramclone.R;
import com.parse.ParseFile;

public class PostDetailsFragment extends Fragment {
    public static final String TAG = "PostDetailsFragment";

    private Post post;

    private TextView tvUsername;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvCreatedAt;
    private ImageView ivProfPhoto;

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

        tvDescription.setText(post.getDescription());
        tvUsername.setText(post.getUser().getUsername());
        tvCreatedAt.setText(ParseRelativeDate.getRelativeTimeAgo(post.getCreatedAt().toString()));

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
    }
}

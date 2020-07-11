package com.example.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.fragments.GeneralUserProfileFragment;
import com.example.instagramclone.fragments.PostDetailsFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String TAG = "PostsAdapter";

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
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

    // Cleans all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Adds the list of items
    public void addAll(List<Post> Postslist) {
        posts.addAll(Postslist);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvCreatedAt;
        private ImageView ivProfPhoto;
        private TextView tvBottomUsername;
        private TextView tvNumLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivProfPhoto = itemView.findViewById(R.id.ivProfPhoto);
            tvBottomUsername = itemView.findViewById(R.id.tvBottomUsername);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);

//            ivProfPhoto.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                    Fragment userFragment = new GeneralUserProfileFragment(posts.get(getAdapterPosition()).getUser());
//                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, userFragment).commit();
//                }
//            });
            itemView.setOnClickListener(this);
        }

        public void bind(final Post post) {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvCreatedAt.setText(ParseRelativeDate.getRelativeTimeAgo(post.getCreatedAt().toString()));
            tvBottomUsername.setText(post.getUser().getUsername());
            tvNumLikes.setText(String.format("%s likes", String.valueOf(post.getNumLikes())));

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
                Glide.with(context)
                        .load(post.getImage().getUrl())
                        .into(ivImage);
            }

            ParseFile profFile = post.getUser().getParseFile("profilePicture");
            if (profFile != null) {
                Glide.with(context)
                        .load(profFile.getUrl())
                        .circleCrop()
                        .into(ivProfPhoto);
            } else {
                Glide.with(context)
                        .load(R.drawable.general_prof)
                        .into(ivProfPhoto);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post currentPost = posts.get(position);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment detailsFragment = new PostDetailsFragment(currentPost);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, detailsFragment).commit();
            }
        }
    }
}

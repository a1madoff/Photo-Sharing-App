package com.example.instagramclone;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.fragments.GeneralUserProfileFragment;
import com.example.instagramclone.fragments.PostDetailsFragment;
import com.parse.ParseFile;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public ProfilePostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_post, parent, false);
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

        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);

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
            tvCreatedAt.setText(ParseRelativeDate.getRelativeTimeAgo(post.getCreatedAt().toString()));

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(post.getImage().getUrl())
                        .into(ivImage);
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

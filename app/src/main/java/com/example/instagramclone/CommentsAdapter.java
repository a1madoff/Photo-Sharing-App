package com.example.instagramclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // Adds the list of items
    public void addAll(List<Comment> commentsList) {
        comments.addAll(commentsList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfPhoto;
        private TextView tvUsername;
        private TextView tvComment;
        private TextView tvCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfPhoto = itemView.findViewById(R.id.ivProfPhoto);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }

        public void bind(Comment comment) {
            tvComment.setText(comment.getText());
            try {
                tvUsername.setText(comment.getCommenter().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                tvCreatedAt.setText(ParseRelativeDate.getRelativeTimeAgo(comment.fetch().getCreatedAt().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ParseFile profFile = comment.getCommenter().getParseFile("profilePicture");
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
    }
}

package com.example.instagramclone.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.instagramclone.EndlessRecyclerViewScrollListener;
import com.example.instagramclone.LoginActivity;
import com.example.instagramclone.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.ProfilePostsAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public static final int PROFILE_IMAGE_REQUEST_CODE = 9;
    public static final int QUERY_LIMIT = 20;

    private File photoFile;
    private String photoFileName = "profilephoto.jpg";

    private Button btnLogout;
    private ImageView ivProfPhoto;
    private RecyclerView rvPosts;
    private TextView tvUsername;

    protected ProfilePostsAdapter adapter;
    protected List<Post> allPosts;
    private EndlessRecyclerViewScrollListener scrollListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnLogout = view.findViewById(R.id.btnLogout);
        ivProfPhoto = view.findViewById(R.id.ivProfPhoto);
        rvPosts = view.findViewById(R.id.rvPosts);
        tvUsername = view.findViewById(R.id.tvProfileUsername);

        allPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvPosts.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore");
                loadMorePosts();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        ivProfPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();

                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.put("profilePicture", new ParseFile(photoFile));
                currentUser.saveInBackground();
                Log.i(TAG, "Uploaded profile photo");
            }
        });

        tvUsername.setText(ParseUser.getCurrentUser().getUsername());

        ParseFile profFile = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (profFile == null) {
            Glide.with(getContext())
                    .load(R.drawable.no_prof_pic)
                    .into(ivProfPhoto);
        } else {
            Glide.with(getContext())
                    .load(profFile.getUrl())
                    .circleCrop()
                    .into(ivProfPhoto);
        }

        queryPosts();
    }

    private void launchCamera() {
        // Creates Intent to take a picture and return control to the application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Creates File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // Wraps File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Starts the image capture intent to take photo
            startActivityForResult(intent, PROFILE_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Camera photo is on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Loads the taken image into a preview
                Glide.with(getContext())
                        .load(takenImage)
                        .circleCrop()
                        .into(ivProfPhoto);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Gets safe storage directory for photos
        // Uses `getExternalFilesDir` on Context to access package-specific directories
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Creates the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Returns the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    protected void queryPosts() {
        // Specifies which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                for (Post post: posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadMorePosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setSkip(allPosts.size());
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                for (Post post: posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                adapter.addAll(posts);
            }
        });
    }
}

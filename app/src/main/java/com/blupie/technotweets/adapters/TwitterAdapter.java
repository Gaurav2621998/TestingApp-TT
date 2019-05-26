package com.blupie.technotweets.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blupie.technotweets.fragments.BlankFragment;
import com.blupie.technotweets.fragments.fragment_my_post;
import com.blupie.technotweets.models.TimeAgo;
import com.blupie.technotweets.models.User;
import com.bumptech.glide.Glide;
import com.blupie.technotweets.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.ViewHolder> {
    Context context;
    List<User> userList;
    ImageView postimg;

    StorageReference ref = FirebaseStorage.getInstance().getReference().child("images");
    StorageReference reference = FirebaseStorage.getInstance().getReference();

    public TwitterAdapter(Context context, List<User> user) {
        this.context = context;
        this.userList = user;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardfornews, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        try {
            holder.postimg.setVisibility(View.GONE);
            final User user = userList.get(i);
            String t = user.getPosttime();
            holder.name.setText(user.getName());

            Glide.with(context.getApplicationContext()).load(user.getProfileimage()).into(holder.profileimg);
            final String postimage = user.getPostimage();
            if ((postimage == null)) {
                holder.postimg.setVisibility(View.GONE);
            } else if ((postimage.equals("0"))) {
                holder.postimg.setVisibility(View.GONE);
            } else {
                reference = ref.child(user.getPostimage());
                holder.postimg.setVisibility(View.VISIBLE);
                //Glide.with(context.getApplicationContext()).load(user.getPostimage()).into(holder.postimg);
                Glide.with(context.getApplicationContext()).using(new FirebaseImageLoader()).load(reference).into(holder.postimg);
            }
            holder.description.setText(user.getPostdata());

            if (t != null) {
                String time = TimeAgo.getTimeAgo(Long.parseLong(t));
                holder.date.setText(time);
            } else {
                holder.date.setText("");
            }
            holder.postimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new BlankFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("imageurl", user.getPostimage());
                    myFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.newRelative, myFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new fragment_my_post();
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", user.getUserid());
                    myFragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.newRelative, myFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Exception Raised "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profileimg)
        CircleImageView profileimg;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.postimg)
        ImageView postimg;
        @BindView(R.id.image_relative)
        RelativeLayout layout;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}


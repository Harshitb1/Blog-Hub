package com.example.sagar.bloghub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.String.format;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    List<BlogPost> list;
    Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    BlogRecyclerAdapter(List<BlogPost> list){
        this.list=list;
    }

    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context= parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogRecyclerAdapter.ViewHolder holder, int position) {

        final String blogPostId = list.get(position).blogPostId;
        final String currentUserId=firebaseAuth.getCurrentUser().getUid();

        String descData= list.get(position).getDesc();
        holder.setDescText(descData);

        final String image_url = list.get(position).getImage_url();
        String thumb_uri = list.get(position).getImage_thumb();
        holder.setBlogImage(image_url,thumb_uri);


        String userId= list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                    String userName= task.getResult().getString("name");
                    String userImage= task.getResult().getString("image");
                    holder.setUserName(userName);
                    holder.setImage(userImage);
                }else{

                    String error= task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        long milliseconds =list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> likeMap = new HashMap<>();
                likeMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).set(likeMap);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView descTextView;
        View view;
        TextView dateTextView;
        ImageView blogImageView;
        TextView usernameTextView;
        CircleImageView circleImageView;
        ImageView likeImageView;
        TextView likeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            view= itemView;
            likeImageView= view.findViewById(R.id.likesImageView);

        }

        public void setDescText(String text){
            descTextView=view.findViewById(R.id.postDescTextView);
            descTextView.setText(text);
        }

        public void setBlogImage(String downloadUri,String thuhmbUri){

            blogImageView=view.findViewById(R.id.postImageView);
            Glide.with(context).load(downloadUri).thumbnail(Glide.with(context).load(thuhmbUri)).into(blogImageView);
        }

        public void  setTime(String date){

            dateTextView=view.findViewById(R.id.timeTextView);
            dateTextView.setText(date);
        }

        public void setUserName(String name){

            usernameTextView=view.findViewById(R.id.usernameTextView);
            usernameTextView.setText(name);
        }

        public void setImage(String image){
            circleImageView= view.findViewById(R.id.profileImageView);
            Glide.with(context).load(image).into(circleImageView);
        }
    }
}

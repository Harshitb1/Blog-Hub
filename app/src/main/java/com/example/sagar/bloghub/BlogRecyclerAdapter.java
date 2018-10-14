package com.example.sagar.bloghub;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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

        if(firebaseAuth==null){

        }
        else {
            holder.setIsRecyclable(false);

            final String blogPostId = list.get(position).blogPostId;
            final String currentUserId = firebaseAuth.getCurrentUser().getUid();

            String descData = list.get(position).getDesc();
            holder.setDescText(descData);

            final String image_url = list.get(position).getImage_url();
            String thumb_uri = list.get(position).getImage_thumb();
            holder.setBlogImage(image_url, thumb_uri);


            final String userId = list.get(position).getUser_id();
            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        // Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                        String userName = task.getResult().getString("name");
                        String userImage = task.getResult().getString("image");
                        holder.setUserName(userName);
                        holder.setImage(userImage);
                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            try {
                long milliseconds = list.get(position).getTimestamp().getTime();
                String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
                holder.setTime(dateString);
            } catch (Exception e) {
//            Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()) {
                        holder.updateLikeCount(documentSnapshots.size());
                    } else {
                        holder.updateLikeCount(0);
                    }
                }
            });

            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()) {
                        holder.updateCommentCount(documentSnapshots.size());
                    } else {
                        holder.updateCommentCount(0);
                    }
                }
            });
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        if (documentSnapshot.exists()) {
                            holder.likeImageView.setImageDrawable(context.getDrawable(R.mipmap.likeenabled));

                        } else {
                            holder.likeImageView.setImageDrawable(context.getDrawable(R.mipmap.likedisabled));

                        }
                    } else {

                    }
                }
            });

            holder.likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (!task.getResult().exists()) {
                                Map<String, Object> likeMap = new HashMap<>();
                                likeMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likeMap);

                            } else {
                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                            }
                        }
                    });

                }
            });

            holder.commentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra("blogPostid", blogPostId);
                    context.startActivity(intent);
                }
            });
        }
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
        ImageView commentImageView;
        TextView commentCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            view= itemView;
            likeImageView= view.findViewById(R.id.likesImageView);
            commentImageView=view.findViewById(R.id.CommentButtonImageView);

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

        public void updateLikeCount(int count){
            likeTextView=view.findViewById(R.id.likesTextView);
            likeTextView.setText(count+"");
        }
        public void updateCommentCount(int count){
            commentCountTextView=view.findViewById(R.id.CommentCountTextView);
            commentCountTextView.setText(count+"");
        }
    }
}

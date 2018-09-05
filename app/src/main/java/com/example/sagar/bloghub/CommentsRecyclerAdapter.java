package com.example.sagar.bloghub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentsRecyclerAdapter  extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comment> commentsList;
    public Context context;
    FirebaseFirestore firebaseFirestore;

    public CommentsRecyclerAdapter(List<Comment> commentsList){

        this.commentsList = commentsList;

    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore= FirebaseFirestore.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Comment comment = commentsList.get(position);
//        if(comment.getUserId()==null){
//            Log.d("pathCheck","null");
//        }
//       else{
//            Log.d("pathCheck",comment.getUserId());
//        }
        holder.setComment(comment);

    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView comment_username;
        private TextView comment_message;
        private ImageView comment_user_image;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            comment_username= mView.findViewById(R.id.comment_username);
            comment_user_image=mView.findViewById(R.id.comment_image);
        }

       public void setComment(Comment comment){
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(comment.getMessage());
//            Log.d("pathCheck",comment.getUserId());
            firebaseFirestore.collection("Users").document(comment.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        // Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                        String userName= task.getResult().getString("name");
                        String userImage= task.getResult().getString("image");

                        Log.d("commentcheck",userName);
                        Log.d("commentcheck",userImage);
                        comment_username.setText(userName);
                        Glide.with(context).load(userImage).into(comment_user_image);
                    }else{

                        String error= task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

}
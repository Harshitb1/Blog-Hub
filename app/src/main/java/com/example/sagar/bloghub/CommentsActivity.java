package com.example.sagar.bloghub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText commentEditText;
    ImageView saveComment;
    String blog_post_id;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    RecyclerView commentList;
    CommentsRecyclerAdapter adapter;
    List<Comment> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        toolbar= findViewById(R.id.commentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        blog_post_id= getIntent().getStringExtra("blogPostid");
        currentUserId= firebaseAuth.getCurrentUser().getUid();

        commentEditText= findViewById(R.id.CommentEditText);
        saveComment= findViewById(R.id.CommentImageView);

        commentList=findViewById(R.id.CommentRecyclerView);

        list = new ArrayList<>();
        adapter = new CommentsRecyclerAdapter(list);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setAdapter(adapter);

        firebaseFirestore.collection("Posts/"+blog_post_id+"/Comments").addSnapshotListener(CommentsActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String commentId= doc.getDocument().getId();

                        Comment comments = doc.getDocument().toObject(Comment.class);
                        list.add(comments);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });

        saveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentMessage = commentEditText.getText().toString();
                if(!commentMessage.isEmpty()){

                    Map<String , Object> commentMap =new HashMap<>();
                    commentMap.put("message",commentMessage);
                    commentMap.put("user_id",currentUserId);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts/"+blog_post_id+"/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(!task.isSuccessful()){

                                Toast.makeText(CommentsActivity.this,"Error Posting Comment"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }else{
                                commentEditText.setText("");
                            }
                        }
                    });
                }
            }
        });
    }
}

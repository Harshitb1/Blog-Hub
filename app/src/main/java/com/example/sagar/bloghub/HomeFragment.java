package com.example.sagar.bloghub;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    RecyclerView recyclerView;
    List<BlogPost> blogList;
    FirebaseFirestore firebaseFirestore;
    BlogRecyclerAdapter adapter;
    FirebaseAuth firebaseAuth;
    DocumentSnapshot lastVisible;
    Boolean isFirstPageLoadedFirst=true;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        blogList = new ArrayList<>();
        recyclerView= view.findViewById(R.id.postRecyclerView);
        adapter= new BlogRecyclerAdapter(blogList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        firebaseAuth= FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null ){
            firebaseFirestore = FirebaseFirestore.getInstance();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean isReachedBottom = recyclerView.canScrollVertically(1);

                    if(!isReachedBottom){
                       //Toast.makeText(getContext(),"Pagination",Toast.LENGTH_SHORT).show();
                        loadMorePost();
                    }
                }
            });
            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(4);
            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if(isFirstPageLoadedFirst) {
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    }
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId= doc.getDocument().getId();

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            if(isFirstPageLoadedFirst) {
                                blogList.add(blogPost);
                            }else {
                                blogList.add(0,blogPost);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageLoadedFirst=false;
                }
            });
        }
        return view;
    }

    public void loadMorePost(){

        firebaseFirestore = FirebaseFirestore.getInstance();

        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(4);

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()) {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId= doc.getDocument().getId();

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blogList.add(blogPost);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}

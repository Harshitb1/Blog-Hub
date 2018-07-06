package com.example.sagar.bloghub;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {

    @Exclude
    public String blogPostId;
    public <T extends BlogPostId> T withId(@NonNull final String id){
        this.blogPostId=id;
        return (T) this;
    }
}

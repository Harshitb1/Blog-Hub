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
import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import static java.lang.String.format;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    List<BlogPost> list;
    Context context;

    BlogRecyclerAdapter(List<BlogPost> list){
        this.list=list;
    }

    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context= parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogRecyclerAdapter.ViewHolder holder, int position) {

        String descData= list.get(position).getDesc();
        holder.setDescText(descData);

        String image_url = list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        long milliseconds =list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);
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

        public ViewHolder(View itemView) {
            super(itemView);
            view= itemView;
        }

        public void setDescText(String text){
            descTextView=view.findViewById(R.id.postDescTextView);
            descTextView.setText(text);
        }

        public void setBlogImage(String downloadUri){

            blogImageView=view.findViewById(R.id.postImageView);
            Glide.with(context).load(downloadUri).into(blogImageView);
        }

        public void  setTime(String date){

            dateTextView=view.findViewById(R.id.timeTextView);
            dateTextView.setText(date);
        }
    }
}

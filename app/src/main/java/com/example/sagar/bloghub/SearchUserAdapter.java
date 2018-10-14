package com.example.sagar.bloghub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<User> mUsers;

    public SearchUserAdapter(Context context, ArrayList<User> users){
        mContext = context;
        mUsers = users;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View output = convertView;
        if(output == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            output = inflater.inflate(R.layout.row_user,parent,false);
            ViewHolder holder = new ViewHolder();
            holder.avatar = output.findViewById(R.id.avatar);
            holder.username = output.findViewById(R.id.username);
            output.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) output.getTag();
        User user = mUsers.get(position);
        holder.username.setText(user.getName());
        Glide.with(mContext).load(user.getImage()).into(holder.avatar);
        return output;
    }

    class ViewHolder {

        CircleImageView avatar;
        TextView username;

    }
}

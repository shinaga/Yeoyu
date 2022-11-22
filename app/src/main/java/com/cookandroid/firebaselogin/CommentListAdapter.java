package com.cookandroid.firebaselogin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private ArrayList<Comment> commentList;

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_borad_recyclerview, parent, false);
        return new CommentListAdapter.ViewHolder(view);//CommentListAdapter. 없어도 됨
    }
    public void setCommentList(ArrayList<Comment> list){
        this.commentList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView number;
        TextView nickname;
        TextView date;
        TextView context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number = (TextView) itemView.findViewById(R.id.number);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            date = (TextView) itemView.findViewById(R.id.date);
            context = (TextView) itemView.findViewById(R.id.context);

        }

        void onBind(Comment item) {
            number.setText(item.getNumber()+"");
            nickname.setText(item.getNickname());
            date.setText(item.getDate());
            context.setText(item.getContext());
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(commentList.get(position));
    }
}

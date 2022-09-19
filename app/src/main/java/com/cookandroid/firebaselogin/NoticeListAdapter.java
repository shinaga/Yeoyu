package com.cookandroid.firebaselogin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder> {

    private ArrayList<Notice> noticeList;

    @NonNull
    @Override
    public NoticeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_borad_recyclerview, parent, false);
        return new NoticeListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeListAdapter.ViewHolder holder, int position) {
        holder.onBind(noticeList.get(position));
    }

    public void setNoticeList(ArrayList<Notice> list){
        this.noticeList = list;
        notifyDataSetChanged();
    }
    public void addNoticeList(ArrayList<Notice> list){
        for(Notice item : list){
            noticeList.add(item);
        }
        notifyItemRangeInserted(getItemCount(),list.size());
    }
    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView number;
        TextView title;
        TextView date;
        TextView context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number = (TextView) itemView.findViewById(R.id.number);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            context = (TextView) itemView.findViewById(R.id.context);
        }

        void onBind(Notice item) {
            number.setText(item.getNumber()+"");
            title.setText(item.getTitle());
            date.setText(item.getDate());
            context.setText(item.getContext());
        }
    }
}
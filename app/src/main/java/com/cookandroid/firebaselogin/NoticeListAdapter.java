package com.cookandroid.firebaselogin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder> {

    private ArrayList<Notice> noticeList;

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    @NonNull
    @Override
    public NoticeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_borad_recyclerview, parent, false);
        return new NoticeListAdapter.ViewHolder(view);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView number;//프론트에선 안보임
        TextView title;
        TextView date;
        TextView context;
        TextView hearth_count;
        TextView comment_count;
        ImageView hearth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number = (TextView) itemView.findViewById(R.id.number);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            context = (TextView) itemView.findViewById(R.id.context);
            hearth_count = (TextView) itemView.findViewById(R.id.hearth_count);
            comment_count = (TextView) itemView.findViewById(R.id.comment_count);
            hearth = (ImageView) itemView.findViewById(R.id.hearth);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),NoticeviewActivity.class);
                    intent.putExtra("number",number.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }

        void onBind(Notice item) {
            number.setText(item.getNumber()+"");
            title.setText(item.getTitle());
            date.setText(item.getDate());
            context.setText(item.getContext());
            hearth_count.setText(item.getHtCnt()+"");
            comment_count.setText(item.getCmtCnt()+"");
            if(item.getIsCheck()==false){
                hearth.setImageResource(R.drawable.hearth);
            }
            else hearth.setImageResource(R.drawable.hearth_check);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull NoticeListAdapter.ViewHolder holder, int position) {
        holder.onBind(noticeList.get(position));
    }
}
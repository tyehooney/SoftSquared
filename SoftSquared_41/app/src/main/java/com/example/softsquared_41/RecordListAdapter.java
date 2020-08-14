package com.example.softsquared_41;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Record> records;

    public RecordListAdapter(Context context, List<Record> recordList){
        this.mContext = context;
        this.records = recordList;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int i) {
        return records.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Record record = records.get(i);

        RecordViewHolder holder;

        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_record_list, viewGroup, false);

            holder = new RecordViewHolder();

            holder.tv_rank = view.findViewById(R.id.textView_item_rank);
            holder.tv_name = view.findViewById(R.id.textView_item_name);
            holder.tv_level = view.findViewById(R.id.textView_item_level);
            holder.tv_time = view.findViewById(R.id.textView_item_time);

            view.setTag(holder);
        }else{
            holder = (RecordViewHolder) view.getTag();
        }

        holder.tv_rank.setText(""+(i+1));
        holder.tv_name.setText(record.getName());
        holder.tv_level.setText(""+record.getLevel());
        holder.tv_time.setText(record.getTime());

        return view;
    }

    public static class RecordViewHolder{
        private TextView tv_rank, tv_name, tv_level, tv_time;
    }
}

package com.blupie.technotweets.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blupie.technotweets.R;
import com.blupie.technotweets.models.Schedule;

import java.util.List;

public class Schedule_Adapter extends RecyclerView.Adapter<Schedule_Adapter.ViewHolder> {


    List<Schedule>scheduleList;
    Context context;

    public Schedule_Adapter(Context context,List<Schedule>list) {
        this.context=context;
        this.scheduleList=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ttitem,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.time.setText(scheduleList.get(position).getTime());
        String temp=scheduleList.get(position).getSubject()+"\n"+"("+scheduleList.get(position).getFaculty()+")";
        holder.subfac.setText(temp);

    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView time,subfac;


        public ViewHolder(View itemView) {
            super(itemView);

            time=(TextView)itemView.findViewById(R.id.time);
            subfac=(TextView)itemView.findViewById(R.id.subfac);
        }
    }
}

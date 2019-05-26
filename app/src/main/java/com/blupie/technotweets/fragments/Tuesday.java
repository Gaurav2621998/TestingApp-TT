package com.blupie.technotweets.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blupie.technotweets.R;
import com.blupie.technotweets.adapters.Schedule_Adapter;
import com.blupie.technotweets.models.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blupie.technotweets.fragments.TimeTable.deptsem;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tuesday extends Fragment {


    public Tuesday() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    List<Schedule> schedules=new ArrayList<>();

    Schedule_Adapter adapter;

    DatabaseReference mydef= FirebaseDatabase.getInstance().getReference().child("root")
            .child("timetable").child("branch");




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tuesday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=(RecyclerView)view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mydef= mydef.child(deptsem).child("Days").child("Tuesday");
        mydef.keepSynced(true);


        mydef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                schedules.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Schedule schedule=snapshot.getValue(Schedule.class);
                    schedules.add(schedule);

                }
                adapter=new Schedule_Adapter(getContext(),schedules);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

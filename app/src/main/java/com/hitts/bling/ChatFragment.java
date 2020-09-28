package com.hitts.bling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerViewAdapterChat recyclerViewAdapter;
    private List<User> mUser;
    private List<String> chatlist;
    ProgressBar progressBar;

    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container, false);
        recyclerView = view.findViewById(R.id.recyclerviewchat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progressbarchat);
        progressBar.setVisibility(View.VISIBLE);
        chatlist = new ArrayList<>();
        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getSender().equals(firebaseUser.getUid()))
                    {
                        //if(!chatlist.contains(chat.getReceiver()))
                        chatlist.remove(chat.getReceiver());
                        chatlist.add(chat.getReceiver());
                    }
                    else if (chat.getReceiver().equals(firebaseUser.getUid()))
                    {
                        //if(!chatlist.contains(chat.getSender()))
                        chatlist.remove(chat.getSender());
                        chatlist.add(chat.getSender());
                    }
                }
                readuser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    public void readuser()
    {
        mUser = new ArrayList<>();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (String id : chatlist)
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (user.getId().equals(id))
                        {
                            mUser.add( 0,user);
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                recyclerViewAdapter = new RecyclerViewAdapterChat(getContext(),mUser);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

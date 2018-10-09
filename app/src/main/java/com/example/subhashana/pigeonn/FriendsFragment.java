package com.example.subhashana.pigeonn;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.UserRecoverableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    private RecyclerView myFriendsList;

    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;
    private FirebaseAuth mAuth;

    String online_user_id;

    private View myMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        myFriendsList = (RecyclerView) myMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();

        online_user_id =  mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                (
                        Friends.class,
                        R.layout.all_users_display_layout,
                        FriendsViewHolder.class,
                        FriendsReference
                ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                viewHolder.setDate(model.getDate());

                String list_user_id = getRef(position).getKey();

                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();


                        if (dataSnapshot.hasChild("online")){
                            Boolean online_status = (Boolean) dataSnapshot.child("online").getValue();

                            viewHolder.setUserOnline(online_status);
                        }


                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage, getContext());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        myFriendsList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setThumbImage(final String thumbImage, final Context ctx) {

            final CircleImageView thumb_image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);



            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile_image)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.default_profile_image).into(thumb_image);

                        }
                    });


        }

        public void setDate(String date){
            TextView sinceFriendsDate = (TextView) mView.findViewById(R.id.all_users_status);
            sinceFriendsDate.setText(date);
        }

        public void setUserName(String userName){

            TextView userNameDisplay = (TextView) mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }

        public void setUserOnline(Boolean online_status) {

            ImageView onlineStatusView = (ImageView) mView.findViewById(R.id.online_status);

            if (online_status == true){

                onlineStatusView.setVisibility(View.VISIBLE);
            }
            else{
                onlineStatusView.setVisibility(View.INVISIBLE);
            }


        }
    }
}

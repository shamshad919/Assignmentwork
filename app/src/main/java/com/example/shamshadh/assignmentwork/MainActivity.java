package com.example.shamshadh.assignmentwork;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    FirebaseRecyclerAdapter<postdetails,postviewohlder> firebaseRecyclerAdapter;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        floatingActionButton=findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final DatabaseReference mRef=FirebaseDatabase.getInstance().getReference("post");

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<postdetails, postviewohlder>(postdetails.class,
                R.layout.listrow,
                postviewohlder.class,
                mRef) {
            @Override
            protected void populateViewHolder(final postviewohlder viewHolder, postdetails model, final int position) {
                viewHolder.textView.setText(model.text);
                Glide.with(MainActivity.this).load(model.image).into(viewHolder.imageView);
                viewHolder.count.setText(model.like);
                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     final DatabaseReference cRef=FirebaseDatabase.getInstance().getReference("post").child(firebaseRecyclerAdapter.getRef(position).getKey());
                     cRef.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             String count= (String) dataSnapshot.child("like").getValue();
                             int n=Integer.parseInt(count);
                             n++;
                             cRef.child("like").setValue(String.valueOf(n));
                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {

                         }
                     });
                    }
                });

            }
        };
     recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onClick(View v) {
        if(v==floatingActionButton){
            startActivity(new Intent(MainActivity.this,add_activity.class));
        }

    }

    public static  class postdetails{
        String image;
        String text;
        String like;

        public postdetails() {
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }
    }

    public static class postviewohlder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        TextView count;
        ImageView like;
        public postviewohlder(View itemView) {
            super(itemView);
            like=itemView.findViewById(R.id.likeButton);
            textView=itemView.findViewById(R.id.text);
            imageView=itemView.findViewById(R.id.image);
            count=itemView.findViewById(R.id.likeCount);
        }
    }
}

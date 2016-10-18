package firebaseapps.com.blogging;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mLonglist;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthstatelistener;
    private DatabaseReference likes;
    private boolean like=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        //That checks if the user is logged in
       mAuthstatelistener=new FirebaseAuth.AuthStateListener() {
           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if(firebaseAuth.getCurrentUser()==null)
               {
                   //If no user logged in the user is taken to the login activity so that user can log in
                   Intent signingintent=new Intent(MainActivity.this,LoginActivity.class);
                   signingintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //User won't be able to go back
                   startActivity(signingintent);
                   finish();
               }
           }
       };

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");//Checks the blogs
        likes=FirebaseDatabase.getInstance().getReference().child("Likes");     //Checks the likes
        mDatabase.keepSynced(true);
        likes.keepSynced(true );
        mLonglist=(RecyclerView)findViewById(R.id.bloglist);
        mLonglist.setHasFixedSize(true);
        mLonglist.setLayoutManager(new LinearLayoutManager(this));




    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder
    {
        //These member help the views in recylcer view clickable
        View mview;
        String likers=null;
        ImageButton imageButton;

        DatabaseReference likess;
        DatabaseReference post;
        FirebaseAuth mAuthh;
        TextView liker;
        String tmp;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mview=itemView;


            imageButton=(ImageButton) mview.findViewById(R.id.like_bttn);
            likess=FirebaseDatabase.getInstance().getReference().child("Likes");
            post=FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuthh=FirebaseAuth.getInstance();
            liker=(TextView) mview.findViewById(R.id.likers);

        }
        public  void setLikers(String posts)
        {
            DatabaseReference Poste;

            Poste=post.child(posts);

            Poste.keepSynced(true);



            Poste.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    likers=liker.getText().toString();

                    if(likers.startsWith(","))
                    {
                       likers= likers.replaceFirst(",","");
                    }


                    liker.setText(likers+","+dataSnapshot.getValue().toString());



                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                  //  likers=liker.getText().toString();

                    if(likers.contains(dataSnapshot.getValue().toString()))
                    {
                       likers= likers.replaceAll(dataSnapshot.getValue().toString(),"");
                    }

                    liker.setText(likers);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setText(String title )
        {
           TextView post_title=(TextView) mview.findViewById(R.id.title);
            post_title.setText(title);
        }
        public void setDesc(String desc)
        {
            TextView setdesc=(TextView) mview.findViewById(R.id.descript);
            setdesc.setText(desc);
        }
        public void setUser(String user)
        {
            TextView Userplace=(TextView) mview.findViewById(R.id.userName);
                    Userplace.setText(user);
        }
        public void setProfile(final Context ctx,final String image)
        {
            final ImageView post_image=(ImageView)mview.findViewById(R.id.profile);
            // Picasso.with(ctx).load(image).into(post_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(image).into(post_image);

                 }
            });
        }

        public void setImage(final Context ctx, final String image)
        {
            final ImageView post_image=(ImageView)mview.findViewById(R.id.postimage);
           // Picasso.with(ctx).load(image).into(post_image);

            //Check for the image in local cache first if not present then retrives from the database in firebase storage
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(image).into(post_image);

                }
            });

        }
        public void setlikebtn(final String post_key)
        {
            likess.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuthh.getCurrentUser().getUid())) //THIS IS LINE 203
                    {
                        //If already liked the this image is set or else thumb
                        imageButton.setImageResource(R.mipmap.ic_like);
                    }
                    else
                    {

                        imageButton.setImageResource(R.mipmap.ic_thumb_up_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthstatelistener);

        //Takes the data from "mDatabase" and converts the data into object of Blog class's object and puts it in
        //layout of blog-row and the last retrived data are stored in object of BlogViewHolder class

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String Post_key=getRef(position).getKey();

                //Populates the view  with the data to display

                viewHolder.setText(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setUser(model.getUser());
                viewHolder.setImage(getApplicationContext(),model.getImageurl());
                viewHolder.setProfile(getApplicationContext(),model.getProfilepic());
                viewHolder.setlikebtn(Post_key);
               viewHolder.setLikers(Post_key);


                viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        like=true;

                            likes.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                 //If like is present then heart image is displayed else thumb is used and on click one another is sent
                                 if (like) {


                                     if (dataSnapshot.child(Post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                         likes.child(Post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                         like = false;
                                     } else {
                                         likes.child(Post_key).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getEmail());
                                         like = false;
                                     }

                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });



                    }
                });

            }
        };
        mLonglist.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,Postactivity.class));
        }
        if(item.getItemId()==R.id.action_signout)
        {
            mAuth.signOut();
        }


        return super.onOptionsItemSelected(item);
    }
}

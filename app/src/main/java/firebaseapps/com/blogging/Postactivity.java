package firebaseapps.com.blogging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**This activity allows the signed in user to post a new post he must upload  apicture
 * and write some description ad give it a title
 */


public class Postactivity extends AppCompatActivity {


    private ImageView mSelect;
    private static final int GALLERY_OPEN=89;
    private Button submit;
    private EditText title;
    private EditText descp;
    private Uri imageuri =null;
    private StorageReference mStorage;
    private ProgressDialog progessdialog;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth PostingAuth;
    private FirebaseUser PostingUser;
    private DatabaseReference UserDatabasename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postactivity);

        UserDatabasename=FirebaseDatabase.getInstance().getReference().child("Users");
        submit=(Button)findViewById(R.id.Submitbutton);
        title=(EditText)findViewById(R.id.titlefield);
        descp=(EditText)findViewById(R.id.descriptionfield);
        mSelect=(ImageView) findViewById(R.id.imageselect);
        PostingAuth=FirebaseAuth.getInstance();
        PostingUser=PostingAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        progessdialog=new ProgressDialog(this);

        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**Intent to select image
                 */
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_OPEN);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });



    }

    private void startPosting() {

        final DatabaseReference User;
        User=UserDatabasename.child(PostingUser.getUid());
        progessdialog.setMessage("Posting to Blog...");

        final String titles=title.getText().toString().trim();
        final String descpr=descp.getText().toString().trim();

        if(!TextUtils.isEmpty(titles)&&!TextUtils.isEmpty(descpr)&& imageuri!=null)
        {
            progessdialog.show();
            //Putting the file in the firebase storage
                StorageReference filepath=mStorage.child("Blog_images").child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        //getting the path where image is stored
                       final Uri downloaduri=taskSnapshot.getDownloadUrl();
                      final  DatabaseReference newpost=mDatabaseReference.push();


                        User.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                /*Making a new entry in the database
                                 */

                                newpost.child("title").setValue(titles);
                                newpost.child("desc").setValue(descpr);
                                newpost.child("imageurl").setValue(downloaduri.toString());
                                newpost.child("profilepic").setValue(dataSnapshot.child("Image").getValue());
                                newpost.child("User").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {


                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            startActivity(new Intent(Postactivity.this,MainActivity.class));
                                        }
                                    }
                                });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        progessdialog.dismiss();
                      //  startActivity(new Intent(Postactivity.this,MainActivity.class));


                    }
                });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_OPEN&&resultCode==RESULT_OK)
        {
            //Setting the image in the image view
            imageuri=data.getData();
            mSelect.setImageURI(imageuri);


        }

    }
}

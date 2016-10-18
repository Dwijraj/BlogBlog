package firebaseapps.com.blogging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static firebaseapps.com.blogging.R.color.Imageback;

/**This activity is used to register a new user
 * the user has to register with email and password
 * that they will use to login into their account
 */

public class RegisterActivity extends AppCompatActivity {


    private EditText namefields;
    private EditText emailfields;
    private EditText passwordfields;
    private Button   mRegister;
    private Uri imageUri=null;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress,getmProgress;
    private DatabaseReference databasereference;
    private ImageButton imageButton;
    private StorageReference storageReference;
    private int GALLERY_REQUEST=9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getmProgress=new ProgressDialog(this);
        namefields=(EditText)findViewById(R.id.namefield);
        imageButton=(ImageButton)findViewById(R.id.imageButton);
        emailfields=(EditText)findViewById(R.id.emailfield);
        passwordfields=(EditText)findViewById(R.id.passwordfield);
        mRegister=(Button)findViewById(R.id.registerfield);
        mAuth=FirebaseAuth.getInstance();
        databasereference= FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress=new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Allows user to put their display pic
                Intent profile=new Intent();
                profile.setAction(Intent.ACTION_PICK);
                profile.setType("image/*");
                startActivityForResult(profile,GALLERY_REQUEST);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
           //Allows cropping of the displayed image
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                imageButton.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }

    private void startRegister() {

        final String NAME=namefields.getText().toString().trim();
        final String EMAIL=emailfields.getText().toString().trim();
        final String PASSOWRD=passwordfields.getText().toString();

        if(TextUtils.isEmpty(NAME)||TextUtils.isEmpty(EMAIL)||TextUtils.isEmpty(PASSOWRD)||TextUtils.isEmpty(imageUri.toString()))
        {
            Toast.makeText(getApplicationContext(),"Error registering make sure all fields are shown",Toast.LENGTH_LONG).show();
        }
        else
        {
            mProgress.setMessage("Creating user...");
            mProgress.show();
            /**Registers the user with email address and password
             *
             */

            mAuth.createUserWithEmailAndPassword(EMAIL,PASSOWRD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        StorageReference Profile_Pic=storageReference.child("Profilepics").child(mAuth.getCurrentUser().getUid());
                        Profile_Pic.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Uri profileuri=taskSnapshot.getDownloadUrl();

                                String USER_ID=mAuth.getCurrentUser().getUid();

                                //Stores the user info in the database

                                DatabaseReference currentuserdb=databasereference.child(USER_ID);

                                currentuserdb.child("name").setValue(NAME);
                                // storageReference.child("Profile Pics").putFile(imageUri,)
                                currentuserdb.child("Image").setValue(profileuri.toString());

                                mProgress.dismiss();

                                //After registering the user directly signs him into posting or the Main Activity

                                mAuth.signInWithEmailAndPassword(EMAIL,PASSOWRD).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        Intent main_activity=new Intent(RegisterActivity.this,MainActivity.class);
                                        main_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(main_activity);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        });






                    }

                }
            });
        }

    }
}

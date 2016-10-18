package firebaseapps.com.blogging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**This is the login actitiy if the user isn't singed in this activity pops up and asks the user the sigin before the user can post
 * or retrive posted posts
 */
public class LoginActivity extends AppCompatActivity {

    private DatabaseReference databaseReferenceUsers;
    private EditText email;
    private EditText pass;
    private Button login;
    private FirebaseAuth loginAuth;     //Firebase auth object
    private TextView registerview;
    private TextView forgotpassword;
    private ProgressDialog sign;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sign=new ProgressDialog(this);
        email=(EditText)findViewById(R.id.loginemail);
        pass=(EditText)findViewById(R.id.loginpassword);
        login=(Button)findViewById(R.id.loginbutton);
        loginAuth=FirebaseAuth.getInstance();
        registerview=(TextView)findViewById(R.id.registerId);
        forgotpassword=(TextView)findViewById(R.id.forgotPassword);
        databaseReferenceUsers= FirebaseDatabase.getInstance().getReference().child("User");
        databaseReferenceUsers.keepSynced(true);

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sign.setMessage("Signing you in");
                String LoginEmail=email.getText().toString().trim();
                String LoginPass=pass.getText().toString().trim();

                if(TextUtils.isEmpty(LoginEmail)||TextUtils.isEmpty(LoginPass))
                {
                    Toast.makeText(getApplicationContext(),"Email and password field empty",Toast.LENGTH_LONG).show();
                }
                else
                {
                    sign.show();
                    /*The firebase auth is used to sign the user with the given credentials
                    if the credentials match the user is singed in if not the problem is displayed in a toast
                    message to the user
                     */
                    loginAuth.signInWithEmailAndPassword(LoginEmail,LoginPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            sign.dismiss();
                            Intent Blogintetn=new Intent(LoginActivity.this,MainActivity.class);
                            Blogintetn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//makes sure that the person can't use the back button to come back to login activity
                            startActivity(Blogintetn);//Takes users to the main actitiy where he can put previouslt posted posts or post his new post
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            sign.dismiss();
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                    final String USER_ID= loginAuth.getCurrentUser().getUid();
                                   databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {

                                           if(dataSnapshot.hasChild(USER_ID))
                                           {
                                               Intent Blogintetn=new Intent(LoginActivity.this,MainActivity.class);
                                               Blogintetn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                               startActivity(Blogintetn);
                                           }

                                       }

                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {

                                         //  Toast.makeText(getApplicationContext(),"Need to sign up first",Toast.LENGTH_SHORT).show();
                                       }
                                   });

                            }
                        }
                    });
                }

            }
        });
        registerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**If the user is new it can
                 * help user to create a new account and then sign in
                 */

                Intent register=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(register);


            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**Helps user to retrive their credentials
                 * if they have already made an account but they forgot their
                 * credentials
                 */

                Intent forgot=new Intent(LoginActivity.this,ResendEmail.class);
                forgot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(forgot);
            }
        });



    }
}

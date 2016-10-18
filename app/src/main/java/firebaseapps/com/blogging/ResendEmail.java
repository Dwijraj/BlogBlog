package firebaseapps.com.blogging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

/**The user has to put
 * their registered email address
 * and a password resent link will be sent to their email address
 */

public class ResendEmail extends AppCompatActivity {

    private EditText resendEmail;
    private Button resend;
    private FirebaseAuth resendAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_email);

        resendAuth=FirebaseAuth.getInstance();

        resendEmail=(EditText)findViewById(R.id.resendEmail);
        resend=(Button)findViewById(R.id.resend);


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String RESEND_EMAIL=resendEmail.getText().toString().trim();

                if(TextUtils.isEmpty(RESEND_EMAIL))
                {
                    Toast.makeText(getApplicationContext(),"Enter email field",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //If the email field isn't empty sends a password resend link to thei mail address
                    resendAuth.sendPasswordResetEmail(RESEND_EMAIL).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"PASSWORD RESEND LINK SENT",Toast.LENGTH_SHORT).show();
                            //Prompts the user to login activity
                            Intent Login=new Intent(ResendEmail.this,LoginActivity.class);
                            Login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(Login);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}

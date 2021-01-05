package activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jquiz.project2.R;

public class MetroActivity extends AppCompatActivity {


    private Button btnTranslate;
    private Button btnVision;
    private Button btnSyntax;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metro);
        btnTranslate = (Button) findViewById(R.id.btnTranslate);
        btnVision = (Button) findViewById(R.id.btnVision);
        btnSyntax = (Button) findViewById(R.id.btnSyntax);
        context = this;
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });

        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_WRITE_EXTERNAL_STORAGE);
        askPermission(Manifest.permission.RECORD_AUDIO, MY_RECORD_AUDIO);
        askPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_ACCESS_FINE_LOCATION);
        askPermission(Manifest.permission.CAMERA, MY_CAMERA);
    }

    void askPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    private static final int MY_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_RECORD_AUDIO = 1;
    private static final int MY_ACCESS_FINE_LOCATION = 2;
    private static final int MY_CAMERA = 3;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            switch (requestCode) {
                case MY_WRITE_EXTERNAL_STORAGE:
                    askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);
                    break;
                case MY_RECORD_AUDIO:
                    askPermission(Manifest.permission.RECORD_AUDIO, requestCode);
                    break;
                case MY_ACCESS_FINE_LOCATION:
                    askPermission(Manifest.permission.ACCESS_FINE_LOCATION, requestCode);
                    break;
                case MY_CAMERA:
                    askPermission(Manifest.permission.CAMERA, requestCode);
                    break;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}

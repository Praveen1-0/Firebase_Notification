package com.example.basicnotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private Button btn_buy;
    private EditText cookies;

    String CHANNEL_ID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_buy = findViewById(R.id.button);
        cookies = findViewById(R.id.ed_cookies);
        myRegistrationToken();
        createNotificationChannel();
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberOfCookies = cookies.getText().toString();
                subscribeTo(Integer.parseInt(numberOfCookies));

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.piza);

                Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_chat)
                        .setContentTitle("Cookies!")
                        .setContentText("You just bought " + numberOfCookies + " Cookies!")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setLargeIcon(bitmap)
                        .setSound(defaultNotificationSound)
                        .setLights(Color.GREEN,500,200)
                        .setVibrate(new long[] {0,250,250,250})
                        /* .setStyle(new NotificationCompat.BigPictureStyle()
                                 .bigPicture(bitmap)
                                 .bigLargeIcon(null))*/
                        .addAction(R.mipmap.ic_launcher, "GET BONUS!", pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);


                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                notificationManagerCompat.notify(1, builder.build());
            }
        });
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "My Channel";
            String description = "My channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setVibrationPattern(new long[]{0,250,250,250});
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void myRegistrationToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("FirebaseMessagingService", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseMessagingService", token);
                    }
                });
    }

    public void subscribeTo(int cookies){
        if (cookies <=50){
            FirebaseMessaging.getInstance().subscribeToTopic("small_discount")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Failed to subscribe to small discount", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Subscribed to small discount", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            FirebaseMessaging.getInstance().subscribeToTopic("large_discount")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Failed to subscribe to large discount", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Subscribed to large discount", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
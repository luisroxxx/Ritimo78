package br.com.ritmo78;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import java.util.Calendar;
public class WaterReceiver extends BroadcastReceiver {
  @Override public void onReceive(Context c,Intent intent) {
    int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    if(hour<8||hour>=21) return;
    if(!c.getSharedPreferences("MainActivity",0).getBoolean("water",false))return;
    if(Build.VERSION.SDK_INT>=33&&c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)return;
    NotificationManager manager=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
    if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(new NotificationChannel("agua","Lembretes de água",NotificationManager.IMPORTANCE_DEFAULT));
    NotificationCompat.Builder b=new NotificationCompat.Builder(c,"agua")
      .setSmallIcon(android.R.drawable.ic_popup_reminder)
      .setContentTitle("Hora de beber água 💧")
      .setContentText("Registre sua hidratação no Ritmo78!")
      .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);
    NotificationManagerCompat.from(c).notify((int)(System.currentTimeMillis()%10000),b.build());
  }
}

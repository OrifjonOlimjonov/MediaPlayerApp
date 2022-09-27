package uz.orifjon.mediaplayerapp.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import uz.orifjon.mediaplayerapp.MainActivity
import uz.orifjon.mediaplayerapp.R
import java.io.IOException
import android.app.NotificationManager as NotificationManager1


class MusicService : Service(), MediaPlayer.OnPreparedListener {

    companion object {
        var myMusicPlayer: MediaPlayer? = null
        var path: String? = null
    }

    private val mBinder: IBinder = LocalBinder()


    class LocalBinder : Binder() {

        val service: LocalBinder
            get() =
                this
    }


    override fun onCreate() {
        myMusicPlayer = MediaPlayer()
//        val intent = Intent("music")
//        intent.putExtra("key", "done")
//        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        Log.d(javaClass.simpleName, "onCreate()")

        // startForeground(1, Notification())
        super.onCreate()

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service")
        } else {
            ""
        }
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.app_name))
            .setGroup("Orifjon")
            .setColor(ContextCompat.getColor(this, R.color.teal_200))
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager1.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager1
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        super.onDestroy()
        myMusicPlayer?.stop()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        myMusicPlayer?.reset()
        path = intent?.getStringExtra("path")

        Log.v("TAG", "onStartCommand: $path")
        try {
            if (path != null) {
                myMusicPlayer?.setDataSource(applicationContext, Uri.parse(path))
                myMusicPlayer?.isLooping = true
                myMusicPlayer?.setOnPreparedListener(this)
                myMusicPlayer?.prepare()
                myMusicPlayer?.prepareAsync()
            }
        } catch (e: IllegalArgumentException) {

            e.printStackTrace()
        } catch (e: SecurityException) {

            e.printStackTrace()
        } catch (e: IllegalStateException) {

            e.printStackTrace()
        } catch (e: IOException) {

            e.printStackTrace()
        }

        if (intent?.getStringExtra("key").equals("START")) {
            if (myMusicPlayer?.isPlaying == false) {
                playMusic()
            }
        }
        if (intent?.getStringExtra("key").equals("STOP")) {
            if (myMusicPlayer?.isPlaying == true) {
                stopMusic()
            }
        }
        if (intent?.getStringExtra("key").equals("PAUSE")) {
            if (myMusicPlayer?.isPlaying == true) {
                pauseMusic()
            }
        }



        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {

        return mBinder
    }



    fun pauseMusic() {
        myMusicPlayer!!.pause()
        Log.d(javaClass.simpleName, "pauseMusic()")
    }

    fun playMusic() {
        myMusicPlayer!!.start()

        Log.d(javaClass.simpleName, "start()")
    }

    fun stopMusic() {
        myMusicPlayer?.stop()
        Log.d(javaClass.simpleName, "stop()")
    }
    override fun onPrepared(p0: MediaPlayer?) {
        p0?.start()
       }

}
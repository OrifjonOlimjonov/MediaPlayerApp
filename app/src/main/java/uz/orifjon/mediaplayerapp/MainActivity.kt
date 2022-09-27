package uz.orifjon.mediaplayerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uz.orifjon.mediaplayerapp.databinding.ActivityMainBinding
import uz.orifjon.mediaplayerapp.service.MusicService

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this,MusicService::class.java)
        intent.putExtra("key","STOP")
        startForegroundService(intent)
    }
}
package uz.orifjon.mediaplayerapp.fragments

import android.annotation.SuppressLint
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import uz.orifjon.mediaplayerapp.R
import uz.orifjon.mediaplayerapp.database.MyMusic
import uz.orifjon.mediaplayerapp.databinding.FragmentPlayMusicBinding

class PlayMusicFragment : Fragment(), MediaPlayer.OnPreparedListener {

    private lateinit var binding: FragmentPlayMusicBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var list: ArrayList<MyMusic>
    private lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayMusicBinding.inflate(inflater)
        val music = arguments?.getSerializable("music") as MyMusic
        var index = arguments?.getInt("index", -1)
        list = scanDeviceForMp3Files() as ArrayList<MyMusic>
        //MusicDatabase.getDatabase(requireContext()).musicDao().listMusics() as ArrayList<MyMusic>

        playing(music)
        binding.apply {

            //musicTime.text = music.duration

            btnPlay.setOnClickListener {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    binding.btnPlay.setImageResource(R.drawable.btn_play)
                } else {
                    mediaPlayer?.start()
                    binding.btnPlay.setImageResource(R.drawable.btn_pause)
                }
            }

            btnBack.setOnClickListener {
                mediaPlayer?.stop()
                if (index == 0) {
                    playing(list[list.size - 1])
                    index = list.size - 1
                } else {
                    playing(list[index!! - 1])
                    index = index!! - 1
                }
            }

            btnNext.setOnClickListener {
                mediaPlayer?.stop()
                index = if (index == list.size - 1) {
                    playing(list[0])
                    0
                } else {
                    playing(list[index!! + 1])
                    index!! + 1
                }
            }

            btnBack10.setOnClickListener {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.minus(5000)?:0)
            }

            btnNext10.setOnClickListener {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(5000)?:0)
            }
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        mediaPlayer?.seekTo(p1)
                        p0?.progress = p1
                    }

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
        }


        return binding.root
    }

    private val runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            val currentPosition = mediaPlayer?.currentPosition
            if (currentPosition != null) {
                binding.seekbar.progress = currentPosition
                if((currentPosition%60000)/1000 > 9 && currentPosition/60000 > 9){
                    binding.musicTime.text = "${currentPosition/60000}:${(currentPosition%60000)/1000}"
                }
                if(currentPosition/60000 < 10){
                    binding.musicTime.text = "0${currentPosition/60000}:${(currentPosition%60000)/1000}"
                }
                if((currentPosition%60000)/1000 < 10){
                    binding.musicTime.text = "${currentPosition/60000}:0${(currentPosition%60000)/1000}"
                }
                if((currentPosition%60000)/1000 < 10 && currentPosition/60000 < 10){
                    binding.musicTime.text = "0${currentPosition/60000}:0${(currentPosition%60000)/1000}"
                }
            }
            handler.postDelayed(this, 1000)
        }

    }
    @SuppressLint("SetTextI18n")
    private fun playing(music: MyMusic) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(requireContext(), Uri.parse(music.aPath))
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.prepareAsync()
        binding.musicName.text = music.aName
        binding.musicArtist.text = music.aArtist
        val duration = music.duration
        if((duration%60000)/1000 > 9 && duration/60000 > 9){
            binding.musicMaxTime.text = "/ ${duration/60000}:${(duration%60000)/1000}"
        }else if(duration/60000 < 10){
            binding.musicMaxTime.text = "/ 0${duration/60000}:${(duration%60000)/1000}"
        }else if((duration%60000)/1000 < 10){
            binding.musicMaxTime.text = "/ ${duration/60000}:0${(duration%60000)/1000}"
        }else if((duration%60000)/1000 < 10 && duration/60000 < 10 ){
            binding.musicMaxTime.text = "/ 0${duration/60000}:0${(duration%60000)/1000}"
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        p0?.start()
        binding.seekbar.progress = 0
        binding.musicTime.text = "00:00"
        binding.seekbar.max = mediaPlayer?.duration!!
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.stop()
    }



    fun scanDeviceForMp3Files(): List<MyMusic> {

        // val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM
        )
        val mp3Files: MutableList<MyMusic> = ArrayList()
        var cursor: Cursor? = null
        try {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    //   val title: String = cursor.getString(0)
                    val artist: String = cursor.getString(1)
                    val path: String = cursor.getString(2)
                    val displayName: String = cursor.getString(3)
                    val songDuration: Long = cursor.getLong(4)
                    val album: String = cursor.getString(5)
                    cursor.moveToNext()
//                   if (path.endsWith(".mp3")) {
                    val music = MyMusic(
                        aPath = path,
                        aArtist = artist,
                        aName = displayName,
                        aAlbum = album,
                        duration = songDuration
                    )
                    mp3Files.add(music)
                    //      MusicDatabase.getDatabase(requireContext()).musicDao().addMusic(music)
                    //  }
                }
            }

        } catch (e: Exception) {
            Log.e("TAG", e.toString())
        } finally {
            cursor?.close()
        }
        return mp3Files
    }




}
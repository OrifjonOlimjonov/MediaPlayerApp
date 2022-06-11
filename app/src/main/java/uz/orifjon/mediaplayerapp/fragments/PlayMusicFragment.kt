package uz.orifjon.mediaplayerapp.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.orifjon.mediaplayerapp.R
import uz.orifjon.mediaplayerapp.database.MusicDatabase
import uz.orifjon.mediaplayerapp.database.MyMusic
import uz.orifjon.mediaplayerapp.databinding.FragmentPlayMusicBinding

class PlayMusicFragment : Fragment(), MediaPlayer.OnPreparedListener {

    private lateinit var binding: FragmentPlayMusicBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var list: ArrayList<MyMusic>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayMusicBinding.inflate(inflater)
        val music = arguments?.getSerializable("music") as MyMusic
        val index = arguments?.getInt("index", -1)
        list = MusicDatabase.getDatabase(requireContext()).musicDao()
            .listMusics() as ArrayList<MyMusic>

        playing(music)
        binding.apply {

            //musicTime.text = music.duration


            btnPlay.setOnClickListener {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.start()
                }
            }

            btnBack.setOnClickListener {
                if (index == 0) {
                    playing(list[list.size - 1])
                } else {
                    playing(list[index!! - 1])
                }
            }

            btnNext.setOnClickListener {
                if (index == list.size - 1) {
                    playing(list[0])
                } else {
                    playing(list[index!! + 1])
                }
            }

            btnBack10.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition.minus(5000))
            }

            btnNext10.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition.plus(5000))
            }
        }


        return binding.root
    }

    private fun playing(music: MyMusic) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(music.aPath)
        mediaPlayer.setOnPreparedListener(this@PlayMusicFragment)
        mediaPlayer.prepareAsync()
        binding.musicName.text = music.aName
        binding.musicArtist.text = music.aArtist
    }

    override fun onPrepared(p0: MediaPlayer?) {
        p0?.start()
    }


}
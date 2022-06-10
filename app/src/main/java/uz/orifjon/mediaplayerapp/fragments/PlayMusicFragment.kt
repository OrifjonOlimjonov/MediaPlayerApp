package uz.orifjon.mediaplayerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.orifjon.mediaplayerapp.R
import uz.orifjon.mediaplayerapp.databinding.FragmentPlayMusicBinding

class PlayMusicFragment : Fragment() {

    private lateinit var binding: FragmentPlayMusicBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayMusicBinding.inflate(inflater)

        binding.apply {


        }

        return binding.root
    }


}
package uz.orifjon.mediaplayerapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import uz.orifjon.mediaplayerapp.R
import uz.orifjon.mediaplayerapp.adapters.AdapterRV
import uz.orifjon.mediaplayerapp.database.MusicDatabase
import uz.orifjon.mediaplayerapp.database.MyMusic
import uz.orifjon.mediaplayerapp.databinding.FragmentMainBinding


class MainFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var list: ArrayList<MyMusic>
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: AdapterRV
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)

        if (!hasPermission()) {
            requestPermission()
        } else {
            list = scanDeviceForMp3Files() as ArrayList<MyMusic>
            adapter = AdapterRV(list){music, position ->
                val bundle = Bundle()
                    bundle.putSerializable("music",music)
                bundle.putInt("index",position)
              findNavController().navigate(R.id.playMusicFragment,bundle)
            }
            binding.rv.adapter = adapter
        }

        return binding.root
    }
    private fun hasPermission() =
        EasyPermissions.hasPermissions(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        )

    private fun requestPermission() {
        EasyPermissions.requestPermissions(
            this, "Kontaktlarni o'qishga ruxsat berishingiz kerak!",
            1,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            list = scanDeviceForMp3Files() as ArrayList<MyMusic>
            adapter = AdapterRV(list){
                    music, position ->
                val bundle = Bundle()
                bundle.putSerializable("music",music)
                bundle.putInt("index",position)
                findNavController().navigate(R.id.playMusicFragment,bundle)
            }
            binding.rv.adapter = adapter
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (hasPermission()) {

        } else {
            Toast.makeText(requireContext(), "Not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        list = scanDeviceForMp3Files() as ArrayList<MyMusic>
        adapter = AdapterRV(list){music, position ->
            findNavController().navigate(R.id.playMusicFragment)}
        binding.rv.adapter = adapter
        return super.shouldShowRequestPermissionRationale(permission)
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
                    val album:String = cursor.getString(5)
                    cursor.moveToNext()
//                   if (path.endsWith(".mp3")) {
                        val music = MyMusic(aPath = path, aArtist = artist, aName = displayName, aAlbum = album, duration = songDuration)
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
package uz.orifjon.mediaplayerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.orifjon.mediaplayerapp.databinding.ItemBinding
import uz.orifjon.mediaplayerapp.database.MyMusic

class AdapterRV(var list: ArrayList<MyMusic>, var onItemClick:(MyMusic,Int)->Unit) : RecyclerView.Adapter<AdapterRV.VH>() {

    inner class VH(var binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(music: MyMusic, position: Int) {
            binding.artist.text = music.aArtist
            binding.title.text = music.aName

            itemView.setOnClickListener {
                onItemClick(music,position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}
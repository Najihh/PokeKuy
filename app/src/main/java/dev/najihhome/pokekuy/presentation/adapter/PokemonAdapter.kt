package dev.najihhome.pokekuy.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.najihhome.pokekuy.databinding.ItemPokemonBinding
import dev.najihhome.pokekuy.domain.model.Pokemon

class PokemonAdapter(
    private val onItemClick: (Pokemon) -> Unit
) : ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(PokemonDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding, onItemClick)
    }
    
    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class PokemonViewHolder(
        private val binding: ItemPokemonBinding,
        private val onItemClick: (Pokemon) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(pokemon: Pokemon) {
            binding.textViewPokemonName.text = pokemon.getFormattedName()
            binding.textViewPokemonId.text = "#${pokemon.id}"
            
            binding.root.setOnClickListener {
                onItemClick(pokemon)
            }
        }
    }
    
    class PokemonDiffCallback : DiffUtil.ItemCallback<Pokemon>() {
        override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem == newItem
        }
    }
}

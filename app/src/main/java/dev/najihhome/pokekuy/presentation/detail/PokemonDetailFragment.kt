package dev.najihhome.pokekuy.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dev.najihhome.pokekuy.databinding.FragmentPokemonDetailBinding
import dev.najihhome.pokekuy.domain.model.Pokemon
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PokemonDetailFragment : Fragment() {
    
    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: PokemonDetailFragmentArgs by navArgs()
    private val viewModel: PokemonDetailViewModel by viewModel { parametersOf(args.pokemonId) }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        observeViewModel()
        
        viewModel.loadPokemonDetail()
    }
    
    private fun observeViewModel() {
        viewModel.detailState.observe(viewLifecycleOwner) { state ->
            when {
                state.isLoading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.cardViewPokemonInfo.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                }
                state.error != null -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                }
                state.pokemon != null -> {
                    binding.progressBar.visibility = View.GONE
                    binding.cardViewPokemonInfo.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                    
                    updatePokemonInfo(state.pokemon)
                }
            }
        }
    }
    
    private fun updatePokemonInfo(pokemon: Pokemon) {
        binding.textViewPokemonName.text = pokemon.getFormattedName()
        
        val abilitiesText = pokemon.abilities.joinToString("\n") { ability ->
            "• ${ability.getFormattedName()}${if (ability.isHidden) " (Hidden)" else ""}"
        }
        
        binding.textViewPokemonAbilities.text = abilitiesText
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

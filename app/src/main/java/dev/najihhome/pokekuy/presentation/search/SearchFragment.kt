package dev.najihhome.pokekuy.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dev.najihhome.pokekuy.databinding.FragmentSearchBinding
import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.presentation.adapter.PokemonAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : Fragment() {
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val args: SearchFragmentArgs by navArgs()
    private val viewModel: SearchViewModel by viewModel { parametersOf(args.query) }
    
    private lateinit var pokemonAdapter: PokemonAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        
        binding.textViewSearchQuery.text = "Results for: ${args.query}"
        viewModel.searchPokemon()
    }
    
    private fun setupRecyclerView() {
        pokemonAdapter = PokemonAdapter { pokemon ->
            navigateToPokemonDetail(pokemon.id.toString())
        }
        
        binding.recyclerViewSearchResults.apply {
            adapter = pokemonAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun observeViewModel() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            when {
                state.isLoading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewSearchResults.visibility = View.GONE
                    binding.textViewNoResults.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                }
                state.error != null -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                }
                state.pokemonList.isEmpty() -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewSearchResults.visibility = View.GONE
                    binding.textViewNoResults.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewSearchResults.visibility = View.VISIBLE
                    binding.textViewNoResults.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                    
                    pokemonAdapter.submitList(state.pokemonList)
                }
            }
        }
    }
    
    private fun navigateToPokemonDetail(pokemonId: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToPokemonDetailFragment(pokemonId)
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

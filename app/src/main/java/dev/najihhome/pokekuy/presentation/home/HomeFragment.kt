package dev.najihhome.pokekuy.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dev.najihhome.pokekuy.R
import dev.najihhome.pokekuy.databinding.FragmentHomeBinding
import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.presentation.adapter.PokemonAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var pokemonAdapter: PokemonAdapter
    
    private var isLoading = false
    private var offset = 0
    private val limit = 10
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        
        viewModel.loadPokemonList(offset, limit)
    }
    
    private fun setupRecyclerView() {
        pokemonAdapter = PokemonAdapter { pokemon ->
            navigateToPokemonDetail(pokemon.id.toString())
        }
        
        binding.recyclerViewPokemon.apply {
            adapter = pokemonAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    
                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                        && firstVisibleItemPosition >= 0
                    ) {
                        // Load more items
                        offset += limit
                        viewModel.loadPokemonList(offset, limit)
                    }
                }
            })
        }
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    navigateToSearch(query)
                }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    
    private fun observeViewModel() {
        viewModel.pokemonListState.observe(viewLifecycleOwner) { state ->
            when {
                state.isLoading -> {
                    isLoading = true
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                }
                state.error != null -> {
                    isLoading = false
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    isLoading = false
                    binding.progressBar.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                    
                    if (state.pokemonList.isNotEmpty()) {
                        pokemonAdapter.submitList(state.pokemonList)
                    }
                }
            }
        }
    }
    
    private fun navigateToPokemonDetail(pokemonId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToPokemonDetailFragment(pokemonId)
        findNavController().navigate(action)
    }
    
    private fun navigateToSearch(query: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(query)
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

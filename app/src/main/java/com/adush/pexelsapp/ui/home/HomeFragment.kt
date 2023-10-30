package com.adush.pexelsapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.FragmentHomeBinding
import com.adush.pexelsapp.ui.utils.AlertSender
import com.adush.pexelsapp.ui.PexelsApp
import com.adush.pexelsapp.ui.adapter.HeadersAdapter
import com.adush.pexelsapp.ui.adapter.ImageListAdapter
import com.adush.pexelsapp.ui.adapter.ImagesLoadStateAdapter
import com.adush.pexelsapp.ui.utils.ViewModelFactory
import com.adush.pexelsapp.ui.utils.simpleScan
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private val imageListAdapter by lazy {
        ImageListAdapter(::setupDetailFragment)
    }

    private val headersAdapter by lazy {
        HeadersAdapter(::searchByHeader)
    }

    private var mDisposable = CompositeDisposable()

    private val component by lazy {
        (requireActivity().application as PexelsApp).component
    }

    override fun onAttach(context: Context) {
          component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeadersAdapter()
        setupImagesAdapter()

        getOnQueryTextObservable()
        handleScrollingToTopWhenSearching()
        handleListVisibility()

        observeFeaturedCollections()
        observeFeaturedCollectionsError()
        getImageList()

        binding.exploreText.setOnClickListener {
            searchByHeader("")
        }
        binding.tryAgainText.setOnClickListener {
            imageListAdapter.retry()
        }

    }

    private fun setupImagesAdapter() {
        binding.imageList.apply {
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = imageListAdapter.withLoadStateFooter(
                ImagesLoadStateAdapter { imageListAdapter.retry() }
            )
//            itemAnimator = null
        }

        imageListAdapter.addLoadStateListener { loadState ->
            binding.imageList.isVisible = loadState.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            binding.tryAgainLayout.isVisible = loadState.refresh is LoadState.Error

            val displayEmptyMessage = (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && imageListAdapter.itemCount == 0)
            binding.exploreLayout.isVisible = displayEmptyMessage


            val errorState = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }

            when (val throwable = errorState?.error) {
                is IOException -> {
                    AlertSender.sendMessage(requireContext(), requireContext().resources.getString(R.string.err_poor_internet_connection))
                }
                is HttpException -> {
                    if (throwable.code() == 504) {
                        AlertSender.sendMessage(requireContext(), requireContext().resources.getString(R.string.err_network_connection))
                    }
                }
                else -> {}
            }
        }
//        (binding.imageList.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
    }

    private fun setupHeadersAdapter() {
        binding.headersList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = headersAdapter
        }
    }

    private fun searchByHeader(header: String) {
        binding.searchView.apply {
            setQuery(header, true)
            clearFocus()
        }
    }

    private fun observeFeaturedCollectionsError(){
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            AlertSender.sendMessage(requireContext(), requireContext().resources.getString(R.string.err_network_connection))
        }
    }

    private fun setupDetailFragment() {
    }

    private fun observeFeaturedCollections() {
        viewModel.featuredCollections.observe(viewLifecycleOwner) {
            headersAdapter.submitList(it)
        }
        mDisposable = viewModel.disposable
    }

    private fun getImageList() {
        mDisposable.add(
            viewModel.getImageListObservable()
                .subscribe {
                    imageListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                })
    }

    private fun getOnQueryTextObservable() {
        mDisposable.add(createOnQueryTextObservable()
            .filter { it != null }
            .map { query ->
                query.lowercase().trim().apply {
                    headersAdapter.setHeader(this)
                }
            }
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .flatMap { query ->
                if (query.isEmpty()) {
                    return@flatMap viewModel.getImageListObservable()
                } else {
                    return@flatMap viewModel.getImageListBySearch(query)
                }
            }
            .subscribe {
                imageListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            })
    }

    private fun createOnQueryTextObservable(): Observable<String?> {
        val onQueryTextObservable = Observable.create { subscriber ->
            binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    subscriber.onNext(query)
                    binding.searchView.clearFocus()
                    return true
                }

                    override fun onQueryTextChange(query: String?): Boolean {
                    subscriber.onNext(query)
                    return true
                }
            })
        }
        return onQueryTextObservable
    }

    private fun handleScrollingToTopWhenSearching() = lifecycleScope.launch {
        getRefreshLoadStateFlow()
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.imageList.scrollToPosition(0)
                }
            }
    }

    private fun handleListVisibility() = lifecycleScope.launch {
        getRefreshLoadStateFlow()
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.imageList.isInvisible = current is LoadState.Error
                        || (previous is LoadState.Error && current is LoadState.Loading)
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun getRefreshLoadStateFlow(): Flow<LoadState> {
        return imageListAdapter.loadStateFlow
            .map { it.refresh }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable.clear()
        _binding = null
    }
}
package com.adush.pexelsapp.ui.bookmarks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.FragmentBookmarksBinding
import com.adush.pexelsapp.ui.PexelsApp
import com.adush.pexelsapp.ui.adapter.bookmarks.BookmarksAdapter
import com.adush.pexelsapp.ui.adapter.images.ImagesLoadStateAdapter
import com.adush.pexelsapp.ui.details.DetailsFragment
import com.adush.pexelsapp.ui.utils.AlertSender
import com.adush.pexelsapp.ui.utils.Constants
import com.adush.pexelsapp.ui.utils.ViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (requireActivity().application as PexelsApp).component
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[BookmarksViewModel::class.java]
    }

    private val bookmarksAdapter by lazy {
        BookmarksAdapter(::setupDetailsFragment)
    }

    private var mDisposable = CompositeDisposable()

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getImageListFromDb()
        setupBookmarksAdapter()
        setClickListenersExplore()
    }

    private fun getImageListFromDb() {
        mDisposable.add(
            viewModel.getImageListFromDb()
                .subscribe {
                    bookmarksAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                })
    }

    private fun setupDetailsFragment(id: Int) {
        val bundle = DetailsFragment.newInstance(id, Constants.MODE_DB)
        findNavController().navigate(R.id.action_navigation_bookmarks_screen_to_navigation_details_screen, bundle)
    }

    private fun setupBookmarksAdapter() {
        binding.bookmarksList.apply {
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = bookmarksAdapter.withLoadStateFooter(
                ImagesLoadStateAdapter { bookmarksAdapter.retry() }
            )
        }

        bookmarksAdapter.addLoadStateListener { loadState ->
            binding.bookmarksList.isVisible = loadState.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            val displayEmptyMessage = (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && bookmarksAdapter.itemCount == 0)
            binding.exploreLayout.root.isVisible = displayEmptyMessage

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
    }

    private fun setClickListenersExplore() {
        binding.exploreLayout.apply {
            textErrorExplore.text = requireContext().resources.getString(R.string.lbl_havent_saved)
            exploreText.setOnClickListener {
                findNavController().popBackStack(R.id.navigation_home_screen, true)
                findNavController().navigate(R.id.navigation_home_screen)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}
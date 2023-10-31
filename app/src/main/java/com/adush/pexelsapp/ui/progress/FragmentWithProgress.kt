package com.adush.pexelsapp.ui.progress

import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

open class FragmentWithProgress: Fragment() {

    var progress: ProgressBar? = null

    fun listenProgress(viewModel: ProgressViewModel) {
        viewModel.showProgress.observe(viewLifecycleOwner) {
            if (it) {
                showProgress()
            } else {
                hideProgress()
            }
        }
    }

    private fun showProgress() {
        if (progress != null ) {
            progress!!.visibility = View.VISIBLE
        }
    }

    private fun hideProgress() {
        if (progress != null ) {
            progress!!.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        hideProgress()
    }
}
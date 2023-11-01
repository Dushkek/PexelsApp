package com.adush.pexelsapp.ui.details

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.FragmentDetailsBinding
import com.adush.pexelsapp.domain.model.ImageItem
import com.adush.pexelsapp.domain.model.ImageItem.Companion.UNDEFINED_ID
import com.adush.pexelsapp.ui.PexelsApp
import com.adush.pexelsapp.ui.progress.FragmentWithProgress
import com.adush.pexelsapp.ui.utils.AlertSender
import com.adush.pexelsapp.ui.utils.Constants
import com.adush.pexelsapp.ui.utils.ViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class DetailsFragment : FragmentWithProgress() {

    private var _binding: FragmentDetailsBinding? = null

    private val binding
        get() = _binding!!

    private var imageId = UNDEFINED_ID
    private var screenMode = Constants.MODE_UNKNOWN
    private lateinit var imageItem: ImageItem

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (requireActivity().application as PexelsApp).component
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[DetailsViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        progress = binding.progressBar
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        listenProgress(viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (screenMode == Constants.MODE_REQUEST) {
            getImage()
        } else if (screenMode == Constants.MODE_DB) {
            getImageFromDb()
        }

        observeErrors()
        observeImage()
        setClickListenersBack()
        setClickListenerBookmark()
        setClickListenerDownload()
        imageZoom()

    }

    private fun getImage() {
        viewModel.getImageById(imageId)
        viewModel.getImageFromDb(imageId)
    }

    private fun getImageFromDb() {
        viewModel.getImageFromDb(imageId)
    }

    private fun observeErrors() {
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            setExploreVisibility()
            showToastMessage(R.string.err_poor_internet_connection)
        }

        viewModel.errorMessageException.observe(viewLifecycleOwner) {
            setExploreVisibility()
            showToastMessage(R.string.err_failed_response)
        }
    }

    private fun observeActiveBookmark() {
        viewModel.imageFromDb.observe(viewLifecycleOwner) { imageFromDb ->
            if(::imageItem.isInitialized) {
                imageItem.bookmark = true
                if (imageItem == imageFromDb){
                    binding.btnToBookmark.isSelected = true
                }
            }
        }
    }

    private fun setExploreVisibility() {
        binding.exploreLayout.root.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.imageRoot.visibility = View.GONE
        binding.authorName.visibility = View.GONE
    }

    private fun observeImage() {
        val listener = object : RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                setExploreVisibility()
                showToastMessage(R.string.err_poor_internet_connection)
                return true
            }

            override fun onResourceReady(resource: Drawable, model: Any,target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                observeActiveBookmark()
                binding.authorName.text = imageItem.author
                binding.btnDownload.isEnabled = true
                binding.btnToBookmark.isEnabled = true
                viewModel.hideProgress()
                return false
            }
        }
        viewModel.image.observe(viewLifecycleOwner) {
            imageItem = it
            Glide.with(this)
                .load(it.imageSrc.original)
                .placeholder(R.drawable.ic_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_placeholder)
                .addListener(listener)
                .transition(DrawableTransitionOptions.withCrossFade())
                .dontTransform()
                .into(binding.imageView)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun imageZoom() {
        binding.imageView.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP){
                binding.imageView.resetZoom()
            }
            true
        }
    }

    private fun setClickListenerBookmark() {
        binding.btnToBookmark.isEnabled = false
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_bounce).apply {
            interpolator = BounceInterpolator()
        }
        binding.btnToBookmark.setOnClickListener {
            it.startAnimation(animation)
            it.isSelected = !it.isSelected
            if (it.isSelected){
                imageItem.bookmark = true
                viewModel.addImageToBookmarks(imageItem)
            } else{
                imageItem.bookmark = false
                viewModel.deleteImageFromBookmarks(imageItem)
            }
        }
    }

    private fun setClickListenerDownload() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_bounce).apply {
            interpolator = FastOutSlowInInterpolator()
        }
        binding.btnDownload.setOnClickListener {
            it.startAnimation(animation)
            if (checkPermission()){
                downloadImage()
            }else {
                requestPermission()
            }
        }
    }

    private fun setClickListenersBack() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_back).apply {
            interpolator = LinearOutSlowInInterpolator()
        }
        binding.buttonBack.setOnClickListener {
            it.startAnimation(animation)
            findNavController().popBackStack()
        }

        binding.exploreLayout.apply {
            textErrorExplore.text = requireContext().resources.getString(R.string.lbl_image_not_found)
            exploreText.setOnClickListener {
                closeFragment()
            }
        }
    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(Constants.EXTRA_IMAGE_ID) || !args.containsKey(Constants.EXTRA_IMAGE_MODE)) {
            throw RuntimeException("Image is not found")
        }

        val modeLaunch = args.getString(Constants.EXTRA_IMAGE_MODE)
        if (modeLaunch != Constants.MODE_REQUEST && modeLaunch != Constants.MODE_DB) {
            throw RuntimeException("Unknown screen mode: $modeLaunch")
        }
        screenMode = modeLaunch
        if (!args.containsKey(Constants.EXTRA_IMAGE_ID)) {
            throw RuntimeException("Unknown screen mode: $screenMode")
        } else
            imageId = args.getInt(Constants.EXTRA_IMAGE_ID, UNDEFINED_ID)
    }

    private fun closeFragment() {
        findNavController().popBackStack(R.id.navigation_home_screen, true)
        findNavController().navigate(R.id.navigation_home_screen)
    }

    private fun downloadImage() {
        val bitmap = binding.imageView.drawable.toBitmap()
        val timestamp = System.currentTimeMillis()

        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        val contentResolver = requireActivity().contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                try {
                    val outputStream = contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    contentResolver.update(uri, values, null, null)

                    showToastMessage(R.string.msg_successfully_saved)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            val imageFileFolder = File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name))
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                showToastMessage(R.string.msg_successfully_saved)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showToastMessage(R.string.msg_allow_permission)
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage()
            } else {
                showToastMessage(R.string.msg_provide_permission)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showToastMessage(id: Int){
        AlertSender.sendMessage(requireContext(),requireContext().resources.getString(id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100

        fun newInstance(id: Int, modeLaunch: String): Bundle {
            return Bundle().apply {
                putString(Constants.EXTRA_IMAGE_MODE, modeLaunch)
                putInt(Constants.EXTRA_IMAGE_ID, id)
            }
        }
    }
}
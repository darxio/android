package com.darx.foodwise.fragments


import android.Manifest
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.darx.foodwise.R
import com.darx.foodwise.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.hardware.Camera
import android.util.Log
import android.view.View.OnClickListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.google.common.base.Objects
import com.darx.foodwise.camerafragment.camera.GraphicOverlay
import com.darx.foodwise.camerafragment.camera.WorkflowModel
import com.darx.foodwise.camerafragment.camera.WorkflowModel.WorkflowState
import com.darx.foodwise.camerafragment.camera.CameraSource
import com.darx.foodwise.camerafragment.camera.CameraSourcePreview
import java.io.IOException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Camera.Parameters.FLASH_MODE_OFF
import android.hardware.Camera.Parameters.FLASH_MODE_TORCH
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.FoodApp
import com.darx.foodwise.camerafragment.objectdetection.MultiObjectProcessor
import com.darx.foodwise.camerafragment.objectdetection.ProminentObjectProcessor
import com.darx.foodwise.camerafragment.productsearch.BottomSheetScrimView
import com.darx.foodwise.camerafragment.productsearch.SearchEngine
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSource
import com.darx.foodwise.services.NetworkDataSourceImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.common.collect.ImmutableList
import com.darx.foodwise.camerafragment.productsearch.ProductAdapter
import com.darx.foodwise.camerafragment.settings.PreferenceUtils
import com.darx.foodwise.camerafragment.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_live_object_kotlin.*
import kotlinx.android.synthetic.main.top_action_bar_in_live_camera.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ObjectDetectionFragment : Fragment(), OnClickListener {

    init {
        Log.d("TEST", "TEST OBJECT")
    }

    private var CAMERA_REQUEST = 1;
    private var AUTO_MODE = false;
    private var MULTIPLE_MODE = false;

    private var cameraSource: CameraSource? = null
    private var camera: Camera? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var searchButton: ExtendedFloatingActionButton? = null
    private var changeModeButton: View? = null
    private var searchButtonAnimator: AnimatorSet? = null
    private var searchProgressBar: ProgressBar? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowState? = null
    private var searchEngine: SearchEngine? = null

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var bottomSheetScrimView: BottomSheetScrimView? = null
    private var productRecyclerView: RecyclerView? = null
    private var bottomSheetTitleView: TextView? = null
    private var objectThumbnailForBottomSheet: Bitmap? = null
    private var slidingSheetUpFromHiddenState: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?  {
        super.onCreate(savedInstanceState)

        if (container == null) {
            return null;
        }

        val viewGroupID: Int
        val view: View

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                context!!, "Дайте доступ к камере!",
                Toast.LENGTH_SHORT
            ).show()
            viewGroupID = R.layout.no_camera
            view = inflater.inflate(viewGroupID, container, false)
        } else {
            viewGroupID = R.layout.activity_live_object_kotlin
            view = inflater.inflate(viewGroupID, container, false)

            val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
            networkDataSource = NetworkDataSourceImpl(apiService, context!!)
//            productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

//            networkDataSource?.product?.observe(this, Observer {
//                barcodeField.label = it.name ?: ""
//                barcodeField.value = it.description ?: ""
//                productViewModel?.upsert_(it)
//
//                BarcodeResultFragment.show(activity!!.supportFragmentManager, barcodeField, it)
//            })

//            view.tutorial_button.setOnClickListener {
//                val intent = Intent(this.context, WelcomeWizardActivity::class.java)
//                startActivity(intent)
//            }
        }

        networkDataSource?.fruit?.observe(this, Observer {
           Toast.makeText(context, it.prediction, Toast.LENGTH_SHORT).show()
        })
        return view
    }

    override  fun onStart() {
        super.onStart()
        var view = getView()
        camera = FoodApp.instance.camera
        searchEngine = SearchEngine(context!!.applicationContext)
        preview = getView()?.findViewById(R.id.camera_preview)
        graphicOverlay = getView()?.findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay)?.apply {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context!!,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.CAMERA
                    ),
                    CAMERA_REQUEST
                    )
            }
            getView()?.setOnClickListener(this@ObjectDetectionFragment)
            cameraSource = CameraSource(this)
        }


        val barcodeIcon = resources.getDrawable(R.drawable.ic_action_camera_small)
        change_mode_button.setImageDrawable(barcodeIcon)

        searchButton = view?.findViewById<ExtendedFloatingActionButton>(R.id.product_search_button).apply {
            view?.setOnClickListener(this@ObjectDetectionFragment)
        }
        searchButton?.setOnClickListener(this)

        searchButtonAnimator =
            (AnimatorInflater.loadAnimator(context, R.animator.search_button_enter) as AnimatorSet).apply {
                setTarget(searchButton)
            }
        searchProgressBar = view?.findViewById(R.id.search_progress_bar)
        setUpBottomSheet()
        flashButton = view?.findViewById<View>(R.id.flash_button).apply {
            view?.setOnClickListener(this@ObjectDetectionFragment)
        }

        settingsButton = settings_button
        settingsButton?.setOnClickListener(this)
//        settingsButton?.visibility = VISIBLE

        changeModeButton = getView()?.findViewById(R.id.change_mode_button)
        changeModeButton?.setOnClickListener(this@ObjectDetectionFragment)

        promptChip = getView()?.findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(context, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                setTarget(promptChip)
            }

        flashButton = getView()?.findViewById(R.id.flash_button)
        flashButton?.setOnClickListener(this)

        setUpWorkflowModel()

    }

    override fun onResume() {
        super.onResume()

        workflowModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        currentWorkflowState = WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(
            if (PreferenceUtils.isMultipleObjectsMode(context!!)) {
//            if (MULTIPLE_MODE) {
            MultiObjectProcessor(graphicOverlay!!, workflowModel!!)
            } else {
                ProminentObjectProcessor(graphicOverlay!!, workflowModel!!)
            }
//        ProminentObjectProcessor(graphicOverlay!!, workflowModel!!)
        )
        workflowModel?.setWorkflowState(WorkflowState.DETECTING)
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
        searchEngine?.shutdown()
    }

    override fun onClick(view: View) {
        val id = view.id
        when (view.id) {
            R.id.product_search_button -> {
                searchButton?.isEnabled = false
                workflowModel?.onSearchButtonClicked()
            }
            R.id.bottom_sheet_scrim_view -> bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        this.cameraSource?.updateFlashMode(FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        this.cameraSource?.updateFlashMode(FLASH_MODE_TORCH)
                    }
                }
            }
            R.id.settings_button -> {
//                settingsButton?.isEnabled = false
                startActivity(Intent(context, SettingsActivity::class.java))
            }
            R.id.change_mode_button -> {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                cameraSource?.release()
                transaction?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                transaction?.replace(R.id.fragments_frame, CameraFragment(), "CameraFragment")
                transaction?.commit()
            }
        }
    }

    private fun startCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            flashButton?.isSelected = false
            preview?.stop()
        }
    }

    private fun setUpBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior?.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    Log.d(TAG, "Bottom sheet new state: $newState")
                    bottomSheetScrimView?.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                    graphicOverlay?.clear()

                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                        BottomSheetBehavior.STATE_COLLAPSED,
                        BottomSheetBehavior.STATE_EXPANDED,
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> slidingSheetUpFromHiddenState = false
                        BottomSheetBehavior.STATE_DRAGGING, BottomSheetBehavior.STATE_SETTLING -> {
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val searchedObject = workflowModel!!.searchedObject.value
                    if (searchedObject == null || java.lang.Float.isNaN(slideOffset)) {
                        return
                    }

                    val graphicOverlay = graphicOverlay ?: return
                    val bottomSheetBehavior = bottomSheetBehavior ?: return
                    val collapsedStateHeight = Math.min(bottomSheetBehavior.peekHeight, bottomSheet.height)
                    val bottomBitmap = objectThumbnailForBottomSheet ?: return
                    if (slidingSheetUpFromHiddenState) {
                        val thumbnailSrcRect = graphicOverlay.translateRect(searchedObject.boundingBox)
                        bottomSheetScrimView?.updateWithThumbnailTranslateAndScale(
                            bottomBitmap,
                            collapsedStateHeight,
                            slideOffset,
                            thumbnailSrcRect)
                    } else {
                        bottomSheetScrimView?.updateWithThumbnailTranslate(
                            bottomBitmap, collapsedStateHeight, slideOffset, bottomSheet)
                    }
                }
            })

        bottomSheetScrimView = view?.findViewById<BottomSheetScrimView>(R.id.bottom_sheet_scrim_view).apply {
            view?.setOnClickListener(this@ObjectDetectionFragment)
        }

        bottomSheetTitleView = view?.findViewById(R.id.bottom_sheet_title)
        productRecyclerView = view?.findViewById<RecyclerView>(R.id.product_recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ProductAdapter(ImmutableList.of())
            }
        }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java).apply {

            // Observes the workflow state changes, if happens, update the overlay view indicators and
            // camera preview state.
            workflowState.observe(this@ObjectDetectionFragment, Observer { workflowState ->
                if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                    return@Observer
                }
                currentWorkflowState = workflowState
                Log.d(TAG, "Current workflow state: ${workflowState.name}")

                if (PreferenceUtils.isAutoSearchEnabled(context!!)) {
                    stateChangeInAutoSearchMode(workflowState)
                } else {
                    stateChangeInManualSearchMode(workflowState)
                }
            })

            // Observes changes on the object to search, if happens, fire product search request.
            objectToSearch.observe(this@ObjectDetectionFragment, Observer { detectObject ->
                searchEngine!!.search(detectObject) {
                        detectedObject, products -> workflowModel?.onSearchCompleted(detectedObject, products)
                }
            })

            // Observes changes on the object that has search completed, if happens, show the bottom sheet
            // to present search result.
            searchedObject.observe(this@ObjectDetectionFragment, Observer { nullableSearchedObject ->
                if (currentWorkflowState == WorkflowState.DETECTED || currentWorkflowState == WorkflowState.SEARCHED) {
                    val searchedObject = nullableSearchedObject ?: return@Observer
                    val productList = searchedObject.productList

                    objectThumbnailForBottomSheet = searchedObject.getObjectThumbnail()
                    GlobalScope.launch(Dispatchers.IO) {
                        val file = bitmapToFile(searchedObject.getObjectThumbnail())
                        val fbody = RequestBody.create(MediaType.parse("image/*"), file);
                        val part = MultipartBody.Part.createFormData("file", file.name, fbody)
                        networkDataSource?.searchFruit(
                            part, object : NetworkDataSource.Callback {
                                override fun onTimeoutException() {
                                    Log.e("HTTP", "Wrong answer.")
                                    Toast.makeText(
                                        context!!, "Проблемы с интернетом!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                                }

                                override fun onException() {
                                    Log.e("HTTP", "EXCEPTION CAUGHT.")
                                    Toast.makeText(
                                        context!!, "Неизвестная ошибка",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                                }

                                override fun onNoConnectivityException() {
                                    Log.e("HTTP", "Wrong answer.")
                                    Toast.makeText(
                                        context!!, "Проблемы с интернетом!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    NetworkDataSource.DefaultCallback(context!!)
                                        .onNoConnectivityException()
                                    workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                                }

                                override fun onHttpException() {
                                    Log.e("HTTP", "Wrong answer.")
                                    Toast.makeText(
                                        context!!, "Продукт не найден!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                                }
                            })
                    }
                    bottomSheetTitleView?.text = resources
                        .getQuantityString(
                            R.plurals.bottom_sheet_title, productList.size, productList.size
                        )
//                    productRecyclerView?.adapter = ProductAdapter(productList)
                    slidingSheetUpFromHiddenState = true
                    bottomSheetBehavior?.peekHeight =
                        preview?.height?.div(2) ?: BottomSheetBehavior.PEEK_HEIGHT_AUTO
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            })
        }
    }

    private fun bitmapToFile(bitmap:Bitmap): File {
        // Get the context wrapper
//        val wrapper = ContextWrapper(context)

        // Initialize a new file instance to save bitmap object
//        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
//        file = File(file,"${UUID.randomUUID()}.jpg")
        val file = File(context!!.cacheDir, "" + System.currentTimeMillis() + ".jpg")
        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        return file
    }

    private fun stateChangeInAutoSearchMode(workflowState: WorkflowState) {
        val wasPromptChipGone = promptChip!!.visibility == View.GONE

        searchButton?.visibility = View.GONE
        searchProgressBar?.visibility = View.GONE
        when (workflowState) {
            WorkflowState.DETECTING, WorkflowState.DETECTED, WorkflowState.CONFIRMING -> {
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(
                    if (workflowState == WorkflowState.CONFIRMING)
                        R.string.prompt_hold_camera_steady
                    else
                        R.string.prompt_point_at_an_object)
                startCameraPreview()
            }
            WorkflowState.CONFIRMED -> {
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(R.string.prompt_searching)
                stopCameraPreview()
            }
            WorkflowState.SEARCHING -> {
                searchProgressBar?.visibility = View.VISIBLE
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(R.string.prompt_searching)
                stopCameraPreview()
            }
            WorkflowState.SEARCHED -> {
                promptChip?.visibility = View.GONE
                stopCameraPreview()
            }
            else -> promptChip?.visibility = View.GONE
        }

        val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
        if (shouldPlayPromptChipEnteringAnimation && promptChipAnimator?.isRunning == false) {
            promptChipAnimator?.start()
        }
    }


    private fun stateChangeInManualSearchMode(workflowState: WorkflowState) {
        val wasPromptChipGone = promptChip?.visibility == View.GONE
        val wasSearchButtonGone = searchButton?.visibility == View.GONE

        searchProgressBar?.visibility = View.GONE
        when (workflowState) {
            WorkflowState.DETECTING, WorkflowState.DETECTED, WorkflowState.CONFIRMING -> {
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(R.string.prompt_point_at_an_object)
                searchButton?.visibility = View.GONE
                startCameraPreview()
            }
            WorkflowState.CONFIRMED -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.VISIBLE
                searchButton?.isEnabled = true
                searchButton?.setBackgroundColor(Color.WHITE)
                startCameraPreview()
            }
            WorkflowState.SEARCHING -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.VISIBLE
                searchButton?.isEnabled = false
                searchButton?.setBackgroundColor(Color.DKGRAY)
                searchProgressBar!!.visibility = View.VISIBLE
                stopCameraPreview()
            }
            WorkflowState.SEARCHED -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.GONE
                stopCameraPreview()
            }
            else -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.GONE
            }
        }

        val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
        promptChipAnimator?.let {
            if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
        }

        val shouldPlaySearchButtonEnteringAnimation = wasSearchButtonGone && searchButton?.visibility == View.VISIBLE
        searchButtonAnimator?.let {
            if (shouldPlaySearchButtonEnteringAnimation && !it.isRunning) it.start()
        }
    }

    companion object {
        private const val TAG = "ObjectDetectionFragment"
    }

}

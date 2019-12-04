package com.darx.foodscaner.fragments


import android.Manifest
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.darx.foodscaner.R
import com.darx.foodscaner.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.hardware.Camera
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.google.common.base.Objects
import com.darx.foodscaner.camerafragment.camera.GraphicOverlay
import com.darx.foodscaner.camerafragment.camera.WorkflowModel
import com.darx.foodscaner.camerafragment.camera.WorkflowModel.WorkflowState
import com.darx.foodscaner.camerafragment.barcodedetection.BarcodeField
import com.darx.foodscaner.camerafragment.barcodedetection.BarcodeProcessor
import com.darx.foodscaner.camerafragment.barcodedetection.BarcodeResultFragment
import com.darx.foodscaner.camerafragment.camera.CameraSource
import com.darx.foodscaner.camerafragment.camera.CameraSourcePreview
import java.io.IOException
import android.content.pm.PackageManager
import android.view.View.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.darx.foodscaner.camerafragment.settings.SettingsActivity
import com.darx.foodscaner.database.ProductViewModel
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSource
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.activity_live_barcode_kotlin.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.top_action_bar_in_live_camera.*


class CameraFragment : Fragment(), OnClickListener {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_camera, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }

    init {
        Log.d("TEST", "TEST CAMERA")
    }

    private var CAMERA_REQUEST = 1;

    private var camera: Camera? = null
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowState? = null
    private var promtChipShown: Boolean = false
    private var changeModeButton: View? = null

    private var barcodeField = BarcodeField("", "")

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var productViewModel: ProductViewModel? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                onReadyToWork()
            } else {
                Toast.makeText(context!!, "We really need this permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?  {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.activity_live_barcode_kotlin, container, false)
//            view.tutorial_button.setOnClickListener {
//                val intent = Intent(this.context, WelcomeWizardActivity::class.java)
//                startActivity(intent)
//            }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                context!!, "Дайте доступ к камере!",
                Toast.LENGTH_SHORT
            ).show()
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )
            no_permission_stub.visibility = VISIBLE
        } else {
            onReadyToWork()
        }
    }

    private fun onReadyToWork() {
        no_permission_stub.visibility = GONE
        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        networkDataSource?.product?.observe(this, Observer {
            barcodeField.label = it.name ?: ""
            barcodeField.value = it.description ?: ""
            it.scanned = true
            productViewModel?.upsert_(it)

            BarcodeResultFragment.show(activity!!.supportFragmentManager, barcodeField, it)
        })

    }

    override fun onStop() {
        super.onStop()
        cameraSource?.release()
        cameraSource = null
    }

    override  fun onStart() {
        super.onStart()
        preview = getView()?.findViewById(R.id.camera_preview)
        graphicOverlay = getView()?.findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay)?.apply {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST
                )
            }
//            getView()?.setOnClickListener(this@CameraFragment)

//            if (cameraSource == null) {
//                FoodApp.instance.cameraSource =  CameraSource(this)
//                cameraSource = FoodApp.instance.cameraSource
//            }
            cameraSource = CameraSource(this)
        }

        promptChip = getView()?.findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(context, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                setTarget(promptChip)
            }

        settingsButton = settings_button
        settingsButton?.visibility = INVISIBLE

        changeModeButton = change_mode_button
        changeModeButton?.setOnClickListener(this@CameraFragment)

        flashButton = getView()?.findViewById<View>(R.id.flash_button)
        flashButton?.setOnClickListener(this)

        setUpWorkflowModel()

    }

    override fun onResume() {
        super.onResume()

        workflowModel?.markCameraFrozen()
//        settingsButton?.isEnabled = false
        currentWorkflowState = WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, workflowModel!!))
        workflowModel?.setWorkflowState(WorkflowState.DETECTING)
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
//        cameraSource?.release()
//        cameraSource = null
    }

    override fun onClick(view: View) {
        val id = view.id
        when (view.id) {
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        this.cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        this.cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }
            R.id.settings_button -> {
//                settingsButton?.isEnabled = false
                startActivity(Intent(context, SettingsActivity::class.java))
            }
            R.id.change_mode_button -> {
                var frag = ObjectDetectionFragment()
                var tag = "OBJECT"
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                cameraSource?.release()
                transaction?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                transaction?.replace(R.id.fragments_frame, frag, tag)
                transaction?.commit()
            }
        }
//        fragment = ObjectDetectionFragment()
//        if (fragment != null) {
//            val transaction = fragmentManager?.beginTransaction()
//            transaction?.replace(R.id.camera_preview, ObjectDetectionFragment())
//            transaction?.commit()
//        }
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

    private fun isDigit(name: String): Boolean {
        var chars = name.toCharArray();

        for (char in chars) {
            if(Character.isLetter(char)) {
                return false;
            }
        }

        return true;
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(activity!!).get(WorkflowModel::class.java)

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d(TAG, "Current workflow state: ${currentWorkflowState!!.name}")

            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                WorkflowState.DETECTING -> {
                    promtChipShown = false
                    promptChip?.visibility = VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                WorkflowState.SEARCHING -> {
                    promptChip?.visibility = VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                WorkflowState.DETECTED, WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null && !promtChipShown && (currentWorkflowState == WorkflowState.DETECTED || currentWorkflowState == WorkflowState.SEARCHED)) {
                this.promtChipShown = true
                GlobalScope.launch(Dispatchers.Main) {
                    if (isDigit((barcode.rawValue!!))) {
                        networkDataSource?.fetchProductByBarcode(barcode.rawValue!!.toLong(), object : NetworkDataSource.Callback {
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
                                NetworkDataSource.DefaultCallback(context!!).onNoConnectivityException()
                                workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                            }

                            override fun onHttpException() {
                                Log.e("HTTP", "Wrong answer.")
                                Toast.makeText(
                                    context!!, "Продукт не найден!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                AddProductFragment.show(activity!!.supportFragmentManager, barcode.rawValue!!.toLong())
                                workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                            }
                        })
                    } else {
                        Toast.makeText(
                            context!!, "Нельзя сканировать QR код",
                            Toast.LENGTH_SHORT
                        ).show()
                        workflowModel?.setWorkflowState(WorkflowState.DETECTING)
                    }
                }
            }
        })
    }

    companion object {
        private const val TAG = "CameraFragment"
    }

}

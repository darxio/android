package com.darx.foodscaner.fragments
import android.Manifest
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.darx.foodscaner.R
import com.darx.foodscaner.services.ApiService
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Instrumentation
import android.content.Intent
import android.hardware.Camera
import android.util.Log
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
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
import com.darx.foodscaner.camerafragment.settings.SettingsActivity
import java.io.IOException
import java.util.ArrayList
import androidx.core.app.ActivityCompat.requestPermissions
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat





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

    private var CAMERA_REQUEST = 1;

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowState? = null
    private var mInstrumentation: Instrumentation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?  {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.activity_live_barcode_kotlin)
        return inflater.inflate(R.layout.activity_live_barcode_kotlin, container, false)

    }

    override  fun onStart() {
        super.onStart()
        var view = getView()
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
            getView()?.setOnClickListener(this@CameraFragment)
            cameraSource = CameraSource(this)
        }

        promptChip = getView()?.findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(context, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                setTarget(promptChip)
            }

        flashButton = getView()?.findViewById<View>(R.id.flash_button)
        flashButton?.setOnClickListener(this)

        settingsButton = getView()?.findViewById<View>(R.id.settings_button)
        settingsButton?.setOnClickListener(this)

        setUpWorkflowModel()

    }

    override fun onResume() {
        super.onResume()

        workflowModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
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
        cameraSource?.release()
        cameraSource = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }
            R.id.settings_button -> {
                settingsButton?.isEnabled = false
                val intent = Intent (activity, SettingsActivity::class.java)
                activity?.startActivity(intent)

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
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                WorkflowState.SEARCHING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                WorkflowState.DETECTED, WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null) {
                val barcodeFieldList = ArrayList<BarcodeField>()
                barcodeFieldList.add(BarcodeField("Raw Value", barcode.rawValue ?: ""))
                BarcodeResultFragment.show(activity!!.supportFragmentManager, barcodeFieldList)
            }
        })
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
    }

}

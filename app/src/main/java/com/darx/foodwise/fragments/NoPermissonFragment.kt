package com.darx.foodwise.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.darx.foodwise.MainActivity
import com.darx.foodwise.R
import kotlinx.android.synthetic.main.no_permission_fragment.*

class NoPermissonFragment() : Fragment() {
    private var CAMERA_REQUEST = 1;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.no_permission_fragment, container, false)

        return view
    }

    override fun onStart() {
        super.onStart()
        permission_button.setOnClickListener(View.OnClickListener {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )

        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                val frag= CameraFragment()
                (activity as MainActivity).cameraFragment = frag
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragments_frame, frag)?.commit()
            } else {
                Toast.makeText(context, "We really need this permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
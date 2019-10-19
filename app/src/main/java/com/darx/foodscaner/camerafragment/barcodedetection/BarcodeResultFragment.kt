/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.darx.foodscaner.camerafragment.barcodedetection

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.darx.foodscaner.R
import com.darx.foodscaner.camerafragment.camera.WorkflowModel
import com.darx.foodscaner.camerafragment.camera.WorkflowModel.WorkflowState
import kotlinx.android.synthetic.main.product_item.*

/** Displays the bottom sheet to present barcode fields contained in the detected barcode.  */
class BarcodeResultFragment : BottomSheetDialogFragment() {

    var barcodeField: BarcodeField? = null

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val view = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup)

        val arguments = arguments
        var barcodeField: BarcodeField =
            if (arguments?.containsKey(ARG_BARCODE_FIELD_LIST) == true) {
                arguments.getSerializable(ARG_BARCODE_FIELD_LIST) as BarcodeField? ?: BarcodeField("", "")
            } else {
                Log.e(TAG, "No barcode field list passed in!")
                BarcodeField("", "")
            }

        val title: TextView = view.findViewById(R.id.productName)
        title.text = barcodeField.label

        return view
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        activity?.let {
            // Back to working state after the bottom sheet is dismissed.
            var wfm = ViewModelProviders.of(it).get<WorkflowModel>(
                WorkflowModel::class.java)
            wfm.setWorkflowState(WorkflowState.DETECTING)
        }
        super.onDismiss(dialogInterface)
    }

    companion object {

        private const val TAG = "BarcodeResultFragment"
        private const val ARG_BARCODE_FIELD_LIST = "arg_barcode_field_list"

        fun show(fragmentManager: FragmentManager, barcodeField: BarcodeField) {
            val barcodeResultFragment = BarcodeResultFragment()
            barcodeResultFragment.arguments = Bundle().apply {
                putSerializable(ARG_BARCODE_FIELD_LIST, barcodeField)
            }
            barcodeResultFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as BarcodeResultFragment?)?.dismiss()
        }
    }
}

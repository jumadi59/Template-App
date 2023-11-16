package com.app.common.ui.choseimage

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.common.databinding.FragmentChoseImageDialogBinding
import com.app.core.extensions.formatDateTime
import com.app.core.util.PermissionListener
import com.app.core.util.PermissionManager
import com.app.core.util.PermissionType
import com.app.core.util.Util
import com.app.core.util.cekPermission
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ChoseImageDialogFragment : BottomSheetDialogFragment() {


    companion object {
        fun show(activity: FragmentActivity, callbackResult: (String, Uri?) -> Unit) =
            ChoseImageDialogFragment().apply {
                this.callbackResult = callbackResult
                show(activity.supportFragmentManager, "ChoseImageDialogFragment")
            }

        fun show(fragment: Fragment, callbackResult: (String, Uri?) -> Unit) =
            ChoseImageDialogFragment().apply {
                this.callbackResult = callbackResult
                show(fragment.childFragmentManager, "ChoseImageDialogFragment")
            }
    }

    private var callbackResult: (String,Uri?) -> Unit = { _: String, _: Uri? -> }
    var callbackPermissionDenied: ((List<String>) -> Unit?)? = null

    private var _binding: FragmentChoseImageDialogBinding? = null
    private val binding get() = _binding!!
    private var isShowButtonDelete = true

    private val resultGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data?.data != null ) {
            val uri = result.data!!.data
            Log.e(this::class.java.simpleName, "$uri")
            if (uri != null) {
                val fileName = Util.queryName(requireActivity().contentResolver, uri)
                requireActivity().contentResolver.openInputStream(uri).use {
                    callbackResult.invoke(fileName, uri)
                    dismiss()
                }
            }
        }
    }

    private var imageUri: Uri? = null

    private val resultCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (imageUri != null) {
                val fileName = Util.queryName(requireActivity().contentResolver, imageUri!!)
                callbackResult.invoke(fileName, imageUri!!)
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChoseImageDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.gallery.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P){
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                resultGalleryLauncher.launch(Intent.createChooser(intent, "Select Upload Picture"))
            } else {
                cekPermission(PermissionType.STORAGE, object : PermissionListener {
                    override fun onPermissionsChecked() {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        resultGalleryLauncher.launch(Intent.createChooser(intent, "Select Upload Picture"))
                    }

                    override fun onPermissionsDenied() {
                        callbackPermissionDenied?.invoke(arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                    }

                })
            }
        }

        binding.camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "dd-MM-yyyy HH:mm".formatDateTime() + ".jpg")
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera")
            val permissions = if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) arrayListOf(Manifest.permission.CAMERA) else arrayListOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            PermissionManager.cekPermission(this, permissions, object : PermissionListener {
                override fun onPermissionsChecked() {
                    imageUri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    resultCameraLauncher.launch(intent)
                }

                override fun onPermissionsDenied() {
                    callbackPermissionDenied?.invoke(permissions)
                }

            })
        }

        binding.btnDelete.isVisible = isShowButtonDelete
        binding.btnDelete.setOnClickListener {
            dismiss()
            callbackResult.invoke("", null)
        }
    }

    fun showButtonDelete(isShowButtonDelete: Boolean) : ChoseImageDialogFragment {
        this.isShowButtonDelete = isShowButtonDelete
        return this
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
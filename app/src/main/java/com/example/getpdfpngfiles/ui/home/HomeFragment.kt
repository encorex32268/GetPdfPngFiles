package com.example.getpdfpngfiles.ui.home

import android.Manifest
import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.getpdfpngfiles.databinding.FragmentHomeBinding
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object{
        const val REQUEST_READ_EXTERNAL_STORAGE = 101
        const val PICKFILE_REQUEST_CODE = 102
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.apply {

            homeButton.setOnClickListener {
                if (requireActivity().checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED){

                    gotoFiles()

                }else{

                    Log.d("TAG", "onCreateView: without permission ")
                    requestLocationPermission()
                }



            }


        }





        return root
    }

    private fun gotoFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        requireActivity().startActivityFromFragment(this,intent, PICKFILE_REQUEST_CODE)

    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setMessage("此應用程式，需要位置權限才能正常使用")
                .setPositiveButton("確定") { _, _ ->
                    requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
                .setNegativeButton("取消") { _, _ ->
                    requestLocationPermission()
                }
                .show()
            Log.d("TAG", "shouldShowRequestPermissionRationale: ")

        } else {
            Log.d("TAG", "requestLocationPermission: ")
            requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //get files
            }else{
                //權限被永久拒絕
                Toast.makeText(requireContext(), "位置權限已被關閉，功能將會無法正常使用", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(requireContext())
                    .setTitle("開啟位置權限")
                    .setMessage("此應用程式，位置權限已被關閉，需開啟才能正常使用")
                    .setPositiveButton("確定") { _, _ ->
                        val intent = Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        requireActivity().startActivityFromFragment(this,intent, REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
                    .show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if (requireActivity().checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED){

                }else{

                    requestLocationPermission()
                }
            }
            PICKFILE_REQUEST_CODE-> {
                if (resultCode == RESULT_OK){
                    val path = data?.data?.path
                    binding.fileNameTextView.text = path



                }

            }
        }
    }


}


package com.meranged.schoolschedule.ui.teacherdetails

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.databinding.TeacherDetailsFragmentBinding
import com.meranged.schoolschedule.saveImageToInternalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream


class TeacherDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = TeacherDetailsFragment()
    }

    private lateinit var teacherDetailsViewModel: TeacherDetailsViewModel
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_GALLERY = 2
    var isPictureSet = false
    var pic_path: Uri? = null
    lateinit var l_imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<TeacherDetailsFragmentBinding>(
            inflater,
            R.layout.teacher_details_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = TeacherDetailsFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = TeacherDetailsViewModelFactory(arguments!!.teacherId, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        teacherDetailsViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(TeacherDetailsViewModel::class.java)


        binding.setLifecycleOwner(this)

        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.teacherDetailsViewModel = teacherDetailsViewModel

        binding.saveButton.setOnClickListener{
            uiScope.launch {
                teacherDetailsViewModel.updateTeacher(pic_path)
            }
            view!!.findNavController()
                .navigate(TeacherDetailsFragmentDirections
                    .actionTeacherDetailsFragmentToNavigationMyTeachers())
        }


        binding.deleteButton.setOnClickListener{
            uiScope.launch {
                teacherDetailsViewModel.deleteTeacher()
            }
            view!!.findNavController()
                .navigate(TeacherDetailsFragmentDirections
                    .actionTeacherDetailsFragmentToNavigationMyTeachers())
        }


        binding.imageView.setOnClickListener{


            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle(R.string.dialog_option_get_photo_title)

            builder.setPositiveButton(R.string.dialog_option_get_photo_from_camera) { dialog, which ->
                getPictureFromCamera()
            }

            builder.setNegativeButton(R.string.dialog_option_get_photo_from_gallery) { dialog, which ->
                getPictureFromGallery()
            }

            builder.show()

        }


        l_imageView = binding.imageView

        teacherDetailsViewModel.teacher.observe(this, Observer{
            if (it.photo_path.isNotEmpty()){
                //val arrayInputStream = ByteArrayInputStream(it.photo)
                //l_imageView.setImageBitmap(BitmapFactory.decodeStream(arrayInputStream))
                l_imageView.setImageURI(Uri.parse(it.photo_path))
            } else {
                Log.i("Teacher=", it.toString())
            }
        })

        return binding.root
    }

    private fun getPictureFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun getPictureFromGallery(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var imageBitmap:Bitmap

        if (data != null) {

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                if (data.extras != null) {
                    imageBitmap = data.extras.get("data") as Bitmap
                    //imageBitmap = resizeBitmap(imageBitmap, 800)
                    l_imageView.setImageBitmap(imageBitmap)
                    pic_path = saveImageToInternalStorage(imageBitmap)
                    isPictureSet = true
                }
            }

            if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
                pic_path = data!!.data
                //imageBitmap = MediaStore.Images.Media.getBitmap(activity!!.applicationContext.contentResolver, pic_path);
                //imageBitmap = resizeBitmap(imageBitmap, 800)
                l_imageView.setImageURI(pic_path)
                isPictureSet = true
            }

        }

    }

    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) { // if image height already smaller than the required height
                    return source
                }

                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                val result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
                return result
            } else {
                if (source.width <= maxLength) { // if image width already smaller than the required width
                    return source
                }

                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()

                val result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                return result
            }
        } catch (e: Exception) {
            return source
        }
    }


}

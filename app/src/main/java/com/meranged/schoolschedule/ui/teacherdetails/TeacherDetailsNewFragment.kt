package com.meranged.schoolschedule.ui.teacherdetails

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.databinding.TeacherDetailsNewFragmentBinding
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream


class TeacherDetailsNewFragment : Fragment() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_GALLERY = 2
    lateinit var photo:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<TeacherDetailsNewFragmentBinding>(
            inflater,
            R.layout.teacher_details_new_fragment, container, false)


        binding.setLifecycleOwner(this)



        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        binding.imageView.setOnClickListener {
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

        photo = binding.imageView

        binding.saveButton.setOnClickListener{

            if (binding.name1EditText.text.isNotEmpty()
            or binding.name2EditText.text.isNotEmpty()
            or binding.name3EditText.text.isNotEmpty()){
                var teacher = Teacher()
                teacher.firstName = binding.name1EditText.text.toString()
                teacher.secondName = binding.name2EditText.text.toString()
                teacher.thirdName = binding.name3EditText.text.toString()


                if (photo != null){
                    val stream = ByteArrayOutputStream()
                    val bm = (photo.getDrawable() as BitmapDrawable).bitmap

                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    teacher.photo = stream.toByteArray()
                }

                uiScope.launch {
                    insertTeacher(teacher)
                }
                view!!.findNavController()
                    .navigate(TeacherDetailsNewFragmentDirections
                        .actionTeacherDetailsNewFragmentToNavigationMyTeachers())
            }
        }


        return binding.root
    }

    suspend fun insertTeacher(teacher: Teacher){
        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao

        withContext(Dispatchers.IO) {
            dataSource.insert(teacher)
        }
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageBitmap = data!!.extras.get("data") as Bitmap
            imageBitmap = resizeBitmap(imageBitmap, 800)
            photo.setImageBitmap(imageBitmap)
        }

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = data!!.data
            imageBitmap = MediaStore.Images.Media.getBitmap(activity!!.applicationContext.contentResolver, selectedImage);
            imageBitmap = resizeBitmap(imageBitmap, 800)
            photo.setImageBitmap(imageBitmap)
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


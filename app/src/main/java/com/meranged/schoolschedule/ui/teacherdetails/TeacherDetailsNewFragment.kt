package com.meranged.schoolschedule.ui.teacherdetails

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
            dispatchTakePictureIntent()
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

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            photo.setImageBitmap(imageBitmap)
        }
    }
}


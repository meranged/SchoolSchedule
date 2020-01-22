package com.meranged.schoolschedule.ui.teacherdetails

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.databinding.TeacherDetailsFragmentBinding
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
                teacherDetailsViewModel.updateTeacher()
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
            dispatchTakePictureIntent()
        }


        l_imageView = binding.imageView

        teacherDetailsViewModel.teacher.observe(this, Observer{
            if (it.photo != null){
                val arrayInputStream = ByteArrayInputStream(it.photo)
                l_imageView.setImageBitmap(BitmapFactory.decodeStream(arrayInputStream))
            } else {
                Log.i("Teacher=", it.toString())
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
     //   teacherDetailsViewModel = ViewModelProviders.of(this).get(TeacherDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }



    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            teacherDetailsViewModel.setPicture(imageBitmap)

        }
    }


}

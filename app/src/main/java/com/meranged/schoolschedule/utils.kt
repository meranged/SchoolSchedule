package com.meranged.schoolschedule

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

fun getHoursFromMins(mins:Int):Int {

    return mins/60
}

fun getMinsFromMins(mins:Int):Int {

    val hours:Int = mins/60

    val l_mins = mins - hours*60

    return l_mins
}

fun convertIntTo00(intValue: Int): String{

    var ls = intValue.toString()
    if (ls.length == 1){
        ls = "0${ls}"
    }

    return ls
}

fun getRightDayNumber(wrongDay:Int):Int{
    if (wrongDay == 1)
        return 7
    else
        return wrongDay -1
}

fun getStringFromDaysList(daysList:List<Int>, isString:Boolean = false):String{

    var stringDays = ""
    for(item in daysList){
        if (stringDays.isNotEmpty()) {
            stringDays += ", "
        }

        if (isString) {
            stringDays += App.context!!.resources.getStringArray(R.array.weekdays_array)[item - 1]
        } else {
            stringDays += item.toString()
        }
    }
    return stringDays
}

// Method to save an image to internal storage
fun saveImageToInternalStorage(picture: Bitmap): Uri {

    var pictureToStore = picture
    val wrapper = ContextWrapper(App.context)

    // Initializing a new file
    // The bellow line return a directory in internal storage
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)

    // Create a file to save the image
    file = File(file, "${UUID.randomUUID()}.jpg")

    try {
        // Get the file output stream
        val stream: OutputStream = FileOutputStream(file)

        // Compress bitmap
        pictureToStore.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // Flush the stream
        stream.flush()

        // Close stream
        stream.close()
    } catch (e: IOException){ // Catch the exception
        e.printStackTrace()
    }

    // Return the saved image uri
    return Uri.parse(file.absolutePath)
}

class ScrollAwareFABBehavior (val recyclerView: RecyclerView, val floatingActionButton: FloatingActionButton) {

    fun start() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    if (floatingActionButton.isShown) {
                        floatingActionButton.hide()
                    }
                } else if (dy < 0) {
                    if (!floatingActionButton.isShown) {
                        floatingActionButton.show()
                    }
                }
            }
        })
    }
}
package com.meranged.schoolschedule

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
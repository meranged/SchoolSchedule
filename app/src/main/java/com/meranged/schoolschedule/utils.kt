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

fun getRightDayNumber(wrongDay:Int):Int{
    if (wrongDay == 1)
        return 7
    else
        return wrongDay -1
}

fun getStringFromDaysList(daysList:List<Int>):String{
    var stringDays = ""
    for(item in daysList){
        if (stringDays.length > 2) {
            stringDays = stringDays + ", "
        }
        stringDays = stringDays + App.context!!.resources.getStringArray(R.array.weekdays_array)[item-1]
    }
    return stringDays
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
package com.meranged.schoolschedule

fun getHoursFromMins(mins:Int):Int {
    var hours:Int = 0

    hours = mins/60

    return hours
}

fun getMinsFromMins(mins:Int):Int {

    var l_mins:Int = 0

    var hours:Int = mins/60

    l_mins = mins - hours*60

    return l_mins
}

fun convertIntTo00(intValue: Int): String{
    var ls: String = String()

    ls = intValue.toString()
    if (ls.length == 1){
        ls = "0${ls}"
    }

    return ls
}
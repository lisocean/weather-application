@file:JvmName("StandUtils")
package mg.studio.weatherappdesign

import android.annotation.SuppressLint
import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

@SuppressLint("SimpleDateFormat")

fun getWeatherListIndex(weather: Weather) : Int {
    val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
    val time = SimpleDateFormat("HH").format(Date()).toInt()

    val index = weather.list.map{
                it.dt_txt
            }.findIndexed {
        it.startsWith(date) && (it.hour() - time).absoluteValue < 2
    }

    return index ?: 0

}
fun getResourceList(index : Int, weather: Weather) : ArrayList<Int>{
    val arrayList = arrayListOf<Int>()
    var tempIndex = index
    while(arrayList.size < 5){
        arrayList.add(getConditionResource(weather.list[tempIndex].weather[0].id))
        tempIndex += 8
        if (tempIndex > 40) tempIndex = 40
    }

    return arrayList
}

/**
 * 根据天气id获得资源id
 */
private fun getConditionResource(code : Int) : Int = when(code){
        800 -> R.drawable.sunny_small
        in 801..804 -> R.drawable.partly_sunny_small
        in 701..781 -> R.drawable.windy_small
        in 300..500 -> R.drawable.rainy_small
        else ->R.drawable.rainy_up
    }
fun getWeekList() : ArrayList<String>{
    var today=  Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val temp = mapOf(1 to "SUN", 2 to "MON" , 3 to "TUE", 4 to "WED", 5 to "THU", 6 to "FRI", 7 to "SAT")
    val arrayList = arrayListOf<String>()
    while (arrayList.size < 5){
        temp[today]?.let {
            arrayList.add(it)
        }
        if(++today == 8) today = 1
    }
    return arrayList
}

@SuppressLint("SimpleDateFormat")
fun getDate() : String = SimpleDateFormat("dd/MM/yyyy").format(Date())

/**
 * 获取index
 */
private inline fun <T> Iterable<T>.findIndexed(predicate: (T) -> Boolean) : Int?{
    forEachIndexed { index, element ->
        if(predicate(element))
            return index
    }
    return null
}
private fun String.hour():Int = substring(lastIndexOf(' ') + 1).substring(0, 2).toInt()


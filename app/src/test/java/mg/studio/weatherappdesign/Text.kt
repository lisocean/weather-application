package mg.studio.weatherappdesign

import android.support.v4.media.MediaMetadataCompat
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

public class Test(){
    @Test
    fun ttt(){
        val date = SimpleDateFormat("HH:mm:ss").format(Date())
        val today = date.substring(date.lastIndexOf(' ') + 1).substring(0, 2)
        println(date)
        // val int = today.s
       // println(int)
    }
}
package com.example.kakaotalktospeech

import android.app.Application
import java.io.File

class ContactsManager : Application() {

    //context developer https://developer.android.com/reference/android/content/Context#constants
    private val context = Application().applicationContext

    //앱 별 파일에 액세스 developer https://developer.android.com/training/data-storage/app-specific?hl=ko#internal-access-files
    //file developer https://developer.android.com/reference/java/io/File?hl=ko
    //저장소 별 고유경로 http://egloos.zum.com/pavecho/v/7204359
    //코틀린의 파일 입출력 다양한 방법 https://blog.naver.com/PostView.nhn?blogId=horajjan&logNo=221568409591&from=search&redirect=Log&widgetTypeCall=true&directAccess=false
    private val filename = "Contacts"
    private val file = File(context.filesDir, filename)
    private val filepath = "${context.filesDir}/${filename}"

    //key-value,,
    // 해쉬맵 추가 삭제 https://kkh0977.tistory.com/648
    // 해쉬맵 key로 정렬 https://velog.io/@changhee09/Kotlin-Map-%EC%A0%95%EB%A0%AC
    // 해쉬맵 value로 정렬 https://notepad96.tistory.com/entry/map-2
    private var map = HashMap<String, Int>()


    fun isContactsFileExists(): Boolean{
        if(file.exists()) return true else return false
    }
    fun makeContactsFile(){
        if(!isContactsFileExists()) file.createNewFile()
    }
    fun deleteContactsFile(){
        if(!isContactsFileExists()) file.delete()
    }

    //이슈 사항     >>>      같은 이름의 유저는....??????
    fun importContactsFile(){
        if(!isContactsFileExists()) return

        val bufferedReader = file.bufferedReader()
        val lineList = mutableListOf<String>()
        bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
        map.clear()
        lineList.forEach {
            val arr = it.split(":")
            map.put(arr[0],arr[1].toInt())
        }
        bufferedReader.close()
    }
    fun exportContactsFile(){
        if(!isContactsFileExists()) return

        file.writeText("")
        for ((key, value) in map) {
            val str = "\n${key}:${value}"
            file.appendText(str)
        }
    }
}
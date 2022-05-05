package com.example.kakaotalktospeech

import android.app.Application
import android.content.Context
import android.util.Log
import java.io.File

class ContactsManager : Application() {
    //context developer
    // https://minggu92.tistory.com/31
    // https://developer.android.com/reference/android/content/Context#constants
    // https://namget.tistory.com/entry/%EC%BD%94%ED%8B%80%EB%A6%B0-mvvm%ED%8C%A8%ED%84%B4-%EC%86%8D-application-context-%EA%B0%80%EC%A0%B8%EC%98%A4%EA%B8%B0
    lateinit var context: Context

    //앱 별 파일에 액세스 developer https://developer.android.com/training/data-storage/app-specific?hl=ko#internal-access-files
    //file developer https://developer.android.com/reference/java/io/File?hl=ko
    //저장소 별 고유경로 http://egloos.zum.com/pavecho/v/7204359
    //코틀린의 파일 입출력 다양한 방법 https://blog.naver.com/PostView.nhn?blogId=horajjan&logNo=221568409591&from=search&redirect=Log&widgetTypeCall=true&directAccess=false
    private lateinit var filename : String
    private lateinit var file : File
    private lateinit var filepath : String

    //key-value,,
    // 해쉬맵 추가 삭제 https://kkh0977.tistory.com/648
    // 해쉬맵 key로 정렬 https://velog.io/@changhee09/Kotlin-Map-%EC%A0%95%EB%A0%AC
    // 해쉬맵 value로 정렬 https://notepad96.tistory.com/entry/map-2
    init {
        context = MainActivity.context()

        filename = "Contacts"
        file = File(context.filesDir, filename)
        filepath = "${context.filesDir}/${filename}"
    }

    companion object{
        fun putWhiteList(name : String){
            if(SettingManager.whiteList.containsKey(name))
                SettingManager.whiteList.put(name, SettingManager.whiteList.get(name)!!.toInt() + 1)
            else
                SettingManager.whiteList.put(name, 1)
        }
    }

    fun isContactsFileExists(): Boolean{
        Log.d("ContactsManager", "isContactsFileExists() : " + file.exists())
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
        if(!isContactsFileExists()) {
            Log.d("ContactsManager", "importContactsFile() error!!!")
            return
        }

        val bufferedReader = file.bufferedReader()
        val lineList = mutableListOf<String>()
        bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
        SettingManager.whiteList.clear()
        Log.d("ContactsManager", "lineList size : " + lineList.size)
        lineList.forEach {
            Log.d("ContactsManager", "it : " + it)
            val arr = it.split(":")
            SettingManager.whiteList.put(arr[0],arr[1].toInt())
        }
        bufferedReader.close()

        Log.d("ContactsManager", "importContactsFile() has no problem")
    }
    fun exportContactsFile(){
        if(!isContactsFileExists()) {
            Log.d("ContactsManager", "exportContactsFile() error!!!")
            return
        }

        file.writeText("")
        for ((key, value) in SettingManager.whiteList) {
            val str = "${key}:${value}\n"
            Log.d("ContactsManager", "lineList name&cnt : " + str)
            file.appendText(str)
        }

        Log.d("ContactsManager", "exportContactsFile() has no problem")
    }
}
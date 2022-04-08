package com.example.kakaotalktospeech

import android.app.Application
import java.io.File

class ContactsManager : Application() {

    //context developer https://developer.android.com/reference/android/content/Context#constants
    val context = Application().applicationContext
    val filename = "Contacts"

    //key-value,,
    // 해쉬맵 추가 삭제 https://kkh0977.tistory.com/648
    // key로 정렬 https://velog.io/@changhee09/Kotlin-Map-%EC%A0%95%EB%A0%AC
    // value로 정렬 https://notepad96.tistory.com/entry/map-2
    var map = HashMap<String, Int>()

    //앱 별 파일에 액세스 developer https://developer.android.com/training/data-storage/app-specific?hl=ko#internal-access-files
    //file developer https://developer.android.com/reference/java/io/File?hl=ko
    //저장소 별 고유경로 http://egloos.zum.com/pavecho/v/7204359
    val file = File(context.filesDir, filename)

    fun importContacts(){

    }
    fun exportContacts(){

    }
}
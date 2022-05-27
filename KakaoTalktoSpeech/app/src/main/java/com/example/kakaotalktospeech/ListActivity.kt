package com.example.kakaotalktospeech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ListActivity: AppCompatActivity() {
    //private var adapter: RecyclerAdapter? = null
    private var dataList: ArrayList<RecyclerItem> = ArrayList<RecyclerItem>()
    //private var exitbtn : Button? = null

    //RecycleView 참고 출처:
    //https://blog.yena.io/studynote/2017/12/06/Android-Kotlin-RecyclerView1.html

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ListActivity", "onCreate1")
        setContentView(R.layout.activity_whitelist)


        var usefulIntent = Intent(this, UsefulActivity::class.java)
        val backBtn = findViewById<Button>(R.id.btnBackTomain)
        backBtn.setOnClickListener({
            usefulIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(usefulIntent)
        })

        init()

        Log.d("ListActivity", "onCreate2")
    }

    private fun init() {
        Log.d("ListActivity", "init1")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RecyclerAdapter(this, dataList) { item ->
            //RecycleView의 item을 클릭하면 발생하는 이벤트를 정의하는 부분
            Log.d("ListActivity", item.getTitle() + item.getContent())
        }
        recyclerView.adapter = adapter

        //ListView Adapter와는 다르게, RecyclerView Adapter에서는 레이아웃 매니저 (LayoutManager) 를 설정해주어야 한다.
        /* LayoutManager는 RecyclerView의 각 item들을 배치하고,item이 더이상 보이지 않을 때 재사용할 것인지 결정하는 역할을 한다.
        * item을 재사용할 떄, LayoutManager는 Adapter에게 view의 요소를 다른 데이터로 대체할 것인지 물어본다.
        * LayoutManager 사용을 통해 불필요한 findViewById를 수행하지 않아도 되고, 앱 성능을 향상시킬 수 있다.*/
        val lm = LinearLayoutManager(this)
        recyclerView.layoutManager = lm
        recyclerView.setHasFixedSize(true)

        // whiteList에 있는 인원들을 불러와서 dataList에 넣습니다.
        for(hashmapdata in SettingManager.whiteList){
            var data: RecyclerItem = RecyclerItem()
            var dataTitle: String = hashmapdata.key
            var dataContent: Int = hashmapdata.value
            data.setTitle(dataTitle)
            data.setContent(dataContent)

            dataList.add(data)
        }

        //친구 이름 순으로 정렬
        //dataList.sortBy { it.getTitle() }

        //친구 연락 횟수 순으로 정렬
        //dataList.sortBy { it.getContent() }


        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter!!.notifyDataSetChanged()

        Log.d("ListActivity", "init2")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ListActivity", "onDestroy")
    }
}
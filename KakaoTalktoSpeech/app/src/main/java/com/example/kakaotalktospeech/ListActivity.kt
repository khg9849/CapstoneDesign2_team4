package com.example.kakaotalktospeech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ListActivity: AppCompatActivity() {
    var adapter: RecyclerAdapter? = null
    private var dataList: ArrayList<RecyclerItem> = ArrayList<RecyclerItem>()

    //RecycleView 참고 출처:
    //https://blog.yena.io/studynote/2017/12/06/Android-Kotlin-RecyclerView1.html

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ListActivity", "onCreate1")
        setContentView(R.layout.activity_whitelist)

        SettingManager.listActivityInstance = this

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

        val orderbySpinner = findViewById<Spinner>(R.id.order_by)
        orderbySpinner.adapter = ArrayAdapter.createFromResource(this, R.array.itemList, android.R.layout.simple_spinner_item)
        orderbySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    //<item>기본</item>
                    0 -> {
                        dataList.sortBy { it.getResId() }
                        adapter!!.notifyDataSetChanged()
                    }
                    //<item>오름차순</item>
                    1 -> {
                        dataList.sortBy { it.getTitle() }
                        adapter!!.notifyDataSetChanged()
                    }
                    //<item>내림차순</item>
                    2 -> {
                        dataList.sortByDescending { it.getTitle() }
                        adapter!!.notifyDataSetChanged()
                    }
                    //<item>연락횟수 내림차순</item>
                    3 -> {
                        dataList.sortByDescending { it.getContent() }
                        adapter!!.notifyDataSetChanged()
                    }
                }

            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RecyclerAdapter(this, dataList) { item ->
            //RecycleView의 item을 클릭하면 발생하는 이벤트를 정의하는 부분
            var itemState = item.getState()
            item.setState(!itemState) // on/off 이미지를 바꾸기 위해

            val exitList: ArrayList<Int>? = SettingManager.whiteList.get(item.getTitle())
            exitList!!
            if(item.getState() == true) exitList[1] = 1 else exitList[1] = 0 // SettingManager를 변경하여 앱이 종료되도 on/off 설정이 유지되게

            adapter!!.notifyDataSetChanged()
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
            var dataContent: ArrayList<Int> = hashmapdata.value
            data.setTitle(dataTitle)
            data.setContent(dataContent[0])
            if(dataContent[1] == 1)
                data.setState(true)
            else
                data.setState(false)

            dataList.add(data)
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter!!.notifyDataSetChanged()

        Log.d("ListActivity", "init2")
    }

    override fun onResume(){
        super.onResume()
        Log.d("ListActivity", "onResume")
    }

    fun updateList(){
        Log.d("ListActivity", "updateList")

    }

    override fun onDestroy() {
        super.onDestroy()
        SettingManager.listActivityInstance = null
        Log.d("ListActivity", "onDestroy")
    }
}
package com.example.kakaotalktospeech

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//코틀린 RecycleView Click event 처리:
//https://blog.yena.io/studynote/2017/12/07/Android-Kotlin-RecyclerView2.html


class RecyclerAdapter(val context: Context, val listData: ArrayList<RecyclerItem>, val itemClick: (RecyclerItem) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>() {
    // adapter에 들어갈 list 입니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return ItemViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData[position], context)
    }

    override fun getItemCount(): Int {
        // RecyclerView의 총 개수 입니다.
        return listData.size
    }

    fun addItem(data: RecyclerItem) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data)
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    inner class ItemViewHolder(itemView: View, itemClick: (RecyclerItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val textView1: TextView
        private val textView2: TextView
        private val imageView: ImageView

        fun onBind(data: RecyclerItem, context: Context) {
            Log.d("ItemViewHolder", "onBind")
            /* textView1의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/
            val resId = context.resources.getIdentifier(textView1.toString(), "drawable", context.packageName)
            if(data.getState() == true)
                imageView.setImageResource(R.drawable.ic_checkbox_on)
            else
                imageView.setImageResource(R.drawable.ic_checkbox_off)


            textView1.setText(data.getTitle())
            textView2.setText(data.getContent().toString())
            itemView.setOnClickListener { itemClick(data) }
        }

        init {
            Log.d("ItemViewHolder", "init")
            textView1 = itemView.findViewById<TextView>(R.id.textView1)
            textView2 = itemView.findViewById<TextView>(R.id.textView2)
            imageView = itemView.findViewById<ImageView>(R.id.imageView)
        }
    }
}
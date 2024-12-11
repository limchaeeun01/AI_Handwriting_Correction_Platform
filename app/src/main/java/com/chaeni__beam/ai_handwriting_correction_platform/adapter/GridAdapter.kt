package com.chaeni__beam.ai_handwriting_correction_platform.adapter

import com.chaeni__beam.ai_handwriting_correction_platform.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class GridAdapter(private val text: String, private val cellSize: Int) : BaseAdapter() {

    private val gridData: List<String> = text.chunked(1).toMutableList().apply {
        while (size < 35) add("") // 35개의 셀을 유지
    }

    override fun getCount(): Int = 35

    override fun getItem(position: Int): Any = gridData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            // 새로운 View 생성
            view = LayoutInflater.from(parent?.context).inflate(R.layout.grid_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            // 기존 View 재사용
            view = convertView
            holder = view.tag as ViewHolder
        }

        // 각 셀 크기 설정
        val layoutParams = view.layoutParams
        layoutParams.width = cellSize
        layoutParams.height = cellSize
        view.layoutParams = layoutParams

        // 텍스트 설정
        holder.textView.text = gridData[position]
        return view
    }

    private class ViewHolder(view: View) {
        val textView: TextView = view.findViewById(R.id.grid_item_text)
    }
}

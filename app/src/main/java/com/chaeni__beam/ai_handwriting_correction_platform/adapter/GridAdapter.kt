package com.chaeni__beam.ai_handwriting_correction_platform.adapter

import android.content.Context
import android.util.Log
import com.chaeni__beam.ai_handwriting_correction_platform.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

class GridAdapter(
    private val context: Context,
    private val text: String,
    private val cellSize: Int,
    private val selectedFont: String // 선택된 글씨체
) : BaseAdapter() {

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
            view = LayoutInflater.from(parent?.context).inflate(R.layout.grid_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val layoutParams = view.layoutParams
        layoutParams.width = cellSize
        layoutParams.height = cellSize
        view.layoutParams = layoutParams

        // 텍스트 설정
        holder.textView.text = gridData[position]

        // 글씨체 설정
        val typeface = when (selectedFont) {
            "돋움체" -> ResourcesCompat.getFont(context, R.font.dotum)
            "굴림체" -> ResourcesCompat.getFont(context, R.font.gulim)
            "바탕체" -> ResourcesCompat.getFont(context, R.font.batang)
            else -> null // 기본 글씨체
        }

        if (typeface != null) {
            holder.textView.typeface = typeface
        } else {
            Log.e("GridAdapter", "Failed to load font for selectedFont: $selectedFont")
        }

        return view
    }

    private class ViewHolder(view: View) {
        val textView: TextView = view.findViewById(R.id.grid_item_text)
    }
}

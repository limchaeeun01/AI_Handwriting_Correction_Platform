package com.chaeni__beam.ai_handwriting_correction_platform.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.chaeni__beam.ai_handwriting_correction_platform.R
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.SpinnerDropdownItemBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.SpinnerItemBinding

class SpinnerAdapter(private val context: Context, private val items: Array<String>) : BaseAdapter() {

    private lateinit var spinnerItemBinding: SpinnerItemBinding
    private lateinit var spinnerDropdownItemBinding: SpinnerDropdownItemBinding

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): String {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 기본적으로 표시되는 Spinner 항목에 폰트를 적용
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        spinnerItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_item, parent, false)
        spinnerItemBinding.tvItemName.text = items[position]

        // 각 항목에 대한 폰트 설정
        setFontBasedOnItem(spinnerItemBinding.tvItemName, items[position])

        return spinnerItemBinding.root
    }

    // 드롭다운 항목에 폰트를 적용
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        spinnerDropdownItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_dropdown_item, parent, false)
        spinnerDropdownItemBinding.tvDropdownItemName.text = items[position]

        // 각 항목에 대한 폰트 설정
        setFontBasedOnItem(spinnerDropdownItemBinding.tvDropdownItemName, items[position])

        // 선택된 항목의 텍스트 색상 변경
        if (items[position] == spinnerItemBinding.tvItemName.text.toString()) {
            spinnerDropdownItemBinding.tvDropdownItemName.setTextColor(Color.parseColor("#ED7A2B"))
        } else {
            spinnerDropdownItemBinding.tvDropdownItemName.setTextColor(Color.parseColor("#000000"))
        }

        // 마지막 항목에 Divider 숨기기
        if (position == (count - 1)) {
            spinnerDropdownItemBinding.tvDivider.visibility = View.GONE
        }

        return spinnerDropdownItemBinding.root
    }

    // 항목에 맞는 폰트 설정
    private fun setFontBasedOnItem(textView: TextView, item: String) {
        when (item) {
            "굴림체" -> textView.typeface = ResourcesCompat.getFont(context, R.font.gulim)
            "돋움체" -> textView.typeface = ResourcesCompat.getFont(context, R.font.dotum)
            "바탕체" -> textView.typeface = ResourcesCompat.getFont(context, R.font.batang)
            else -> textView.typeface = Typeface.DEFAULT
        }
    }
}

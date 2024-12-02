package com.chaeni__beam.ai_handwriting_correction_platform.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        spinnerItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_item, parent, false)
        spinnerItemBinding.tvItemName.text = items[position]

        return spinnerItemBinding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        spinnerDropdownItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.spinner_dropdown_item, parent, false)
        spinnerDropdownItemBinding.tvDropdownItemName.text = items[position]

        // Selected Item SetTextColor
        if (items[position] == spinnerItemBinding.tvItemName.text.toString()) {
            spinnerDropdownItemBinding.tvDropdownItemName.setTextColor(Color.parseColor("#ED7A2B"))
        } else {
            spinnerDropdownItemBinding.tvDropdownItemName.setTextColor(Color.parseColor("#000000"))
        }

        // Last Divider Hide
        if (position == (count - 1)) {
            spinnerDropdownItemBinding.tvDivider.visibility = View.GONE
        }

        return spinnerDropdownItemBinding.root
    }
}
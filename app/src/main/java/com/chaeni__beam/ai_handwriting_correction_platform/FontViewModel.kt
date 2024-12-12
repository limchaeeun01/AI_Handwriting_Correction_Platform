package com.chaeni__beam.ai_handwriting_correction_platform

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class FontViewModel(application: Application) : AndroidViewModel(application) {
    var selectedFont: String = "돋움체"  // 기본값 돋움체
}

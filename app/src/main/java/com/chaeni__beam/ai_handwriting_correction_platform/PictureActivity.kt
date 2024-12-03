package com.chaeni__beam.ai_handwriting_correction_platform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityDrawingBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityPictureBinding
import com.chaeni__beam.ai_handwriting_correction_platform.fragment.ModalBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPictureBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomSheetView: FrameLayout = findViewById(R.id.persistent_bottom_sheet)

        // Bottom Sheet 동작 설정
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)

        // 초기 상태 설정 (BOTTOM 상태로 시작)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // Bottom Sheet가 드래그 될 수 있도록 설정
        bottomSheetBehavior.isDraggable = true

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // 상태 변화에 따라 처리
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // 하단으로 접힌 상태
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // 상단으로 펼쳐진 상태
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 offset 처리
            }
        })



    }
}
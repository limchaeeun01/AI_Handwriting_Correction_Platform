package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityDrawingBinding
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityPictureBinding
import com.chaeni__beam.ai_handwriting_correction_platform.fragment.ModalBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException


class PictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPictureBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    // 권한 리스트
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // 권한 요청 코드
    private val PERMISSIONS_REQUEST_CODE = 100

    private fun checkPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

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

        binding.recordingBtn.setOnClickListener {
            val options = arrayOf("카메라로 촬영", "갤러리에서 선택")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("이미지 가져오기")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> openCamera()  // 카메라 호출
                        1 -> openGallery() // 갤러리 호출
                    }
                }
            builder.show()
        }


    }

    //카메라 호출
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            binding.pictureArea.setImageBitmap(bitmap)

            // 촬영된 이미지 파일을 서버로 전송
            sendImageToServer(photoFile)
        }
    }


    private lateinit var photoFile: File

    private fun openCamera() {
        val photoFile = File.createTempFile("photo_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        val photoUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)
        this.photoFile = photoFile
        cameraLauncher.launch(photoUri)
    }

    //갤러리 호출
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.pictureArea.setImageURI(uri)

            // 갤러리에서 선택된 이미지 파일을 서버로 전송
            val imageFile = File(uri.path)
            sendImageToServer(imageFile)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    //통신
    private fun sendImageToServer(imageFile: File) {
        val client = OkHttpClient()

        // 이미지 파일을 서버로 전송할 MultipartBody 구성
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), imageFile))  // 이미지 파일 전송
            .build()

        // 서버에 POST 요청
        val request = Request.Builder()
            .url("http://192.168.200.106:8080/api/ocr/extract-text")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 네트워크 오류 처리
                e.printStackTrace()
                runOnUiThread {
                    // UI 업데이트: 오류 메시지 표시
                    Log.d("test", "Network error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // OCR 처리 결과를 처리하는 코드
                    val responseBody = response.body?.string()

                    runOnUiThread {
                        if (responseBody != null) {
                            // OCR 결과가 텍스트라면, TextView에 표시
                            binding.ocrResultText.text = responseBody
                        } else {
                            // 서버에서 받은 내용이 없을 경우 오류 처리
                            Log.d("test", "Empty response body")
                        }
                    }
                } else {
                    // 서버 오류 처리
                    runOnUiThread {
                        // UI 업데이트: 오류 메시지 표시
                        Log.d("test", "OCR 처리 오류: ${response.code}")
                    }
                }
            }
        })
    }


}
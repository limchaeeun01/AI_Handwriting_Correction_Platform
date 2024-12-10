package com.chaeni__beam.ai_handwriting_correction_platform

import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.chaeni__beam.ai_handwriting_correction_platform.databinding.ActivityPictureBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import android.content.Context
import android.graphics.Canvas
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


class PictureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPictureBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private lateinit var handwrittenImageFile: File

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

        setSelectedFont()

        val bottomSheetView: FrameLayout = findViewById(R.id.persistent_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isDraggable = true

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

    // 카메라 호출
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

    // 갤러리 호출
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.pictureArea.setImageURI(uri)

            // 갤러리에서 선택된 이미지 파일을 서버로 전송
            val imageFile = getFileFromUri(uri)
            if (imageFile != null) {
                sendImageToServer(imageFile)
            } else {
                Log.d("test", "파일을 읽을 수 없습니다.")
            }
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    // URI에서 실제 파일로 변환하는 함수
    private fun getFileFromUri(uri: Uri): File? {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ocr_test_img.jpg")
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.copyTo(file.outputStream())
            }
            return file
        } catch (e: Exception) {
            Log.e("FileError", "파일 변환 중 오류 발생: ${e.message}")
            return null
        }
    }

    private fun setSelectedFont() {
        // SharedPreferences에서 글씨체 가져오기
        val sharedPref = getSharedPreferences("font_preferences", MODE_PRIVATE)
        val selectedFont = sharedPref.getString("selected_font", "돋움체") // 기본값은 "굴림체"로 설정

        // 선택된 폰트 적용
        when (selectedFont) {
            "굴림체" -> binding.ocrResultText.typeface = ResourcesCompat.getFont(this, R.font.gulim)
            "돋움체" -> binding.ocrResultText.typeface = ResourcesCompat.getFont(this, R.font.dotum)
            "바탕체" -> binding.ocrResultText.typeface = ResourcesCompat.getFont(this, R.font.batang)
            else -> binding.ocrResultText.typeface = Typeface.DEFAULT
        }
    }

    // 캡쳐
    fun captureView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas) // 뷰를 비트맵으로 캡처
        return bitmap
    }

    // Bitmap을 Multipart로 변환
    fun bitmapToMultipart(bitmap: Bitmap, paramName: String, fileName: String): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // Bitmap을 PNG로 압축
        val byteArray = stream.toByteArray()

        val requestBody = byteArray.toRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, fileName, requestBody)
    }

    // '/api/ocr' 엔드포인트
    private fun sendImageToServer(imageFile: File) {
        val client = OkHttpClient()
        handwrittenImageFile = imageFile // 전송된 파일을 전역 변수에 저장

        binding.loadingText.visibility = View.VISIBLE

        // 이미지 파일을 서버로 전송할 MultipartBody 구성
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", imageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            ) // 이미지 파일 전송
            .build()

        // 서버에 POST 요청
        val request = Request.Builder()
            .url("http://192.168.200.106:8080/api/handwriting/test")
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
                            Log.d("test", responseBody)
                            // OCR 결과가 텍스트라면, TextView에 표시
                            binding.ocrResultText.text = responseBody
                            binding.loadingText.visibility = View.GONE

                            // '/api/correction'
                            processAndSendComparison()

                        } else {
                            // 서버에서 받은 내용이 없을 경우 오류 처리
                            Log.d("test", "Empty response body")
                            binding.loadingText.visibility = View.GONE
                        }
                    }
                } else {
                    // 서버 오류 처리
                    runOnUiThread {
                        // UI 업데이트: 오류 메시지 표시
                        Log.d("test", "OCR 처리 오류: ${response.code}")
                        binding.loadingText.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun processAndSendComparison() {
        // 1. 캡처된 Bitmap 생성
        val generatedBitmap = captureView(binding.captureArea)

        // 2. 손글씨 이미지 파일 확인 및 비교 요청
        if (::handwrittenImageFile.isInitialized && handwrittenImageFile.exists()) {
            sendComparisonImages(handwrittenImageFile, generatedBitmap)
        } else {
            Log.e("FileError", "손글씨 이미지 파일이 없습니다.")
        }
    }

    // '/apo/correction' 엔드포인트
    private fun sendComparisonImages(handwrittenImage: File, generatedBitmap: Bitmap) {
        val client = OkHttpClient()

        // Bitmap을 Multipart로 변환
        val generatedMultipart = bitmapToMultipart(generatedBitmap, "generatedImage", "generated_image.png")

        // File을 Multipart로 변환
        val handwrittenMultipart = MultipartBody.Part.createFormData(
            "handwrittenImage",
            handwrittenImage.name,
            handwrittenImage.asRequestBody("image/*".toMediaTypeOrNull())
        )

        // MultipartBody 구성
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(handwrittenMultipart)
            .addPart(generatedMultipart)
            .build()

        // 서버 요청
        val request = Request.Builder()
            .url("http://192.168.200.106:8080/api/handwriting/correction")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("NetworkError", "서버 전송 실패: ${e.message}")
                    binding.loadingText.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            Log.d("ServerResponse", "비교 결과: $responseBody")
                            // 결과를 UI에 반영
                        } else {
                            Log.e("ServerError", "응답이 비어 있음")
                        }
                    } else {
                        Log.e("ServerError", "응답 실패: ${response.code}")
                    }
                    binding.loadingText.visibility = View.GONE
                }
            }
        })
    }

}

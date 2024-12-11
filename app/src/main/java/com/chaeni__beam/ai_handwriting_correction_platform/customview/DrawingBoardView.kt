package com.chaeni__beam.ai_handwriting_correction_platform.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Paint 객체들
    private val drawPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val gridPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    // 사용자 입력 저장
    private val drawPath = Path()
    private val paths = mutableListOf<Pair<Path, Paint>>()

    // 원고지 그리드 관련 변수
    var rowCount = 15  // 행 수
    var columnCount = 7  // 열 수
    private val gridRectangles = mutableListOf<RectF>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 원고지 그리드 그리기
        drawGrid(canvas)

        // 사용자 경로 그리기
        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }

        // 현재 경로 그리기
        canvas.drawPath(drawPath, drawPaint)
    }

    private fun drawGrid(canvas: Canvas) {
        val cellWidth = width.toFloat() / columnCount
        val cellHeight = height.toFloat() / rowCount

        gridRectangles.clear() // 기존 그리드 초기화

        for (row in 0 until rowCount) {
            for (col in 0 until columnCount) {
                val left = col * cellWidth
                val top = row * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight

                // 그리드 사각형 저장
                val rect = RectF(left, top, right, bottom)
                gridRectangles.add(rect)

                // 캔버스에 그리드 그리기
                canvas.drawRect(rect, gridPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 터치 시작
                drawPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                // 터치 이동
                drawPath.lineTo(event.x, event.y)
                invalidate() // 화면 갱신
            }
            MotionEvent.ACTION_UP -> {
                // 터치 종료
                paths.add(Pair(Path(drawPath), Paint(drawPaint)))
                drawPath.reset() // 현재 경로 초기화
            }
        }
        return true
    }

    // 그리드 좌표 목록 반환
    fun getGridRectangles(): List<RectF> {
        return gridRectangles
    }

    // 특정 그리드 영역을 캡처하여 비트맵 반환
    fun captureArea(rect: RectF): Bitmap {
        val bitmap = Bitmap.createBitmap(rect.width().toInt(), rect.height().toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 캔버스 좌표 이동하여 특정 영역만 캡처
        canvas.translate(-rect.left, -rect.top)
        draw(canvas)

        return bitmap
    }

    // 비트맵이 비어있는지 확인
    fun isBitmapEmpty(bitmap: Bitmap): Boolean {
        val emptyBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        return bitmap.sameAs(emptyBitmap)
    }

    // DrawingBoardView에 초기화 메서드 추가
    fun clearCanvas() {
        paths.clear()      // 저장된 경로 초기화
        drawPath.reset()   // 현재 그리는 경로 초기화
        invalidate()       // 화면 갱신
    }

}

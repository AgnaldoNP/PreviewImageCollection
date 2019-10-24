package pereira.agnaldo.previewimgcol

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlin.collections.ArrayList


class ImageCollectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mBitmaps: ArrayList<Bitmap>
    private val mHashBitmapOnClick: HashMap<Bitmap, OnImageClickListener>
    private var mMaxImagePerLine = 3
    private var mBaseHeight = 300

    private var mMargins = 1

    init {
        orientation = VERTICAL
        mBitmaps = ArrayList()
        mHashBitmapOnClick = hashMapOf()
    }

    fun addBitmap(bitmap: Bitmap) {
        addBitmap(bitmap, null)
    }

    fun addBitmap(bitmap: Bitmap, onClick: OnImageClickListener?) {
        reEvaluateLastLine(bitmap)
        mBitmaps.add(bitmap)
        onClick?.let { mHashBitmapOnClick.put(bitmap, it) }
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post { loadBitmaps() }
    }

    private fun loadBitmaps() {
        removeAllViews()
        extractFirstImagesPerLine(mBitmaps)
    }

    private fun extractFirstImagesPerLine(bitmaps: List<Bitmap>) {
        if (bitmaps.size > mMaxImagePerLine) {
            val newLine = createNewLine()
            addBitmapsToLine(bitmaps.subList(0, mMaxImagePerLine), newLine)

            extractFirstImagesPerLine(bitmaps.subList(mMaxImagePerLine, bitmaps.size))
            return
        } else {
            val newLine = createNewLine()
            addBitmapsToLine(bitmaps, newLine)
        }
    }

    private fun reEvaluateLastLine(bitmap: Bitmap) {
        if (width == 0) {
            return
        }

        val lineLinearLayout = getChildAt(childCount - 1) as ViewGroup
        val lineChildCount = lineLinearLayout.childCount
        if (lineChildCount == mMaxImagePerLine) {
            createNewLine()
            reEvaluateLastLine(bitmap)
            return
        }

        val bitmaps = mBitmaps.subList(
            mBitmaps.size - lineChildCount, mBitmaps.size
        )
        bitmaps.add(bitmap)

        addBitmapsToLine(bitmaps, lineLinearLayout)
    }

    private fun addBitmapsToLine(bitmaps: List<Bitmap>, lineLinearLayout: ViewGroup) {
        if (bitmaps.isEmpty())
            return

        lineLinearLayout.removeAllViews()

        val widthSum = bitmaps.sumBy { bitmap -> bitmap.width }
        mBaseHeight = width / mMaxImagePerLine

        bitmaps.forEach { bitmap: Bitmap ->
            val imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageBitmap(bitmap)

            mHashBitmapOnClick[bitmap]?.let { bmp ->
                imageView.setOnClickListener { bmp.onClicked(bitmap, imageView) }
            }

            val proportion = bitmap.width / widthSum.toFloat()
            val widthBmp = (width * proportion).toInt()

            val params = LayoutParams(widthBmp, mBaseHeight)
            params.setMargins(mMargins, mMargins, mMargins, mMargins)
            lineLinearLayout.addView(imageView, params)
        }
    }

    private fun createNewLine(): LinearLayout {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = HORIZONTAL

        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(linearLayout, params)
        return linearLayout
    }

    interface OnImageClickListener {
        fun onClicked(bitmap: Bitmap, imageView: ImageView)
    }

}

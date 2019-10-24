package pereira.agnaldo.previewimgcol

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.ablanco.zoomy.Zoomy
import kotlin.collections.ArrayList


class ImageCollectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mBitmaps: ArrayList<Bitmap>
    private val mHashBitmapOnClick: HashMap<Bitmap, OnImageClickListener>
    private val mHashBitmapImageView: HashMap<Bitmap, ImageView>

    private var mMaxImagePerRow = 3
    private var mMaxRows = 1
    private var mBaseHeight = 300
    private var mImageMargin = 1
    private var mBackgroundColor = Color.WHITE
    private var mPinchToZoom = true

    init {
        setBackgroundColor(mBackgroundColor)
        orientation = VERTICAL

        mBitmaps = ArrayList()
        mHashBitmapOnClick = hashMapOf()
        mHashBitmapImageView = hashMapOf()

        getStyles(attrs, defStyleAttr)
    }

    private fun getStyles(attrs: AttributeSet?, defStyle: Int) {
        attrs?.let {

            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.ImageCollectionView, defStyle, R.style.defaultPreviewImageCollection
            )

            mBaseHeight = typedArray.getDimensionPixelSize(
                R.styleable.ImageCollectionView_baseRowHeight, mBaseHeight
            )

            mImageMargin = typedArray.getDimensionPixelSize(
                R.styleable.ImageCollectionView_imageMargin, mImageMargin
            )

            mMaxImagePerRow = typedArray.getInteger(
                R.styleable.ImageCollectionView_maxImagePerRow, mMaxImagePerRow
            )

            mMaxRows = typedArray.getInteger(
                R.styleable.ImageCollectionView_maxRows, mMaxRows
            )

            mBackgroundColor = typedArray.getColor(
                R.styleable.ImageCollectionView_backgroundColor, mBackgroundColor
            )

            mPinchToZoom = typedArray.getBoolean(
                R.styleable.ImageCollectionView_pinchToZoom, mPinchToZoom
            )

            typedArray.recycle()
        }
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mBitmaps.forEach { bitmap: Bitmap ->
            mHashBitmapImageView[bitmap]?.let {
                Zoomy.unregister(it)
            }
        }
    }

    private fun loadBitmaps() {
        removeAllViews()
        extractFirstImagesPerLine(mBitmaps)
    }

    private fun extractFirstImagesPerLine(bitmaps: List<Bitmap>) {
        if (bitmaps.size > mMaxImagePerRow) {
            val newLine = createNewLine()
            addBitmapsToLine(bitmaps.subList(0, mMaxImagePerRow), newLine)

            extractFirstImagesPerLine(bitmaps.subList(mMaxImagePerRow, bitmaps.size))
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
        if (lineChildCount == mMaxImagePerRow) {
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

        bitmaps.forEach { bitmap: Bitmap ->
            val imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageBitmap(bitmap)

            if (mPinchToZoom && context is Activity) {
                Zoomy.Builder(context as Activity).target(imageView).tapListener {
                    mHashBitmapOnClick[bitmap]?.let { bmp ->
                        bmp.onClicked(bitmap, imageView)
                    }
                }.register()
            } else {
                mHashBitmapOnClick[bitmap]?.let { bmp ->
                    imageView.setOnClickListener { bmp.onClicked(bitmap, imageView) }
                }
            }

            val proportion = bitmap.width / widthSum.toFloat()
            val widthBmp = (width * proportion).toInt()

            val params = LayoutParams(widthBmp, mBaseHeight)
            params.setMargins(mImageMargin, mImageMargin, mImageMargin, mImageMargin)
            lineLinearLayout.addView(imageView, params)

            mHashBitmapImageView[bitmap] = imageView
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

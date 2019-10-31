package pereira.agnaldo.previewimgcol

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.ablanco.zoomy.Zoomy
import kotlin.math.roundToInt


class ImageCollectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        const val NO_ROW_LIMITS = -1
    }

    private val mBitmaps: ArrayList<Bitmap>
    private val mHashBitmapOnClick: HashMap<Bitmap, OnImageClickListener>
    private val mHashBitmapImageView: HashMap<Bitmap, ImageView>
    private var onMoreClickListener: OnMoreClickListener? = null

    var maxImagePerRow = 3
        set(value) {
            field = value
            clearAndReloadBitmaps()
        }

    var maxRows = NO_ROW_LIMITS
        set(value) {
            field = value
            clearAndReloadBitmaps()
        }

    var baseImageHeight = 300
        set(value) {
            field = value
            clearAndReloadBitmaps()
        }

    var imageMargin = 1
        set(value) {
            field = value
            clearAndReloadBitmaps()
        }

    var mBackgroundColor = Color.WHITE
        set(value) {
            field = value
            clearAndReloadBitmaps()
        }

    var pinchToZoom = true
        set(value) {
            field = value
            clearAndReloadBitmaps()
        }

    var showExternalBorderMargins = false
        set(value) {
            field = value
            clearAndReloadBitmaps()
        }

    init {
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

            baseImageHeight = typedArray.getDimensionPixelSize(
                R.styleable.ImageCollectionView_baseRowHeight, baseImageHeight
            )

            imageMargin = typedArray.getDimensionPixelSize(
                R.styleable.ImageCollectionView_imageMargin, imageMargin
            )

            maxImagePerRow = typedArray.getInteger(
                R.styleable.ImageCollectionView_maxImagePerRow, maxImagePerRow
            )

            maxRows = typedArray.getInteger(
                R.styleable.ImageCollectionView_maxRows, maxRows
            )

            mBackgroundColor = typedArray.getColor(
                R.styleable.ImageCollectionView_backgroundColor, mBackgroundColor
            )

            pinchToZoom = typedArray.getBoolean(
                R.styleable.ImageCollectionView_pinchToZoom, pinchToZoom
            )

            showExternalBorderMargins = typedArray.getBoolean(
                R.styleable.ImageCollectionView_showExternalBorderMargins,
                showExternalBorderMargins
            )

            typedArray.recycle()
        }
    }

    fun addImage(drawable: Int) {
        addImage(drawable)
    }

    fun addImage(drawable: Int, onClick: OnImageClickListener?) {
        context.getDrawable(drawable)?.let {
            addImage(it.toBitmap(), onClick)
        }
    }

    fun addImage(drawable: Drawable) {
        addImage(drawable)
    }

    fun addImage(drawable: Drawable, onClick: OnImageClickListener?) {
        addImage(drawable.toBitmap(), onClick)
    }

    fun addImage(bitmap: Bitmap) {
        addImage(bitmap, null)
    }

    fun addImage(bitmap: Bitmap, onClick: OnImageClickListener?) {
        mBitmaps.add(bitmap)
        reEvaluateLastRow(bitmap)
        onClick?.let { mHashBitmapOnClick.put(bitmap, it) }
        removeOutsideMargins()
        invalidate()
    }

    fun setOnMoreClicked(onMoreClickListener: OnMoreClickListener) {
        this.onMoreClickListener = onMoreClickListener;
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post { clearAndReloadBitmaps() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mBitmaps.forEach { bitmap: Bitmap ->
            mHashBitmapImageView[bitmap]?.let {
                Zoomy.unregister(it)
            }
        }
    }

    private fun clearAndReloadBitmaps() {
        removeAllViews()
        extractAndInflateImagesPerLine(mBitmaps)
        removeOutsideMargins()
    }

    private fun extractAndInflateImagesPerLine(bitmaps: List<Bitmap>) {
        val maxRowReached = maxRows != NO_ROW_LIMITS && childCount == maxRows

        if (!maxRowReached && bitmaps.size > maxImagePerRow) {
            val newLine = createNewRow()
            addBitmapsToLine(bitmaps.subList(0, maxImagePerRow), newLine)

            extractAndInflateImagesPerLine(bitmaps.subList(maxImagePerRow, bitmaps.size))
            return
        } else {
            if (!maxRowReached) {
                val newLine = createNewRow()
                addBitmapsToLine(bitmaps, newLine)
            } else {
                addThereAreMore()
            }
        }
    }

    private fun addThereAreMore() {
        val lastRow = getChildAt(childCount - 1) as LinearLayout
        val lastImage = lastRow.getChildAt(lastRow.childCount - 1) as ImageView

        Zoomy.unregister(lastImage)
        val lastImageIndice = childCount * maxImagePerRow

        onMoreClickListener?.let {
            lastImage.setOnClickListener {
                onMoreClickListener!!.onMoreClicked(
                    mBitmaps.subList(
                        lastImageIndice - 1,
                        mBitmaps.size
                    )
                )
            }
        }

        val bitmap = mBitmaps[(maxRows * maxImagePerRow) - 1]

        val blurredBitmap = blur(bitmap)

        val canvas = Canvas(blurredBitmap)

        val paintText = Paint()
        paintText.color = Color.WHITE
        paintText.style = Paint.Style.FILL_AND_STROKE
        paintText.textAlign = Paint.Align.CENTER

        val text = "+".plus(mBitmaps.size - lastImageIndice + 1)
        var textSize = 130f
        paintText.textSize = 130f

        val paint = Paint()
        paint.color = Color.argb(100, 0, 0, 0)
        paint.maskFilter = BlurMaskFilter(300F, BlurMaskFilter.Blur.INNER)

        val rect = Rect(0, 0, canvas.width, canvas.height)
        canvas.drawRect(rect, paint)

        canvas.drawText(
            text,
            rect.centerX().toFloat(),
            rect.centerY().toFloat() + textSize / 2f,
            paintText
        )

        lastImage.setImageBitmap(blurredBitmap)
    }

    private fun reEvaluateLastRow(bitmap: Bitmap) {
        if (width == 0) {
            return
        }

        val lineLinearLayout = getChildAt(childCount - 1) as ViewGroup
        val lineChildCount = lineLinearLayout.childCount
        if (lineChildCount == maxImagePerRow) {
            val maxImages = maxRows * maxImagePerRow
            val imagesCount = childCount * maxImagePerRow
            if (imagesCount < maxImages) {
                createNewRow()
                reEvaluateLastRow(bitmap)
            } else {
                addThereAreMore()
            }
            return
        }

        val bitmaps = mBitmaps.subList(
            mBitmaps.size - lineChildCount - 1, mBitmaps.size
        )
        addBitmapsToLine(bitmaps, lineLinearLayout)
    }

    private fun addBitmapsToLine(bitmaps: List<Bitmap>, rowLinearLayout: ViewGroup) {
        if (bitmaps.isEmpty())
            return

        rowLinearLayout.removeAllViews()
        rowLinearLayout.setBackgroundColor(mBackgroundColor)

        val widthSum = bitmaps.sumBy { bitmap -> bitmap.width }

        bitmaps.forEach { bitmap: Bitmap ->
            val imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageBitmap(bitmap)

            if (pinchToZoom && context is Activity) {
                Zoomy.Builder(context as Activity).target(imageView).tapListener {
                    mHashBitmapOnClick[bitmap]?.onClick(bitmap, imageView)
                }.register()
            } else {
                mHashBitmapOnClick[bitmap]?.let { bmp ->
                    imageView.setOnClickListener { bmp.onClick(bitmap, imageView) }
                }
            }

            val proportion = (bitmap.width / widthSum.toFloat())
            val widthBmp = ((width * proportion).toInt()) - (2 * imageMargin)
            val heightBmp = baseImageHeight - (2 * imageMargin)

            val params = LayoutParams(widthBmp, heightBmp)
            params.setMargins(imageMargin, imageMargin, imageMargin, imageMargin)

            rowLinearLayout.addView(imageView, params)

            mHashBitmapImageView[bitmap] = imageView
        }
    }

    private fun createNewRow(): LinearLayout {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = HORIZONTAL

        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(linearLayout, params)
        return linearLayout
    }

    private fun removeOutsideMargins() {
        if (showExternalBorderMargins)
            return

        for (i in 0 until childCount) {
            val row = getChildAt(i) as LinearLayout
            val rowChildCount = row.childCount
            for (j in 0 until rowChildCount) {
                val image = row.getChildAt(j)

                val layoutParams = image.layoutParams as LayoutParams
                if (i == 0) {
                    layoutParams.topMargin = 0
                    layoutParams.height = layoutParams.height + imageMargin
                }

                if (i == childCount - 1) {
                    layoutParams.bottomMargin = 0
                    layoutParams.height = layoutParams.height + imageMargin
                }

                if (j == 0) {
                    layoutParams.leftMargin = 0
                    layoutParams.width = layoutParams.width + imageMargin
                }

                if (j == rowChildCount - 1) {
                    layoutParams.rightMargin = 0
                    layoutParams.width = layoutParams.width + imageMargin
                }

                image.layoutParams = layoutParams
            }
        }
    }

    private fun blur(image: Bitmap): Bitmap {

        val bitmapScale = 0.4f
        val blurRadius = 15.5f

        val width = (image.width * bitmapScale).roundToInt()
        val height = (image.height * bitmapScale).roundToInt()

        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val rs = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(blurRadius)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)

        return outputBitmap
    }

    interface OnImageClickListener {
        fun onClick(bitmap: Bitmap, imageView: ImageView)
    }

    interface OnMoreClickListener {
        fun onMoreClicked(bitmaps: List<Bitmap>)
    }

}

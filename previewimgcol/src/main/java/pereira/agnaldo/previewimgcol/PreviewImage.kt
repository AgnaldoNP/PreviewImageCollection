@file:Suppress("unused", "DEPRECATION")

package pereira.agnaldo.previewimgcol

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import java.io.File

internal class PreviewImage(private val context: Context) {

    var mOnClick: ImageCollectionView.OnImageClickListener? = null
    var mOnClickUnit: ((Bitmap?, ImageView?) -> Unit)? = null

    var mOnLongClick: ImageCollectionView.OnImageLongClickListener? = null
    var mOnLongClickUnit: ((Bitmap?, ImageView?) -> Unit)? = null

    var mImageView: ImageView? = null

    private var imageBitmap: Bitmap? = null

    constructor(context: Context, imageBitmap: Bitmap) : this(context) {
        this.imageBitmap = imageBitmap
    }

    private var imageUri: Uri? = null

    constructor(context: Context, imageUri: Uri) : this(context) {
        this.imageUri = imageUri
    }

    private var imageResourceId: Int? = null

    constructor(context: Context, imageResourceId: Int) : this(context) {
        this.imageResourceId = imageResourceId
    }

    private var imageDrawable: Drawable? = null

    constructor(context: Context, imageDrawable: Drawable) : this(context) {
        this.imageDrawable = imageDrawable
    }

    private var imageFile: File? = null

    constructor(context: Context, imageFile: File) : this(context) {
        this.imageFile = imageFile
    }

    fun loadImage(imageView: ImageView) {
        this.mImageView = imageView
        Glide.with(context).let { glide ->
            when {
                imageUri != null -> {
                    glide.load(imageUri)
                }
                imageBitmap != null -> {
                    glide.load(imageBitmap)
                }
                imageFile != null -> {
                    glide.load(imageFile)
                }
                imageDrawable != null -> {
                    glide.load(imageDrawable)
                }
                imageResourceId != null -> {
                    glide.load(imageResourceId)
                }
                else -> {
                    null
                }
            }?.into(imageView)

            imageView.setOnClickListener { onClick() }
            imageView.setOnLongClickListener { onLongClick() }
        }
    }

    fun width(): Int {
        if (imageBitmap != null) {
            return imageBitmap?.width ?: 0
        }

        return 0
    }

    fun asBitmap(): Bitmap? =
        when {
            imageBitmap != null -> {
                imageBitmap
            }
            imageFile != null -> {
                imageBitmap = BitmapFactory.decodeFile(imageFile!!.path)
                imageBitmap
            }
            imageResourceId != null -> {
                imageBitmap = ContextCompat.getDrawable(context, imageResourceId!!)?.toBitmap()
                imageBitmap
            }
            imageDrawable != null -> {
                imageBitmap = imageDrawable?.toBitmap()
                imageBitmap
            }
            imageUri != null -> {
                imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                imageBitmap
            }
            else -> {
                null
            }
        }

    fun isEquals(bitmap: Bitmap): Boolean = imageBitmap?.equals(bitmap) ?: false

    fun onClick() {
        if (mOnClick != null) {
            val bitmap = asBitmap()
            mOnClickUnit?.invoke(bitmap, mImageView) ?: run {
                if (bitmap != null && mImageView != null) {
                    mOnClick?.onClick(bitmap, mImageView!!)
                }
            }
        }
    }

    fun onLongClick(): Boolean {
        if (mOnLongClick != null || mOnClickUnit != null) {
            val bitmap = asBitmap()
            mOnLongClickUnit?.invoke(bitmap, mImageView) ?: run {
                if (bitmap != null && mImageView != null) {
                    mOnLongClick?.onLongClick(bitmap, mImageView!!)
                }
            }
        }

        return true
    }
}

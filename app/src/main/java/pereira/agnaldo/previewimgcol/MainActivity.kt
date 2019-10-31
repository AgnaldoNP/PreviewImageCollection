package pereira.agnaldo.previewimgcol

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.ivan200.photobarcodelib.PhotoBarcodeScannerBuilder

class MainActivity : AppCompatActivity() {


    private lateinit var collectionView: ImageCollectionView
    private lateinit var backgroundColor: SeekBar
    private lateinit var baseRowHeight: SeekBar
    private lateinit var imageMargin: SeekBar
    private lateinit var maxImagePerRow: SeekBar
    private lateinit var maxRows: SeekBar
    private lateinit var pinchToZoom: CheckBox
    private lateinit var showExternalBoards: CheckBox

    private lateinit var addPhoto: Button

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        collectionView = findViewById(R.id.imageCollectionView)

        backgroundColor = findViewById(R.id.color)
        baseRowHeight = findViewById(R.id.baseRowHeight)
        imageMargin = findViewById(R.id.imageMargin)
        maxImagePerRow = findViewById(R.id.maxImagePerRow)
        maxRows = findViewById(R.id.maxRows)
        pinchToZoom = findViewById(R.id.pinchToZoom)
        showExternalBoards = findViewById(R.id.showExternalBorderMargins)
        addPhoto = findViewById(R.id.add_photo)

        backgroundColor.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                collectionView.mBackgroundColor = if (progress == 0) Color.TRANSPARENT else
                    Color.HSVToColor(floatArrayOf(progress.toFloat(), 100f, 100f))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        baseRowHeight.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                collectionView.baseImageHeight = if (progress <= 2) 120.px else progress.px
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        imageMargin.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                collectionView.imageMargin = progress.px
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        maxImagePerRow.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                collectionView.maxImagePerRow = if (progress == 0) 3 else progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        maxRows.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                collectionView.maxRows =
                    if (progress == 0) ImageCollectionView.NO_ROW_LIMITS else progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        pinchToZoom.setOnCheckedChangeListener { buttonView, isChecked ->
            collectionView.pinchToZoom = isChecked
        }

        showExternalBoards.setOnCheckedChangeListener { buttonView, isChecked ->
            collectionView.showExternalBorderMargins = isChecked
        }

        addPhoto.setOnClickListener {
            PhotoBarcodeScannerBuilder()
                .withActivity(this)
                .withTakingPictureMode()
                .withAutoFocus(true)
                .withFocusOnTap(true)
                .withCameraLockRotate(false)
                .withThumbnails(false)
                .withCameraTryFixOrientation(true)
                .withImageLargerSide(1200)
                .withPictureListener { file ->
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        collectionView.addImage(bitmap)
                        file.delete()
                    }
                }.build().start()
        }

        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_08),
            object : ImageCollectionView.OnImageClickListener {
                override fun onClick(bitmap: Bitmap, imageView: ImageView) {
                    Toast.makeText(imageView.context, "Test Click image 08", Toast.LENGTH_LONG)
                        .show()
                }
            }
        )
        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_01))
        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_02))
        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_05))
        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_03))
        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_04))
        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_06))
        collectionView.addImage(BitmapFactory.decodeResource(resources, R.drawable.landscape_07))

        collectionView.setOnMoreClicked(object : ImageCollectionView.OnMoreClickListener {
            override fun onMoreClicked(bitmaps: List<Bitmap>) {
                Toast.makeText(collectionView.context, "oi oi oi oi ", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}

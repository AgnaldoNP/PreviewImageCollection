package pereira.agnaldo.previewimgcol

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val collectionView = findViewById<ImageCollectionView>(R.id.imageCollectionView)

        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_08),
            object : ImageCollectionView.OnImageClickListener {
                override fun onClicked(bitmap: Bitmap, imageView: ImageView) {
                    Toast.makeText(imageView.context, "Test Click image 08", Toast.LENGTH_LONG)
                        .show()
                }
            }
        )
        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_01))
        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_02))
        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_05))
        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_03))
        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_04))
        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_06))
        collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_07))
    }
}

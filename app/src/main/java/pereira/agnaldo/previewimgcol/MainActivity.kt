package pereira.agnaldo.previewimgcol

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val collectionView = findViewById<ImageCollectionView>(R.id.imageCollectionView)
//        collectionView.post {
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_08))
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_01))
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_02))
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_05))
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_03))
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_04))
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_06))
            collectionView.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.landscape_07))
//        }
    }
}

# Preview Image Collection

## Introduction
*Preview Image Collection* is a library to draw a collage with a number of images like facebook preview album

![Screenshot](https://github.com/AgnaldoNP/PreviewImageCollection/blob/master/screenshot/screenshot.png?raw=true)
![GIF](https://github.com/AgnaldoNP/PreviewImageCollection/blob/master/screenshot/sample.gif?raw=true)

## Install

**Step 1**. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
**Step 2.** Add the dependency
```
dependencies {
  implementation 'com.github.AgnaldoNP:PreviewImageCollection:1.5'
}
```
[![](https://jitpack.io/v/AgnaldoNP/PreviewImageCollection.svg)](https://jitpack.io/#AgnaldoNP/PreviewImageCollection)


## Usage

Sample of usage
```xml
<pereira.agnaldo.previewimgcol.ImageCollectionView
    android:id="@+id/imageCollectionView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="30dp"
    app:backgroundColor="@color/colorAccent"
    app:baseRowHeight="150dp"
    app:imageMargin="1dp"
    app:pinchToZoom="true"
    app:showExternalBorderMargins="true"
    app:maxImagePerRow="3"
    app:maxRows="2" />

```
### Options
| Property                  | Value type        | Default     |
|---------------------------|-------------------|-------------|
| backgroundColor           | color             | #FFFFFF     |
| baseRowHeight             | dimension         | 150dp       |
| imageMargin               | dimension         | 1dp         |
| pinchToZoom               | boolean           | true        |
| showExternalBorderMargins | boolean           | true        |
| maxImagePerRow            | integer           | 3           |
| maxRows                   | integer           | 3           |
| imageScaleType            | enum              | center_crop |


### Programmatically
```kotlin
    var collectionView = findViewById(R.id.imageCollectionView)

    collectionView.maxRows = ImageCollectionView.NO_ROW_LIMITS
    collectionView.maxRows = 10

    collectionView.maxImagePerRow =3

    collectionView.imageMargin = 10

    collectionView.baseImageHeight = 150

    collectionView.mBackgroundColor = Color.WHITE

    collectionView.pinchToZoom = true

    ollectionView.showExternalBorderMargins = true

    val bitmap = ...
    collectionView.addImage(bitmap)

    val bitmap2 = ...
    collectionView.addImage(bitmap2, object : ImageCollectionView.OnImageClickListener {
        override fun onClick(bitmap: Bitmap, imageView: ImageView) {
            Toast.makeText(imageView.context, "Test Click on image ...", Toast.LENGTH_LONG).show()
        }
    })
    // or simply
    collectionView.addImage(bitmap2, { bitmap: Bitmap?, imageView: ImageView? ->
        Toast.makeText(context, "Test Click on image ...", Toast.LENGTH_LONG).show()
    })

    collectionView.setOnMoreClicked(object : ImageCollectionView.OnMoreClickListener {
        override fun onMoreClicked(bitmaps: List<Bitmap>) {
            Toast.makeText(collectionView.context, "on more clicked ", Toast.LENGTH_LONG).show()
        }
    })
    // or simply
    collectionView.setOnMoreClicked { bitmaps ->
        Toast.makeText(collectionView.context, "on more clicked ", Toast.LENGTH_LONG).show()
    }
```

```java
    ImageCollectionView collectionView = (ImageCollectionView) findViewById(R.id.imageCollectionView);

    Bitmap bitmap = ...;
    imageCollectionView.addImage(bitmap);

    Bitmap bitmap2 = ...;
    imageCollectionView.addImage(bitmap, (bmp, imageView) -> {
        Toast.makeText(context, "Test Click image 08", Toast.LENGTH_LONG).show();
    });

    imageCollectionView.setOnMoreClicked(bitmaps -> {
        Toast.makeText(context, "OnMoreClicked", Toast.LENGTH_LONG).show();
    });
```


## Contributions and Support

This project made use of [Zoomy](https://github.com/imablanco/Zoomy) by [Álvaro Blanco](https://github.com/imablanco) to enable "pinch to zoom" functionality.

Contributions are welcome. Create a new pull request in order to submit your fixes and they shall be merged after moderation. In case of any issues, bugs or any suggestions, either create a new issue or post comments in already active relevant issues

## Please consider supporting me
ETH Address
 * [0xe61d524595D3bCC92DaD9bCC965B87394F9069d8](https://etherscan.io/address/0xe61d524595D3bCC92DaD9bCC965B87394F9069d8)

--

ETH / SHIB / BNB / SLP / IOTX / DODGE (BEP20 or ERC20)
 * 0x0d620a663692ac8797c289c5715228c5f19f9f7a

--

DOGE
 * (Main net) DQXW3DH2Jwe3zuCAcNAL7xLr1cSx7b7Pmt
 * (BEP20) 0x0d620a663692ac8797c289c5715228c5f19f9f7a

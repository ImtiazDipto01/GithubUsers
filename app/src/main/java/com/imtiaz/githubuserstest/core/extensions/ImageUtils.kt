package com.imtiaz.githubuserstest.core.extensions

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.imtiaz.githubuserstest.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL


fun ImageView.loadImage(url: String?) {
    val options: RequestOptions = RequestOptions()
        .transform(FitCenter())
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder)
        .priority(Priority.HIGH)

    Glide.with(this)
        .load(url)
        .apply(options)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

suspend fun ImageView.loadImageAsBitmap(url: String?) {
    val options: RequestOptions = RequestOptions()
        .transform(FitCenter())
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder)
        .priority(Priority.HIGH)

    val bitmap: Bitmap = withContext(Dispatchers.IO) {
        Glide.with(this@loadImageAsBitmap)
            .asBitmap()
            .load(url)
            .apply(options)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .submit(100, 100)
            .get()
    }

    val invertedBitmap = invertBitmap(bitmap)
    invertedBitmap?.let {
        this.setImageBitmap(it)
        return
    }
    this.setImageBitmap(bitmap)

}

@Composable
fun loadImage(
    url: String?,
    @DrawableRes defaultImage: Int = R.drawable.placeholder
): MutableState<Bitmap?> {
    val bitmapState: MutableState<Bitmap?> = mutableStateOf(null)

    Glide.with(LocalContext.current).asBitmap().load(defaultImage).into(
        object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        }
    )

    Glide.with(LocalContext.current).asBitmap().load(url).into(
        object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        }
    )
    return bitmapState
}

fun invertBitmap(src: Bitmap): Bitmap? {
    try {
        val bitmap = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        val matrixGrayscale = ColorMatrix()
        matrixGrayscale.setSaturation(0f)

        val matrixInvert = ColorMatrix()
        matrixInvert.set(
            floatArrayOf(
                -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        val filter = ColorMatrixColorFilter(matrixInvert)
        paint.colorFilter = filter

        canvas.drawBitmap(src, 0f, 0f, paint)
        return bitmap
    } catch (e: Exception) {
        return null
    }
}

suspend fun getBitmapFromImageUrl(imgUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imgUrl)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: IOException) {
            Log.d("ImageExp", e.toString())
            null
        }
    }
}
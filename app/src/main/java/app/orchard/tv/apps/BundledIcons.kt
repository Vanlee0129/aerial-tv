package app.orchard.tv.apps

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable

/** Offline HD defaults. Owner-uploaded artwork takes precedence in AppCatalog. */
object BundledIcons {
    internal val assets = mapOf(
        "com.ktcp.video" to "tencent.png",
        "com.netease.cloudmusic.tv" to "netease.png",
        "com.tencent.karaoketv" to "karaoke.png",
        "com.xiaomi.tweather" to "weather.png",
        "com.mitv.gallery" to "gallery.png",
        "com.xiaomi.tv.gallery" to "gallery.png",
        "com.xiaomi.mitv.mediaexplorer" to "player.png",
        "com.mitv.mivideoplayer" to "player.png",
    )

    /** Decode near the tile's display size, never a full-resolution image per animation frame. */
    fun load(context: Context, packageName: String): BitmapDrawable? {
        val asset = assets[packageName] ?: return null
        return runCatching {
            val path = "icons-hd/$asset"
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.assets.open(path).use { BitmapFactory.decodeStream(it, null, options) }
            options.inJustDecodeBounds = false
            options.inSampleSize = 1
            while (options.outWidth / (options.inSampleSize * 2) >= TileArtFactory.W &&
                options.outHeight / (options.inSampleSize * 2) >= TileArtFactory.H) {
                options.inSampleSize *= 2
            }
            context.assets.open(path).use { BitmapFactory.decodeStream(it, null, options) }
                ?.let { BitmapDrawable(context.resources, it) }
        }.getOrNull()
    }
}

# HD television tiles

Six custom tvOS-inspired 16:9 PNG tiles were generated with the built-in image_gen tool.
The exact generation prompts are in [prompts.json](prompts.json); final assets live in
`app/src/main/assets/icons-hd/`. These are custom interpretations for this launcher, not official
artwork supplied by the app publishers.

| Asset | Application packages |
|---|---|
| tencent.png | com.ktcp.video |
| netease.png | com.netease.cloudmusic.tv |
| karaoke.png | com.tencent.karaoketv |
| weather.png | com.xiaomi.tweather |
| gallery.png | com.mitv.gallery, com.xiaomi.tv.gallery |
| player.png | com.mitv.mivideoplayer, com.xiaomi.mitv.mediaexplorer |

The original generated files are retained without resampling. BundledIcons decodes near the
current tile size off the UI thread; AppCatalog's existing versioned disk cache avoids repeated
asset decoding. The decoded source is released once the tile raster is built. User-uploaded
custom icons always take precedence. Unmapped apps retain the existing artwork behavior.

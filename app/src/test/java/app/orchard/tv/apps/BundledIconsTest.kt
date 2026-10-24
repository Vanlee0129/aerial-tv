package app.orchard.tv.apps

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BundledIconsTest {
    @Test fun everyMappingDecodesWithinTheTileMemoryBudget() {
        val context = RuntimeEnvironment.getApplication()
        for (pkg in BundledIcons.assets.keys) {
            val bitmap = requireNotNull(BundledIcons.load(context, pkg)) { pkg }.bitmap
            assertTrue(pkg, bitmap.width >= TileArtFactory.W)
            assertTrue(pkg, bitmap.width < TileArtFactory.W * 2)
            assertTrue(pkg, bitmap.height >= TileArtFactory.H)
            bitmap.recycle()
        }
        assertNull(BundledIcons.load(context, "example.unmapped"))
    }
}

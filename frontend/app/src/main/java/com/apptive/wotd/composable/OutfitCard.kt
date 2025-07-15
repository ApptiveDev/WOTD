package com.apptive.wotd.composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptive.wotd.R
import com.apptive.wotd.ui.theme.pretendard
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun OutfitGrid(imgTop: String? = null, imgBottom: String? = null, imgEtc: String? = null) {
    val items = listOf(
        Triple("상의", imgTop, R.drawable.ic_frog_satisfied_4),
        Triple("하의", imgBottom, R.drawable.ic_frog_satisfied_4),
        Triple("기타", imgEtc, R.drawable.ic_frog_satisfied_4)
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .height(370.dp)
            .fillMaxWidth(),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items) { (category, imageUrl, fallbackRes) ->
            OutfitCard(category, imageUrl, fallbackRes)
        }
    }
}
data class OutfitItem(val category: String, val imageRes: Int)
val outfitItems = listOf(
    OutfitItem("상의", R.drawable.ic_frog_satisfied_4),
    OutfitItem("하의", R.drawable.ic_frog_satisfied_4),
    OutfitItem("기타", R.drawable.ic_frog_satisfied_4)
)

@Composable
fun OutfitCard(category: String, imageUrl: String?, fallbackRes: Int) {
    android.util.Log.d("OutfitCardDebug", "category=$category, imageUrl=$imageUrl")
    val painter: Painter = if (!imageUrl.isNullOrBlank()) {
        rememberAsyncImagePainter(
            model = imageUrl,
            onError = { error ->
                android.util.Log.d("CoilError", "category=$category, url=$imageUrl, error=${error.result.throwable}")
            }
        )
    } else {
        painterResource(id = fallbackRes)
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFEBEEF2), shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(156.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 8.dp, y = 8.dp)
                .background(Color.White, shape = RoundedCornerShape(32.dp))
                .border(1.dp, Color(0xFF25D061), shape = RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = category,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF25D061),
                )
            )
        }

        Image(
            painter = painter,
            contentDescription = category,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 32.dp)
                .height(100.dp)
        )
    }
}
@Preview
@Composable
fun OutfitGridPreview() {
    OutfitGrid()
}
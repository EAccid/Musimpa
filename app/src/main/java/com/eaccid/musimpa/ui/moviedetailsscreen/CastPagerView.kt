package com.eaccid.musimpa.ui.moviedetailsscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eaccid.musimpa.data.domain.Actor
import com.eaccid.musimpa.ui.LogCompositions
import com.eaccid.musimpa.utils.PosterSize
import com.eaccid.musimpa.utils.toImageUri

@Composable
fun CastPagerView(actors: List<Actor>, modifier: Modifier = Modifier) {
    LogCompositions("CastPagerView")

    val pagerState = rememberPagerState(pageCount = { actors.size })
    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fixed(120.dp),
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) { page ->
        val actor = actors[page]
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(110.dp),
                painter = rememberAsyncImagePainter(
                    actor.profilePath?.toImageUri(PosterSize.W154) ?: "Non"
                ),
                contentScale = ContentScale.Crop,
                contentDescription = actor.profilePath
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(0.dp, 8.dp, 0.dp, 0.dp),
                text = actor.name.takeIf { !it.isNullOrBlank() } ?: "Unknown",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = actor.character.takeIf { !it.isNullOrBlank() } ?: "Unknown",
                style = TextStyle(
                    fontSize = 12.sp,
                )
            )
        }
    }
}

@Preview
@Composable
fun CastPagerViewPreview() {
    val mockActors = listOf(
        Actor(1, "Character 1", "/some/path1.jpg"),
        Actor(2, "Character 2", "/some/path2.jpg"),
        Actor(3, "Character 3", "/some/path3.jpg")
    )
    CastPagerView(actors = mockActors)
}
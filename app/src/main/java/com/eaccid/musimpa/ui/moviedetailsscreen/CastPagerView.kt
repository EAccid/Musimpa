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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.eaccid.musimpa.data.domain.Actor
import com.eaccid.musimpa.utils.PosterSize
import com.eaccid.musimpa.utils.toImageUri

//TODO check recomposition
//refactor
//make preview

@Composable
fun CastPagerView(actors: List<Actor>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { actors.size })
    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fixed(120.dp),
        modifier = modifier
    ) { page ->
        val actor = actors[page]
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(110.dp),
                painter = rememberAsyncImagePainter(
                    actor.profilePath?.toImageUri(PosterSize.W154) ?: "Non"
                ),
                contentDescription = actor.profilePath
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = actor.name ?: "",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            )
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = actor.character ?: "",
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
    val actors = listOf(
        Actor(1, "Actor 1", "/path1"),
        Actor(2, "Actor 2", "/path2"),
        Actor(3, "Actor 3", "/path3"),
    )
    CastPagerView(
        actors = actors,
        modifier = Modifier
    )
}
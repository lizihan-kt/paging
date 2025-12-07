package com.example.paging.presentation.composable.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.paging.R
import com.example.paging.domain.entities.Repo

@Composable
fun RepoPageItem(
    data: Repo,
    queryText: String,
    onMoveToDetailPage: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        // fillMaxWidth has the same effect as "match_parent"
        modifier = modifier
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            .clickable { onMoveToDetailPage(data.id) }
            .fillMaxWidth()
    ) {
        val (title, subTitle, content, language, starImage, stars, forkImage, forks) = createRefs()
        Text(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            text = data.name,
            color = colorResource(R.color.titleColor),
            // override the default fontSize in material design by "copy()"
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
        )


        Row(
            modifier = Modifier
                .constrainAs(subTitle) {
                    top.linkTo(title.bottom)
                    start.linkTo(parent.start)
                }
                .padding(top = 8.dp)
        ) {
            val splits = data.fullName.split(Regex(queryText, RegexOption.IGNORE_CASE))
            (0..<splits.size).forEach { index ->
                if (!(splits.size > 1 && index == 0)) {
                    Text(
                        text = queryText,
                        color = colorResource(R.color.colorAccent),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp)
                    )
                }
                Text(
                    text = splits[index],
                    color = colorResource(R.color.titleColor),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp)
                )
            }
        }
        Text(
            modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(subTitle.bottom)
                    start.linkTo(parent.start)
                    // end.linkTo(parent.end)
                }
                .padding(vertical = 12.dp),
            text = data.description ?: stringResource(R.string.no_repository_description),
            maxLines = 10,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            modifier = Modifier
                .constrainAs(language) {
                    top.linkTo(content.bottom)
                    start.linkTo(parent.start)
                }
                .padding(vertical = 12.dp),
            text = stringResource(
                R.string.language,
                data.language ?: stringResource(R.string.unknown)
            ),
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            modifier = Modifier
                .constrainAs(forks) {
                    end.linkTo(parent.end)
                    top.linkTo(content.bottom)
                }
                .padding(vertical = 12.dp),
            text = data.forks.toString(),
            style = MaterialTheme.typography.bodySmall
        )
        Image(
            modifier = Modifier
                .constrainAs(forkImage) {
                    top.linkTo(content.bottom)
                    bottom.linkTo(forks.bottom) // constrain the vertical size of the image to the size of the "forks"
                    end.linkTo(forks.start)
                }
                .padding(vertical = 12.dp)
                .size(20.dp),    // use Modifier.size() to resize image in ConstraintLayout
            painter = painterResource(R.drawable.ic_git_branch),
            contentDescription = "fork image",
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .constrainAs(stars) {
                    top.linkTo(content.bottom)
                    bottom.linkTo(forks.bottom)
                    end.linkTo(forkImage.start)
                }
                .padding(vertical = 12.dp),
            text = data.stars.toString(),
            style = MaterialTheme.typography.bodySmall,
        )
        Image(
            modifier = Modifier
                .constrainAs(starImage) {
                    top.linkTo(content.bottom)
                    bottom.linkTo(stars.bottom)
                    end.linkTo(stars.start)
                }
                .padding(vertical = 12.dp)
                .size(20.dp),
            painter = painterResource(R.drawable.ic_star),
            contentDescription = "star image"
        )
    }
}


@Composable
@Preview(showSystemUi = true)
private fun PageItemPreview() = RepoPageItem(
    Repo(
        id = 1,
        name = "android-architecture",
        description = "A collection of samples to discuss and showcase different architectural tools and patterns for Android apps.",
        stars = 30,
        forks = 30,
        language = "Kotlin",
        url = "https://developer.android.com/jetpack/compose/layouts/constraintlayout",
        fullName = "android-architecture-fullName"
    ),
    "android",
    {}
)

// @Composable
// @Preview
// fun JumpBarComposable(
//     currentPage: Int = 1,
//     pivotPage: Int= 1,
//     step: Int = 2,
//     onCurrentPageChanged: (Int)->Unit ={},
//     modifier: Modifier = Modifier,
// ) {
//     val firstPageSign = stringResource(R.string.first_page_sign)
//     val lastPageSign = stringResource(R.string.first_page_sign)
//     Row(
//         modifier = modifier.fillMaxWidth(),
//         horizontalArrangement = Arrangement.spacedBy(
//             space = 2.dp,
//             alignment = Alignment.CenterHorizontally
//         )
//     ) {
//         mutableListOf<String>().apply {
//             add("<<")
//             addAll(getPageNumberList(pivotPage, step).map { it.toString() })
//             add(">>")
//         }.forEach {page->
//             Box(
//                 modifier = Modifier.size(24.dp).border(border = BorderStroke(width = 1.dp, color = Color.Black))
//                     .background(if (page == currentPage.toString()) colorResource(R.color.greyAlpha) else MaterialTheme.colorScheme.surface)
//                     .clickable {onCurrentPageChanged(
//                         when(page){
//                             firstPageSign -> 1
//                             lastPageSign -> pivotPage + step + 1
//                             else -> page.toInt()
//                         }
//                     )}
//             ) {
//                 Text(
//                     text = page.toString(),
//                     style = MaterialTheme.typography.titleSmall,
//                     modifier = Modifier.align(Alignment.Center)
//                 )
//             }
//         }
//
//     }
// }
//
// fun getPageNumberList(pivotPage: Int, step: Int): List<Int> {
//     assert(pivotPage - step >= 0)
//     val pages = mutableListOf<Int>()
//     (pivotPage - step..pivotPage + step).forEach { pages.add(it) }
//     return pages
// }


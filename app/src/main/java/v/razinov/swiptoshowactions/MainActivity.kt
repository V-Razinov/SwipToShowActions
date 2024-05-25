package v.razinov.swiptoshowactions

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import v.razinov.swiptoshowactions.swipeable.Swipeable
import v.razinov.swiptoshowactions.swipeable.rememberSwipeableState
import v.razinov.swiptoshowactions.ui.theme.SwipToShowActionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwipToShowActionsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        val context = LocalContext.current
                        val showMessage = { message: String ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }

                        val data = remember {
                            List(100) { it.toString() }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = data,
                                key = { it },
                                contentType = { "qwe" }
                            ) { item ->
                                val swipeableState = rememberSwipeableState()
                                Swipeable(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .background(Color.Blue)
                                        .clipToBounds(),
                                    state = swipeableState,
                                    content = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight()
                                                .clip(
                                                    RoundedCornerShape(
                                                        topEnd = (16 * swipeableState.progress).dp,
                                                        bottomEnd = (16 * swipeableState.progress).dp,
                                                    )
                                                )
                                                .background(Color.Red)
                                        ) {
                                            Text(text = "WrapContent\nWrapContent\nWrapContent\nWrapContent\nWrapContent\nWrapContent")
                                        }
                                    },
                                    endAction = {
                                        Actions(
                                            modifier = Modifier.fillMaxHeight(),
                                            showMessage,
                                            swipeableState.progress
                                        )
                                    }
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    @Composable
    private fun Actions(
        modifier: Modifier = Modifier,
        showMessage: (String) -> Unit,
        progress: Float
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(onClick = {
                        showMessage("Edit")
                    })
                    .alpha(progress.coerceAtLeast(0.3f))
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    tint = Color.White,
                    contentDescription = null
                )
                Text(text = "Edit", color = Color.White)
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color.Gray)
                    .clickable(onClick = { showMessage("Delete") })
                    .alpha(progress.coerceAtLeast(0.3f))
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = Color.White,
                    contentDescription = null
                )
                Text(text = "Delete", color = Color.White)
            }
        }
    }

    @Preview
    @Composable
    private fun ActionsPreview() {
        Actions(showMessage = { }, progress = 1f)
    }
}
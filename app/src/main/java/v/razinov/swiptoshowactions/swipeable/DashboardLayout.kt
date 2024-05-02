@file:OptIn(ExperimentalFoundationApi::class)

package v.razinov.swiptoshowactions.swipeable

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

enum class DashboardState {
    EXPANDED, PEEKING
}

@Composable
fun DashboardLayout(
    modifier: Modifier = Modifier,
    topContent: @Composable BoxScope.() -> Unit,
    bottomSheetContent: @Composable BoxScope.() -> Unit,
) {
    var topContentHeight by remember { mutableIntStateOf(0) }
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = DashboardState.PEEKING,
            anchors = DraggableAnchors {
                DashboardState.EXPANDED at 0f
                DashboardState.PEEKING at 0f
            },
            positionalThreshold = { it / 3 },
            velocityThreshold = { 0f },
            animationSpec = spring()
        )
    }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.onSizeChanged {
                draggableState.updateAnchors(
                    DraggableAnchors {
                        DashboardState.EXPANDED at 0f
                        DashboardState.PEEKING at it.height.toFloat()
                    },
                    newTarget = draggableState.currentValue
                )
                topContentHeight = it.height
            },
            content = topContent
        )
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = draggableState.offset.roundToInt()
                    )
                }
                .anchoredDraggable(
                    state = draggableState,
                    orientation = Orientation.Vertical
                ),
            content = bottomSheetContent
        )
    }
}
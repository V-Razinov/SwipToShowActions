@file:OptIn(ExperimentalFoundationApi::class)

package v.razinov.swiptoshowactions.swipeable

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastForEach
import kotlin.math.roundToInt

enum class SwipeableStateValue {
    ACTIONS_HIDDEN, ACTIONS_VISIBLE
}

@Composable
fun rememberSwipeableState(
    initialState: SwipeableStateValue = SwipeableStateValue.ACTIONS_HIDDEN,
): SwipeableState = rememberSaveable(
    initialState,
    saver = SwipeableState.Saver,
) {
    SwipeableState(initialState)
}

@Stable
class SwipeableState internal constructor(
    initialState: SwipeableStateValue,
) {

    internal var actionsWidth = 0f

    internal val draggableState: AnchoredDraggableState<SwipeableStateValue> =
        AnchoredDraggableState(
            initialValue = initialState,
            anchors = DraggableAnchors {
                SwipeableStateValue.ACTIONS_HIDDEN at 0f
                SwipeableStateValue.ACTIONS_VISIBLE at 0f
            },
            positionalThreshold = { it / 3 },
            velocityThreshold = { actionsWidth / 3 },
            animationSpec = spring(),
        )

    val progress: Float by derivedStateOf {
        when (draggableState.currentValue) {
            SwipeableStateValue.ACTIONS_HIDDEN -> {
                if (draggableState.targetValue == SwipeableStateValue.ACTIONS_HIDDEN) {
                    0f
                } else {
                    draggableState.progress
                }
            }

            SwipeableStateValue.ACTIONS_VISIBLE -> {
                if (draggableState.targetValue == SwipeableStateValue.ACTIONS_VISIBLE) {
                    1f
                } else {
                    1f - draggableState.progress
                }
            }
        }
    }

    val offset: Float by derivedStateOf {
        draggableState.offset.takeIf { !it.isNaN() } ?: 0f
    }

    internal fun updateActionWidth(
        width: Float,
    ) {
        actionsWidth = width.coerceAtLeast(0f)
        draggableState.updateAnchors(
            DraggableAnchors {
                SwipeableStateValue.ACTIONS_HIDDEN at 0f
                SwipeableStateValue.ACTIONS_VISIBLE at -actionsWidth
            },
            newTarget = draggableState.currentValue
        )
    }

    companion object {
        val Saver: Saver<SwipeableState, *> = Saver(
            save = { it.draggableState.currentValue },
            restore = { SwipeableState(it) }
        )
    }
}

@Composable
fun Swipeable(
    modifier: Modifier = Modifier,
    state: SwipeableState = rememberSwipeableState(),
    content: @Composable BoxScope.(progress: Float) -> Unit,
    endAction: @Composable BoxScope.(progress: Float) -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .onSizeChanged {
                        state.updateActionWidth(it.width.toFloat())
                    }
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Horizontal
                    ),
                contentAlignment = Alignment.Center,
                content = { endAction(state.progress) }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(
                            x = state.offset
                                .roundToInt()
                                .coerceIn(-state.actionsWidth.toInt(), 0),
                            y = 0
                        )
                    }
                    .anchoredDraggable(
                        state = state.draggableState,
                        orientation = Orientation.Horizontal
                    ),
                content = { content(state.progress) }
            )
        },
        measurePolicy = { measurables, constraint ->
            val actionsMeasurable = measurables[0]
            val contentMeasurable = measurables[1]

            val height = maxOf(
                actionsMeasurable.minIntrinsicHeight(0),
                contentMeasurable.minIntrinsicHeight(0)
            )
            val c = constraint.copy(
                minHeight = 0,
                maxHeight = height
            )
            val actionsPlaceable = actionsMeasurable.measure(c.copy(minWidth = 0))
            val contentPlaceable = contentMeasurable.measure(c)

            layout(c.maxWidth, c.maxHeight) {
                actionsPlaceable.place(
                    x = c.maxWidth - actionsPlaceable.width,
                    y = 0
                )
                contentPlaceable.place(0, 0)
            }
        }
    )
//    BoxWithConstraints(modifier = modifier) {
//        Box(
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .fillMaxHeight()
//                .onSizeChanged {
//                    state.updateActionWidth(it.width.toFloat())
//                }
//                .anchoredDraggable(
//                    state = state.draggableState,
//                    orientation = Orientation.Horizontal
//                ),
//            contentAlignment = Alignment.Center,
//            content = { endAction(state.progress) }
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .offset {
//                    IntOffset(
//                        x = state.offset
//                            .roundToInt()
//                            .coerceIn(-state.actionsWidth.toInt(), 0),
//                        y = 0
//                    )
//                }
//                .anchoredDraggable(
//                    state = state.draggableState,
//                    orientation = Orientation.Horizontal
//                ),
//            content = { content(state.progress) }
//        )
//    }
}

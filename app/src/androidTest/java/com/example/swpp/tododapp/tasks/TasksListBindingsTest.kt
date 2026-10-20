package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    @Test
    fun setDueDateLabel_completedOverdue_visibleClear() = onMainThread {
        // Given
        val today = LocalDate.now()
        val textView = staleTextView()
        val task = task(today.minusDays(1), true)

        // When
        setDueDateLabel(textView, task)

        // Then
        assertEquals("CLEAR" to View.VISIBLE, snapshot(textView))
    }

    @Test
    fun setDueDateLabel_completedWithoutDueDate_visibleClear() = onMainThread {
        // Given
        val textView = staleTextView()
        val task = task(null, true)

        // When
        setDueDateLabel(textView, task)

        // Then
        assertEquals("CLEAR" to View.VISIBLE, snapshot(textView))
    }

    @Test
    fun setDueDateLabel_activeWithoutDueDate_clearedAndGone() = onMainThread {
        // Given
        val textView = staleTextView()
        val task = task(null, false)

        // When
        setDueDateLabel(textView, task)

        // Then
        assertEquals("" to View.GONE, snapshot(textView))
    }

    @Test
    fun setDueDateLabel_nullTask_clearedAndGone() = onMainThread {
        // Given
        val textView = staleTextView()
        val task: Task? = null

        // When
        setDueDateLabel(textView, task)

        // Then
        assertEquals("" to View.GONE, snapshot(textView))
    }

    @Test
    fun setDueDateLabel_dueToday_visibleDDay() = onMainThread {
        // Given
        val today = LocalDate.now()
        val textView = staleTextView()
        val task = task(today, false)

        // When
        setDueDateLabel(textView, task)

        // Then
        assertEquals("D-Day" to View.VISIBLE, snapshot(textView))
    }

    @Test
    fun setDueDateLabel_future61Days_visibleDMinus61() = onMainThread {
        // Given
        val today = LocalDate.now()
        val textView = staleTextView()
        val task = task(today.plusDays(61), false)

        // When
        setDueDateLabel(textView, task)

        // Then
        assertEquals("D-61" to View.VISIBLE, snapshot(textView))
    }

    @Test
    fun setDueDateLabel_overdue61Days_visibleDPlus61() = onMainThread {
        // Given
        val today = LocalDate.now()
        val textView = staleTextView()
        val task = task(today.minusDays(61), false)

        // When
        setDueDateLabel(textView, task)

        // Then
        assertEquals("D+61" to View.VISIBLE, snapshot(textView))
    }

    @Test
    fun setDueDateLabel_todayBoundary_visibleRelativeLabels() = onMainThread {
        // Given
        val textView = staleTextView()
        val today = LocalDate.now()
        val tasks = listOf(
            task(today.minusDays(1), false),
            task(today, false),
            task(today.plusDays(1), false)
        )

        // When - Rebind the same view and capture every intermediate state.
        val actual = tasks.map { task ->
            setDueDateLabel(textView, task)
            snapshot(textView)
        }

        // Then
        assertEquals(
            listOf("D+1" to View.VISIBLE, "D-Day" to View.VISIBLE, "D-1" to View.VISIBLE),
            actual
        )
    }

    @Test
    fun setDueDateLabel_completedTodayBoundary_visibleClear() = onMainThread {
        // Given
        val textView = staleTextView()
        val today = LocalDate.now()
        val tasks = listOf(
            task(today.minusDays(1), true),
            task(today, true),
            task(today.plusDays(1), true)
        )

        // When - Rebind the same view and capture every intermediate state.
        val actual = tasks.map { task ->
            setDueDateLabel(textView, task)
            snapshot(textView)
        }

        // Then
        assertEquals(
            listOf("CLEAR" to View.VISIBLE, "CLEAR" to View.VISIBLE, "CLEAR" to View.VISIBLE),
            actual
        )
    }

    @Test
    fun setDueDateLabel_absentPresentAbsentDueDate_clearsAndRestoresView() = onMainThread {
        // Given
        val textView = staleTextView()
        val today = LocalDate.now()
        val tasks = listOf(task(null, false), task(today.plusDays(1), false), task(null, false))

        // When - Rebind the same view and capture every intermediate state.
        val actual = tasks.map { task ->
            setDueDateLabel(textView, task)
            snapshot(textView)
        }

        // Then
        assertEquals(
            listOf("" to View.GONE, "D-1" to View.VISIBLE, "" to View.GONE),
            actual
        )
    }

    @Test
    fun setDueDateLabel_nullPresentNullTask_clearsAndRestoresView() = onMainThread {
        // Given
        val textView = staleTextView()
        val today = LocalDate.now()
        val tasks = listOf(null, task(today, false), null)

        // When - Rebind the same view and capture every intermediate state.
        val actual = tasks.map { task ->
            setDueDateLabel(textView, task)
            snapshot(textView)
        }

        // Then
        assertEquals(
            listOf("" to View.GONE, "D-Day" to View.VISIBLE, "" to View.GONE),
            actual
        )
    }

    @Test
    fun setDueDateLabel_completionToggleWithoutDueDate_clearsAndRestoresView() = onMainThread {
        // Given
        val textView = staleTextView()
        val tasks = listOf(task(null, false), task(null, true), task(null, false))

        // When - Rebind the same view and capture every intermediate state.
        val actual = tasks.map { task ->
            setDueDateLabel(textView, task)
            snapshot(textView)
        }

        // Then
        assertEquals(
            listOf("" to View.GONE, "CLEAR" to View.VISIBLE, "" to View.GONE),
            actual
        )
    }

    @Test
    fun setDueDateLabel_completionToggleWithDueDate_switchesBetweenDueLabelAndClear() = onMainThread {
        // Given
        val textView = staleTextView()
        val today = LocalDate.now()
        val tasks = listOf(
            task(today.plusDays(1), false),
            task(today.plusDays(1), true),
            task(today.plusDays(1), false)
        )

        // When - Rebind the same view and capture every intermediate state.
        val actual = tasks.map { task ->
            setDueDateLabel(textView, task)
            snapshot(textView)
        }

        // Then
        assertEquals(
            listOf("D-1" to View.VISIBLE, "CLEAR" to View.VISIBLE, "D-1" to View.VISIBLE),
            actual
        )
    }

    @Test
    fun setDueDateLabel_nonemptyLabelFromAllVisibilities_visibleDMinus1() = onMainThread {
        // Given
        val today = LocalDate.now()
        val task = task(today.plusDays(1), false)
        val textViews = listOf(View.VISIBLE, View.INVISIBLE, View.GONE).map { visibility ->
            staleTextView().apply { this.visibility = visibility }
        }

        // When
        val actual = textViews.map { textView ->
            setDueDateLabel(textView, task)
            snapshot(textView)
        }

        // Then
        assertEquals(
            listOf("D-1" to View.VISIBLE, "D-1" to View.VISIBLE, "D-1" to View.VISIBLE),
            actual
        )
    }

    private fun task(dueDate: LocalDate?, isCompleted: Boolean) = Task(
        title = "Task",
        description = "Description",
        id = "task-1",
        dueDate = dueDate,
        isCompleted = isCompleted
    )

    private fun staleTextView() =
        TextView(InstrumentationRegistry.getInstrumentation().targetContext).apply {
            text = "stale"
            visibility = View.VISIBLE
        }

    private fun snapshot(textView: TextView) =
        textView.text.toString() to textView.visibility

    private fun onMainThread(block: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { block() }
    }
}


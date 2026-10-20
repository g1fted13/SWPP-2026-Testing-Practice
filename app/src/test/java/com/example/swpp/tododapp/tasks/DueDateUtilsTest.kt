package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DueDateUtilsTest {

    @Test
    fun dueDateLabel_completedOverdue_CLEAR() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-20") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = true

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("CLEAR"), actual)
    }

    @Test
    fun dueDateLabel_completedWithoutDueDate_CLEAR() {
        // Given
        val dates = listOf(
            null to LocalDate.parse("2026-10-21")
        )
        val isCompleted = true

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("CLEAR"), actual)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_emptyLabel() {
        // Given
        val dates = listOf(
            null to LocalDate.parse("2026-10-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf(""), actual)
    }

    @Test
    fun dueDateLabel_dueToday_dDay() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-21") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-Day"), actual)
    }

    @Test
    fun dueDateLabel_futureSameMonth_dMinus3() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-24") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-3"), actual)
    }

    @Test
    fun dueDateLabel_pastSameMonth_dPlus3() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-18") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+3"), actual)
    }

    @Test
    fun dueDateLabel_futureMultipleMonths_dMinus61() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-12-21") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-61"), actual)
    }

    @Test
    fun dueDateLabel_pastMultipleMonths_dPlus61() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-21") to LocalDate.parse("2026-12-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+61"), actual)
    }

    @Test
    fun dueDateLabel_todayBoundary_relativeLabels() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-20") to LocalDate.parse("2026-10-21"),
            LocalDate.parse("2026-10-21") to LocalDate.parse("2026-10-21"),
            LocalDate.parse("2026-10-22") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+1", "D-Day", "D-1"), actual)
    }

    @Test
    fun dueDateLabel_completedTodayBoundary_CLEAR() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-20") to LocalDate.parse("2026-10-21"),
            LocalDate.parse("2026-10-21") to LocalDate.parse("2026-10-21"),
            LocalDate.parse("2026-10-22") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = true

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("CLEAR", "CLEAR", "CLEAR"), actual)
    }

    @Test
    fun dueDateLabel_monthStartFuture_continuousDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-01-31") to LocalDate.parse("2026-01-30"),
            LocalDate.parse("2026-02-01") to LocalDate.parse("2026-01-30"),
            LocalDate.parse("2026-02-02") to LocalDate.parse("2026-01-30")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-1", "D-2", "D-3"), actual)
    }

    @Test
    fun dueDateLabel_monthStartOverdue_continuousDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-01-30") to LocalDate.parse("2026-01-31"),
            LocalDate.parse("2026-01-30") to LocalDate.parse("2026-02-01"),
            LocalDate.parse("2026-01-30") to LocalDate.parse("2026-02-02")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+1", "D+2", "D+3"), actual)
    }

    @Test
    fun dueDateLabel_wholeMonthFuture_totalDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-02-14") to LocalDate.parse("2026-01-15"),
            LocalDate.parse("2026-02-15") to LocalDate.parse("2026-01-15"),
            LocalDate.parse("2026-02-16") to LocalDate.parse("2026-01-15")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-30", "D-31", "D-32"), actual)
    }

    @Test
    fun dueDateLabel_wholeMonthOverdue_totalDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-01-15") to LocalDate.parse("2026-02-14"),
            LocalDate.parse("2026-01-15") to LocalDate.parse("2026-02-15"),
            LocalDate.parse("2026-01-15") to LocalDate.parse("2026-02-16")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+30", "D+31", "D+32"), actual)
    }

    @Test
    fun dueDateLabel_wholeYearFuture_totalDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2027-10-20") to LocalDate.parse("2026-10-21"),
            LocalDate.parse("2027-10-21") to LocalDate.parse("2026-10-21"),
            LocalDate.parse("2027-10-22") to LocalDate.parse("2026-10-21")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-364", "D-365", "D-366"), actual)
    }

    @Test
    fun dueDateLabel_wholeYearOverdue_totalDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-10-21") to LocalDate.parse("2027-10-20"),
            LocalDate.parse("2026-10-21") to LocalDate.parse("2027-10-21"),
            LocalDate.parse("2026-10-21") to LocalDate.parse("2027-10-22")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+364", "D+365", "D+366"), actual)
    }

    @Test
    fun dueDateLabel_yearStartFuture_continuousDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-12-31") to LocalDate.parse("2026-12-30"),
            LocalDate.parse("2027-01-01") to LocalDate.parse("2026-12-30"),
            LocalDate.parse("2027-01-02") to LocalDate.parse("2026-12-30")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-1", "D-2", "D-3"), actual)
    }

    @Test
    fun dueDateLabel_yearStartOverdue_continuousDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2026-12-30") to LocalDate.parse("2026-12-31"),
            LocalDate.parse("2026-12-30") to LocalDate.parse("2027-01-01"),
            LocalDate.parse("2026-12-30") to LocalDate.parse("2027-01-02")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+1", "D+2", "D+3"), actual)
    }

    @Test
    fun dueDateLabel_leapDayFuture_includesLeapDay() {
        // Given
        val dates = listOf(
            LocalDate.parse("2028-02-28") to LocalDate.parse("2028-02-27"),
            LocalDate.parse("2028-02-29") to LocalDate.parse("2028-02-27"),
            LocalDate.parse("2028-03-01") to LocalDate.parse("2028-02-27")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-1", "D-2", "D-3"), actual)
    }

    @Test
    fun dueDateLabel_leapDayOverdue_includesLeapDay() {
        // Given
        val dates = listOf(
            LocalDate.parse("2028-02-27") to LocalDate.parse("2028-02-28"),
            LocalDate.parse("2028-02-27") to LocalDate.parse("2028-02-29"),
            LocalDate.parse("2028-02-27") to LocalDate.parse("2028-03-01")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D+1", "D+2", "D+3"), actual)
    }

    @Test
    fun dueDateLabel_nonLeapFebruary_continuousDays() {
        // Given
        val dates = listOf(
            LocalDate.parse("2027-02-28") to LocalDate.parse("2027-02-27"),
            LocalDate.parse("2027-03-01") to LocalDate.parse("2027-02-27"),
            LocalDate.parse("2027-03-02") to LocalDate.parse("2027-02-27")
        )
        val isCompleted = false

        // When
        val actual = dates.map { (dueDate, today) ->
            dueDateLabel(dueDate, today, isCompleted)
        }

        // Then
        assertEquals(listOf("D-1", "D-2", "D-3"), actual)
    }
}


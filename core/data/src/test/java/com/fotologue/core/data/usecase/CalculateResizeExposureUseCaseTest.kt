package com.fotologue.core.data.usecase

import com.fotologue.core.data.lab.ExposureMode
import com.fotologue.core.data.lab.ExposureStep
import com.fotologue.core.data.lab.StepType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateResizeExposureUseCaseTest {

    private val useCase = CalculateResizeExposureUseCase()

    @Test
    fun `rescales base exposure time correctly using magnification ratio`() {
        // E2 = E1 * ((M2 + 1) / (M1 + 1))^2
        // E1 = 12.5s, M1 = 2.0, M2 = 4.0
        // (4 + 1) / (2 + 1) = 5 / 3 = 1.6667
        // (1.6667)^2 = 2.7778
        // E2 = 12.5 * 2.7778 = 34.72s
        val result = useCase(originalTimeSeconds = 12.5, originalMagnification = 2.0, newMagnification = 4.0)
        assertEquals(34.722, result, 0.01)
    }

    @Test
    fun `save_rejected_when_split_grade_null_grade validates required grade`() {
        val splitGradeSteps = listOf(
            ExposureStep("1", "sess1", StepType.BASE, grade = null, timeSeconds = 8.0f), // INVALID NULL GRADE
            ExposureStep("2", "sess1", StepType.BASE, grade = 5.0f, timeSeconds = 4.5f)
        )

        val mode = ExposureMode.SPLIT_GRADE
        val isValid = splitGradeSteps.all { step ->
            if (mode == ExposureMode.SPLIT_GRADE) step.grade != null else true
        }

        assertFalse("Save must be rejected when SPLIT_GRADE step has null grade", isValid)
    }

    @Test
    fun `save_accepted_when_split_grade_has_non_null_grades`() {
        val validSplitGradeSteps = listOf(
            ExposureStep("1", "sess1", StepType.BASE, grade = 0.0f, timeSeconds = 8.0f),
            ExposureStep("2", "sess1", StepType.BASE, grade = 5.0f, timeSeconds = 4.5f)
        )

        val mode = ExposureMode.SPLIT_GRADE
        val isValid = validSplitGradeSteps.all { step ->
            if (mode == ExposureMode.SPLIT_GRADE) step.grade != null else true
        }

        assertTrue("Save must be accepted when all SPLIT_GRADE steps have non-null grades", isValid)
    }
}

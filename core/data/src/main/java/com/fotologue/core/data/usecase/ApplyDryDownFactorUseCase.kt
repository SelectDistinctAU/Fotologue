package com.fotologue.core.data.usecase

import javax.inject.Inject

class ApplyDryDownFactorUseCase @Inject constructor() {

    /**
     * Calculates the wet-preview exposure time to compensate for wet-to-dry paper darkening.
     *
     * Formula:
     * previewTime = actualTime / (1 + dryDownFactorPercent / 100)
     *
     * @param actualExposureSeconds Final dry exposure target in seconds
     * @param dryDownFactorPercent Paper-specific dry-down percentage (e.g. 8.0 to 12.0 for Baryta/FB paper)
     * @return Adjusted wet-preview exposure time in seconds
     */
    operator fun invoke(
        actualExposureSeconds: Double,
        dryDownFactorPercent: Double
    ): Double {
        if (actualExposureSeconds <= 0.0 || dryDownFactorPercent <= 0.0) {
            return actualExposureSeconds
        }

        val multiplier = 1.0 + (dryDownFactorPercent / 100.0)
        return actualExposureSeconds / multiplier
    }
}

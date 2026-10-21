package com.fotologue.core.data.usecase

import javax.inject.Inject
import kotlin.math.pow

class CalculateResizeExposureUseCase @Inject constructor() {

    /**
     * Calculates the new exposure time when resizing an enlarger print based on magnification ratio.
     *
     * Inverse Square Law in Magnification Form:
     * E2 = E1 * ((M2 + 1) / (M1 + 1))^2
     *
     * @param originalTimeSeconds Initial exposure time E1 in seconds
     * @param originalMagnification Initial magnification ratio M1 (e.g. 2.0)
     * @param newMagnification Target magnification ratio M2 (e.g. 4.0)
     * @return Calculated new exposure time E2 in seconds
     */
    operator fun invoke(
        originalTimeSeconds: Double,
        originalMagnification: Double,
        newMagnification: Double
    ): Double {
        if (originalTimeSeconds <= 0.0 || originalMagnification <= 0.0 || newMagnification <= 0.0) {
            return originalTimeSeconds
        }

        val factor = ((newMagnification + 1.0) / (originalMagnification + 1.0)).pow(2)
        return originalTimeSeconds * factor
    }
}

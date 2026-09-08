package com.fotologue.app.data

data class FilmPreset(
    val manufacturer: String,
    val name: String,
    val iso: Int,
    val type: String
) {
    val displayName: String get() = "$manufacturer $name"
}

object FilmPresets {
    val presets = listOf(
        // Kodak
        FilmPreset("Kodak", "Portra 160", 160, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Kodak", "Portra 400", 400, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Kodak", "Portra 800", 800, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Kodak", "Ektar 100", 100, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Kodak", "Gold 200", 200, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Kodak", "UltraMax 400", 400, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Kodak", "Tri-X 400", 400, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Kodak", "T-Max 100", 100, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Kodak", "T-Max 400", 400, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Kodak", "Ektachrome E100", 100, Film.TYPE_SLIDE),

        // Ilford
        FilmPreset("Ilford", "HP5 Plus 400", 400, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Ilford", "FP4 Plus 125", 125, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Ilford", "Delta 100 Professional", 100, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Ilford", "Delta 400 Professional", 400, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Ilford", "Delta 3200 Professional", 3200, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Ilford", "Pan F Plus 50", 50, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Ilford", "XP2 Super 400", 400, Film.TYPE_BLACK_AND_WHITE),

        // Fujifilm
        FilmPreset("Fujifilm", "Fujicolor 200", 200, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Fujifilm", "Superia X-TRA 400", 400, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Fujifilm", "Velvia 50", 50, Film.TYPE_SLIDE),
        FilmPreset("Fujifilm", "Velvia 100", 100, Film.TYPE_SLIDE),
        FilmPreset("Fujifilm", "Provia 100F", 100, Film.TYPE_SLIDE),
        FilmPreset("Fujifilm", "Neopan 100 Acros II", 100, Film.TYPE_BLACK_AND_WHITE),

        // Cinestill
        FilmPreset("Cinestill", "50Daylight", 50, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Cinestill", "800Tungsten", 800, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Cinestill", "400Dynamic", 400, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Cinestill", "BwXX", 250, Film.TYPE_BLACK_AND_WHITE),

        // Foma
        FilmPreset("Foma", "Fomapan 100 Classic", 100, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Foma", "Fomapan 200 Creative", 200, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Foma", "Fomapan 400 Action", 400, Film.TYPE_BLACK_AND_WHITE),

        // Lomography
        FilmPreset("Lomography", "Color Negative 100", 100, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Lomography", "Color Negative 400", 400, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Lomography", "Color Negative 800", 800, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Lomography", "LomoChrome Metropolis", 200, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Lomography", "LomoChrome Purple", 400, Film.TYPE_COLOR_NEGATIVE),

        // AgfaPhoto
        FilmPreset("AgfaPhoto", "APX 100 Professional", 100, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("AgfaPhoto", "APX 400 Professional", 400, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("AgfaPhoto", "Vista 200", 200, Film.TYPE_COLOR_NEGATIVE),

        // Rollei
        FilmPreset("Rollei", "Retro 80s", 80, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Rollei", "Retro 400s", 400, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Rollei", "RPX 100", 100, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Rollei", "RPX 400", 400, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Rollei", "Superpan 200", 200, Film.TYPE_BLACK_AND_WHITE),

        // Bergger
        FilmPreset("Bergger", "Pancro 400", 400, Film.TYPE_BLACK_AND_WHITE),

        // Harman
        FilmPreset("Harman", "Phoenix 200", 200, Film.TYPE_COLOR_NEGATIVE),
        FilmPreset("Harman", "Kentmere Pan 100", 100, Film.TYPE_BLACK_AND_WHITE),
        FilmPreset("Harman", "Kentmere Pan 400", 400, Film.TYPE_BLACK_AND_WHITE)
    )

    val manufacturers = presets.map { it.manufacturer }.distinct().sorted()
    
    fun getFilmsForManufacturer(manufacturer: String) = presets.filter { it.manufacturer == manufacturer }
}

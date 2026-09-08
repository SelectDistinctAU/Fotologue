package com.fotologue.app.data

data class FilmPreset(
    val manufacturer: String,
    val name: String,
    val iso: Int,
    val type: String,
    val abbreviation: String,
    val expectedExposures: Int = 36
) {
    val displayName: String get() = "$manufacturer $name"
}

object FilmPresets {
    val presets = listOf(
        // Kodak
        FilmPreset("Kodak", "Portra 160", 160, Film.TYPE_COLOR_NEGATIVE, "P160", 36),
        FilmPreset("Kodak", "Portra 400", 400, Film.TYPE_COLOR_NEGATIVE, "P400", 36),
        FilmPreset("Kodak", "Portra 800", 800, Film.TYPE_COLOR_NEGATIVE, "P800", 36),
        FilmPreset("Kodak", "Ektar 100", 100, Film.TYPE_COLOR_NEGATIVE, "E100", 36),
        FilmPreset("Kodak", "Gold 200", 200, Film.TYPE_COLOR_NEGATIVE, "G200", 36),
        FilmPreset("Kodak", "UltraMax 400", 400, Film.TYPE_COLOR_NEGATIVE, "U400", 36),
        FilmPreset("Kodak", "Tri-X 400", 400, Film.TYPE_BLACK_AND_WHITE, "TX400", 36),
        FilmPreset("Kodak", "T-Max 100", 100, Film.TYPE_BLACK_AND_WHITE, "TM100", 36),
        FilmPreset("Kodak", "T-Max 400", 400, Film.TYPE_BLACK_AND_WHITE, "TM400", 36),
        FilmPreset("Kodak", "Ektachrome E100", 100, Film.TYPE_SLIDE, "E100G", 36),

        // Ilford
        FilmPreset("Ilford", "HP5 Plus 400", 400, Film.TYPE_BLACK_AND_WHITE, "HP5"),
        FilmPreset("Ilford", "FP4 Plus 125", 125, Film.TYPE_BLACK_AND_WHITE, "FP4"),
        FilmPreset("Ilford", "Delta 100 Professional", 100, Film.TYPE_BLACK_AND_WHITE, "D100"),
        FilmPreset("Ilford", "Delta 400 Professional", 400, Film.TYPE_BLACK_AND_WHITE, "D400"),
        FilmPreset("Ilford", "Delta 3200 Professional", 3200, Film.TYPE_BLACK_AND_WHITE, "D3200"),
        FilmPreset("Ilford", "Pan F Plus 50", 50, Film.TYPE_BLACK_AND_WHITE, "PANF"),
        FilmPreset("Ilford", "XP2 Super 400", 400, Film.TYPE_BLACK_AND_WHITE, "XP2"),

        // Fujifilm
        FilmPreset("Fujifilm", "Fujicolor 200", 200, Film.TYPE_COLOR_NEGATIVE, "F200"),
        FilmPreset("Fujifilm", "Superia X-TRA 400", 400, Film.TYPE_COLOR_NEGATIVE, "S400"),
        FilmPreset("Fujifilm", "Velvia 50", 50, Film.TYPE_SLIDE, "V50"),
        FilmPreset("Fujifilm", "Velvia 100", 100, Film.TYPE_SLIDE, "V100"),
        FilmPreset("Fujifilm", "Provia 100F", 100, Film.TYPE_SLIDE, "RDP"),
        FilmPreset("Fujifilm", "Neopan 100 Acros II", 100, Film.TYPE_BLACK_AND_WHITE, "ACROS"),

        // Cinestill
        FilmPreset("Cinestill", "50Daylight", 50, Film.TYPE_COLOR_NEGATIVE, "50D"),
        FilmPreset("Cinestill", "800Tungsten", 800, Film.TYPE_COLOR_NEGATIVE, "800T"),
        FilmPreset("Cinestill", "400Dynamic", 400, Film.TYPE_COLOR_NEGATIVE, "400D"),
        FilmPreset("Cinestill", "BwXX", 250, Film.TYPE_BLACK_AND_WHITE, "XX"),

        // Foma
        FilmPreset("Foma", "Fomapan 100 Classic", 100, Film.TYPE_BLACK_AND_WHITE, "FOM100"),
        FilmPreset("Foma", "Fomapan 200 Creative", 200, Film.TYPE_BLACK_AND_WHITE, "FOM200"),
        FilmPreset("Foma", "Fomapan 400 Action", 400, Film.TYPE_BLACK_AND_WHITE, "FOM400"),

        // Lomography
        FilmPreset("Lomography", "Color Negative 100", 100, Film.TYPE_COLOR_NEGATIVE, "L100"),
        FilmPreset("Lomography", "Color Negative 400", 400, Film.TYPE_COLOR_NEGATIVE, "L400"),
        FilmPreset("Lomography", "Color Negative 800", 800, Film.TYPE_COLOR_NEGATIVE, "L800"),
        FilmPreset("Lomography", "LomoChrome Metropolis", 200, Film.TYPE_COLOR_NEGATIVE, "METRO"),
        FilmPreset("Lomography", "LomoChrome Purple", 400, Film.TYPE_COLOR_NEGATIVE, "PURP"),

        // AgfaPhoto
        FilmPreset("AgfaPhoto", "APX 100 Professional", 100, Film.TYPE_BLACK_AND_WHITE, "APX100"),
        FilmPreset("AgfaPhoto", "APX 400 Professional", 400, Film.TYPE_BLACK_AND_WHITE, "APX400"),
        FilmPreset("AgfaPhoto", "Vista 200", 200, Film.TYPE_COLOR_NEGATIVE, "VIST"),

        // Rollei
        FilmPreset("Rollei", "Retro 80s", 80, Film.TYPE_BLACK_AND_WHITE, "R80S"),
        FilmPreset("Rollei", "Retro 400s", 400, Film.TYPE_BLACK_AND_WHITE, "R400S"),
        FilmPreset("Rollei", "RPX 100", 100, Film.TYPE_BLACK_AND_WHITE, "RPX100"),
        FilmPreset("Rollei", "RPX 400", 400, Film.TYPE_BLACK_AND_WHITE, "RPX400"),
        FilmPreset("Rollei", "Superpan 200", 200, Film.TYPE_BLACK_AND_WHITE, "SPAN"),

        // Bergger
        FilmPreset("Bergger", "Pancro 400", 400, Film.TYPE_BLACK_AND_WHITE, "PAN400"),

        // Harman
        FilmPreset("Harman", "Phoenix 200", 200, Film.TYPE_COLOR_NEGATIVE, "PHX"),
        FilmPreset("Harman", "Kentmere Pan 100", 100, Film.TYPE_BLACK_AND_WHITE, "KP100"),
        FilmPreset("Harman", "Kentmere Pan 400", 400, Film.TYPE_BLACK_AND_WHITE, "KP400")
    )

    val manufacturers = presets.map { it.manufacturer }.distinct().sorted()
    
    fun getFilmsForManufacturer(manufacturer: String) = presets.filter { it.manufacturer == manufacturer }
}

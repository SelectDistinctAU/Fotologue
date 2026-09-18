package com.fotologue.core.data.lab

/**
 * Pre-populated Pick Lists for Lab Equipment, Chemistry, Paper Stocks, and Suppliers.
 * Provides curated, standard defaults for rapid data entry (e.g., Intrepid, Ilford, Kodak, Paterson, Rewind Photo Lab, Digital Camera Warehouse).
 */
object LabPresets {

    // --- Equipment Pick List ---
    val equipmentPresets = listOf(
        // Enlargers
        LabEquipment(name = "Intrepid Compact Enlarger (35mm / 120)", category = "Enlarger", notes = "LED Light Source with Color/Multigrade Filters"),
        LabEquipment(name = "Beseler 23C II", category = "Enlarger", notes = "Dichro / Condenser Enlarger"),
        LabEquipment(name = "Omega D2 4x5", category = "Enlarger", notes = "Large Format Condenser Enlarger"),
        LabEquipment(name = "Durst M605", category = "Enlarger", notes = "Medium Format Enlarger"),
        LabEquipment(name = "Kaiser VP 6000", category = "Enlarger", notes = "B&W / Color Enlarger"),
        LabEquipment(name = "Leitz Focomat V35", category = "Enlarger", notes = "Autofocus 35mm Enlarger"),

        // Enlarger Lenses
        LabEquipment(name = "Rodenstock Rodagon 50mm f/2.8", category = "Enlarger Lens", notes = "35mm Format Enlarger Lens"),
        LabEquipment(name = "Rodenstock Rodagon 80mm f/4", category = "Enlarger Lens", notes = "120 Medium Format Enlarger Lens"),
        LabEquipment(name = "Schneider Componon-S 50mm f/2.8", category = "Enlarger Lens", notes = "35mm Format Premium Lens"),
        LabEquipment(name = "Schneider Componon-S 80mm f/4", category = "Enlarger Lens", notes = "120 Format Premium Lens"),
        LabEquipment(name = "El-Nikkor 50mm f/2.8 N", category = "Enlarger Lens", notes = "Nikkor 35mm Enlarger Lens"),
        LabEquipment(name = "El-Nikkor 80mm f/5.6 N", category = "Enlarger Lens", notes = "Nikkor 120 Enlarger Lens"),

        // Development Tanks & Reels
        LabEquipment(name = "Paterson Super System 4 (2-Roll Tank)", category = "Dev Tank", notes = "Standard Plastic Tank for 2x35mm or 1x120"),
        LabEquipment(name = "Paterson Super System 4 Multi-Reel (5-Roll)", category = "Dev Tank", notes = "Multi-roll developing tank"),
        LabEquipment(name = "AP Compact Developing Tank", category = "Dev Tank", notes = "Auto-load spiral system"),
        LabEquipment(name = "Jobo 1520 Tank System", category = "Dev Tank", notes = "Rotary & Inversion Tank"),
        LabEquipment(name = "Kindermann Stainless Steel Tank & Reel", category = "Dev Tank", notes = "Single 35mm SS Tank")
    )

    // --- Chemical Pick List ---
    val chemicalPresets = listOf(
        // Film Developers
        LabChemical(name = "Ilford ID-11", type = "Film Developer", dilution = "1+1", capacityRollsTotal = 10, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Ilford Microphen", type = "Film Developer", dilution = "1+1", capacityRollsTotal = 10, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Ilford Perceptol", type = "Film Developer", dilution = "1+1", capacityRollsTotal = 10, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Ilford Ilfotec DD-X", type = "Film Developer", dilution = "1+4", capacityRollsTotal = 10, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Kodak D-76", type = "Film Developer", dilution = "1+1", capacityRollsTotal = 12, volumeAmount = 3.8f, volumeUnit = "L", supplier = "Vanbar Photos"),
        LabChemical(name = "Kodak HC-110", type = "Film Developer", dilution = "1+31 (Dilution B)", capacityRollsTotal = 20, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Vanbar Photos"),
        LabChemical(name = "Rodinal / R09 One Shot", type = "Film Developer", dilution = "1+50", capacityRollsTotal = 50, volumeAmount = 500.0f, volumeUnit = "mL", supplier = "Ikigai Camera"),
        LabChemical(name = "CineStill C-41 Liquid Kit", type = "Film Developer", dilution = "Stock", capacityRollsTotal = 8, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Walkens House of Film"),

        // Paper Developers
        LabChemical(name = "Ilford Multigrade Developer", type = "Paper Developer", dilution = "1+9", capacityRollsTotal = 50, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Ilford Bromophen", type = "Paper Developer", dilution = "1+1", capacityRollsTotal = 40, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Kodak Dektol", type = "Paper Developer", dilution = "1+2", capacityRollsTotal = 50, volumeAmount = 3.8f, volumeUnit = "L", supplier = "Vanbar Photos"),
        LabChemical(name = "Adox Neutol Eco", type = "Paper Developer", dilution = "1+9", capacityRollsTotal = 50, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Ikigai Camera"),

        // Stop Bath & Fixers
        LabChemical(name = "Ilford Rapid Fixer", type = "Fixer", dilution = "1+4 (Film) / 1+9 (Paper)", capacityRollsTotal = 24, volumeAmount = 1.0f, volumeUnit = "L", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Ilford Ilfostop (Indicator)", type = "Stop Bath", dilution = "1+19", capacityRollsTotal = 60, volumeAmount = 500.0f, volumeUnit = "mL", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Kodak Indicator Stop Bath", type = "Stop Bath", dilution = "1+63", capacityRollsTotal = 60, volumeAmount = 473.0f, volumeUnit = "mL", supplier = "Vanbar Photos"),
        LabChemical(name = "Kodak Hypo Clearing Agent (HCA)", type = "Archival Wash", dilution = "Working Solution", capacityRollsTotal = 30, volumeAmount = 3.8f, volumeUnit = "L", supplier = "Vanbar Photos"),
        LabChemical(name = "Ilford Ilfotol Wetting Agent", type = "Wetting Agent", dilution = "1+200", capacityRollsTotal = 100, volumeAmount = 100.0f, volumeUnit = "mL", supplier = "Rewind Photo Lab"),
        LabChemical(name = "Kodak Photo-Flo 200", type = "Wetting Agent", dilution = "1+200", capacityRollsTotal = 100, volumeAmount = 473.0f, volumeUnit = "mL", supplier = "Vanbar Photos")
    )

    // --- Supplier Pick List ---
    data class SupplierPreset(
        val name: String,
        val region: String,
        val website: String
    )

    val supplierPresets = listOf(
        SupplierPreset("Rewind Photo Lab", "Australia", "https://rewindphotolab.com.au"),
        SupplierPreset("Vanbar Photos", "Australia", "https://www.vanbar.com.au"),
        SupplierPreset("Ikigai Camera", "Australia", "https://ikigaicamera.com.au"),
        SupplierPreset("Walkens House of Film", "Australia", "https://walkens.com.au"),
        SupplierPreset("Digital Camera Warehouse", "Australia", "https://www.digitalcamerawarehouse.com.au"),
        SupplierPreset("Camera Electronic", "Australia", "https://www.cameraelectronic.com.au"),
        SupplierPreset("CRK Photo Imaging", "Australia", "https://crkphotoimaging.com.au"),
        SupplierPreset("Camera House", "Australia", "https://www.camerahouse.com.au"),
        SupplierPreset("Georges Cameras", "Australia", "https://www.georges.com.au"),
        SupplierPreset("Bellerive Photo", "Australia", "https://bellerivephoto.com.au"),
        SupplierPreset("Decisive Moment", "Australia", "https://decisivemoment.com.au"),
        SupplierPreset("B&H Photo Video", "USA / Global", "https://www.bhphotovideo.com"),
        SupplierPreset("Freestyle Photographic", "USA / Global", "https://www.freestylephoto.com")
    )

    // --- Darkroom Paper Stock Presets ---
    data class PaperPreset(
        val brand: String,
        val name: String,
        val baseType: String, // FB (Fiber Base) or RC (Resin Coated)
        val finish: String, // Glossy, Pearl, Matt, Semi-Matte
        val tone: String = "Neutral" // Warmtone, Cooltone, Neutral
    ) {
        val displayName: String get() = "$brand $name ($baseType $finish)"
    }

    val paperPresets = listOf(
        // Ilford
        PaperPreset("Ilford", "Multigrade FB Glossy", "FB", "Glossy", "Neutral"),
        PaperPreset("Ilford", "Multigrade FB Matt", "FB", "Matt", "Neutral"),
        PaperPreset("Ilford", "Multigrade FB Warmtone Glossy", "FB", "Glossy", "Warmtone"),
        PaperPreset("Ilford", "Multigrade FB Cooltone Glossy", "FB", "Glossy", "Cooltone"),
        PaperPreset("Ilford", "Multigrade RC Deluxe Glossy", "RC", "Glossy", "Neutral"),
        PaperPreset("Ilford", "Multigrade RC Deluxe Pearl", "RC", "Pearl", "Neutral"),
        PaperPreset("Ilford", "Multigrade RC Deluxe Satins", "RC", "Satin", "Neutral"),
        PaperPreset("Ilford", "Multigrade RC Portfolio Glossy", "RC", "Glossy", "Neutral"),

        // Foma
        PaperPreset("Foma", "Fomabrom Variant IV 123 FB", "FB", "Glossy", "Neutral"),
        PaperPreset("Foma", "Fomabrom Variant IV 122 FB", "FB", "Matt", "Neutral"),
        PaperPreset("Foma", "Fomaspeed Variant III 311 RC", "RC", "Glossy", "Neutral"),
        PaperPreset("Foma", "Fomaspeed Variant III 312 RC", "RC", "Matt", "Neutral"),
        PaperPreset("Foma", "Foma Retrobrom 151 FB", "FB", "Glossy", "Warmtone"),

        // Adox
        PaperPreset("Adox", "MCC 110 FB Glossy", "FB", "Glossy", "Neutral"),
        PaperPreset("Adox", "MCP 310 RC Glossy", "RC", "Glossy", "Neutral"),
        PaperPreset("Adox", "Lupex Contact Paper", "FB", "Glossy", "Warmtone")
    )
}

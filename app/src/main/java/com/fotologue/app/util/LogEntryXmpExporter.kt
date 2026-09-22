package com.fotologue.app.util

import com.fotologue.core.data.LogEntryWithGear
import com.fotologue.core.data.Tag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object LogEntryXmpExporter {
    
    fun toXmp(
        entry: LogEntryWithGear,
        allTags: List<Tag>,
        creator: String = "",
        copyright: String = "",
        contact: String = ""
    ): String {
        val log = entry.logEntry
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        val dateString = dateFormat.format(Date(log.timestamp))

        val apertureValue = log.exposureSettings.aperture.replace("f/", "").toDoubleOrNull() ?: 0.0
        val shutterSpeedValue = parseShutterSpeed(log.exposureSettings.shutterSpeed)
        
        val gearString = buildString {
            append(entry.camera?.name ?: "Unknown Camera")
            entry.lens?.let { append(" + ${it.make} ${it.model}") }
            entry.teleconverter?.let { append(" + ${it.name}") }
            entry.filter?.let { append(" + ${it.name}") }
            entry.flash?.let { append(" + ${it.make} ${it.model}") }
        }

        val tagMap = allTags.associateBy { it.id }
        val lat = log.latitude
        val lon = log.longitude

        return """
            |<?xpacket begin="" id="W5M0MpCehiHzreSzNTczkc9d"?>
            |<x:xmpmeta xmlns:x="adobe:ns:meta/" x:xmptk="Adobe XMP Core 5.6-c140 79.160451, 2017/05/06-01:08:21        ">
            | <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
            |  <rdf:Description rdf:about=""
            |    xmlns:xmp="http://ns.adobe.com/xap/1.0/"
            |    xmlns:dc="http://purl.org/dc/elements/1.1/"
            |    xmlns:photoshop="http://ns.adobe.com/photoshop/1.0/"
            |    xmlns:exif="http://ns.adobe.com/exif/1.0/"
            |    xmlns:tiff="http://ns.adobe.com/tiff/1.0/"
            |    xmlns:aux="http://ns.adobe.com/exif/1.0/aux/"
            |    xmlns:lr="http://ns.adobe.com/lightroom/1.0/"
            |    xmlns:xmpRights="http://ns.adobe.com/xap/1.0/rights/"
            |    xmlns:Iptc4xmpCore="http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/"
            |    xmp:CreateDate="$dateString"
            |    xmp:ModifyDate="$dateString"
            |    photoshop:Instructions="${escapeXml(log.environmentalConditions)}"
            |    tiff:Make="${escapeXml(entry.camera?.name ?: "Unknown")}"
            |    exif:FNumber="$apertureValue"
            |    exif:ExposureTime="$shutterSpeedValue"
            |    exif:ISOSpeedRatings="${log.exposureSettings.iso}"
            |    exif:FocalLength="${log.focalLength ?: 0.0}"
            |    aux:Lens="${escapeXml(entry.lens?.let { "${it.make} ${it.model}" } ?: "")}"
            |    dc:format="image/jpeg"
            |    xmpRights:Marked="True">
            |   <dc:creator>
            |    <rdf:Seq>
            |     <rdf:li>${escapeXml(creator)}</rdf:li>
            |    </rdf:Seq>
            |   </dc:creator>
            |   <dc:description>
            |    <rdf:Alt>
            |     <rdf:li xml:lang="x-default">${escapeXml(log.notes)}</rdf:li>
            |    </rdf:Alt>
            |   </dc:description>
            |   <dc:rights>
            |    <rdf:Alt>
            |     <rdf:li xml:lang="x-default">${escapeXml(copyright)}</rdf:li>
            |    </rdf:Alt>
            |   </dc:rights>
            |   <dc:subject>
            |    <rdf:Bag>
            |     ${entry.tags.joinToString("\n     ") { "<rdf:li>${escapeXml(it.name)}</rdf:li>" }}
            |     <rdf:li>Fotologue</rdf:li>
            |     <rdf:li>${escapeXml(entry.film?.name ?: "Film")}</rdf:li>
            |    </rdf:Bag>
            |   </dc:subject>
            |   <lr:hierarchicalSubject>
            |    <rdf:Bag>
            |     ${entry.tags.joinToString("\n     ") { "<rdf:li>${escapeXml(getHierarchyPath(it, tagMap))}</rdf:li>" }}
            |     <rdf:li>App|Fotologue</rdf:li>
            |     <rdf:li>Film|${escapeXml(entry.film?.name ?: "Film")}</rdf:li>
            |    </rdf:Bag>
            |   </lr:hierarchicalSubject>
            |   <Iptc4xmpCore:CreatorContactInfo
            |    Iptc4xmpCore:CiEmailOut="$contact"
            |    Iptc4xmpCore:CiUrlWork="$contact" />
            |   ${if (lat != null && lon != null) """
            |   <exif:GPSLatitude>${formatGpsCoordinate(lat, true)}</exif:GPSLatitude>
            |   <exif:GPSLongitude>${formatGpsCoordinate(lon, false)}</exif:GPSLongitude>
            |   <exif:GPSLatitudeRef>${if (lat >= 0) "N" else "S"}</exif:GPSLatitudeRef>
            |   <exif:GPSLongitudeRef>${if (lon >= 0) "E" else "W"}</exif:GPSLongitudeRef>
            |   """.trimMargin() else ""}
            |   <photoshop:Headline>${escapeXml(gearString)}</photoshop:Headline>
            |  </rdf:Description>
            | </rdf:RDF>
            |</x:xmpmeta>
            |<?xpacket end="w"?>
        """.trimMargin()
    }

    private fun getHierarchyPath(tag: Tag, tagMap: Map<Long, Tag>): String {
        val path = mutableListOf<String>()
        var current: Tag? = tag
        while (current != null) {
            path.add(0, current.name)
            if (current.parentId == null) {
                path.add(0, current.category)
            }
            current = current.parentId?.let { tagMap[it] }
        }
        return path.joinToString("|")
    }

    private fun parseShutterSpeed(speed: String): String {
        if (speed.contains("/")) {
            val parts = speed.split("/")
            if (parts.size == 2) {
                val numerator = parts[0].toDoubleOrNull() ?: 1.0
                val denominator = parts[1].toDoubleOrNull() ?: 1.0
                return (numerator / denominator).toString()
            }
        }
        return speed.toDoubleOrNull()?.toString() ?: "0.0"
    }

    private fun formatGpsCoordinate(value: Double, isLatitude: Boolean): String {
        val absVal = Math.abs(value)
        val degrees = absVal.toInt()
        val minutesDouble = (absVal - degrees) * 60.0
        val minutes = minutesDouble.toInt()
        val seconds = (minutesDouble - minutes) * 60.0
        val ref = if (isLatitude) {
            if (value >= 0) "N" else "S"
        } else {
            if (value >= 0) "E" else "W"
        }
        return String.format(Locale.US, "%d,%d,%.2f%s", degrees, minutes, seconds, ref)
    }

    private fun escapeXml(value: String): String {
        return value.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}

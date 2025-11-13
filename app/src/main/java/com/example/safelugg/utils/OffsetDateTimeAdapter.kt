import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OffsetDateTimeAdapter : TypeAdapter<OffsetDateTime>() {
    override fun write(out: JsonWriter, value: OffsetDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.beginObject()
            out.name("year").value(value.year)
            out.name("monthValue").value(value.monthValue)
            out.name("dayOfMonth").value(value.dayOfMonth)
            out.name("hour").value(value.hour)
            out.name("minute").value(value.minute)
            out.name("second").value(value.second)
            out.endObject()
        }
    }

    override fun read(reader: JsonReader): OffsetDateTime? {
        var year = 0
        var month = 1
        var day = 1
        var hour = 0
        var minute = 0
        var second = 0

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "year" -> year = reader.nextInt()
                "monthValue" -> month = reader.nextInt()
                "dayOfMonth" -> day = reader.nextInt()
                "hour" -> hour = reader.nextInt()
                "minute" -> minute = reader.nextInt()
                "second" -> second = reader.nextInt()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return OffsetDateTime.of(year, month, day, hour, minute, second, 0, ZoneOffset.UTC)
    }
}

package org.shunya.server.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateSerializer extends JsonSerializer<Date> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy hh:mm a");

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (date != null)
            jsonGenerator.writeString(formatter.format(date));
        else
            jsonGenerator.writeString("NA");
    }
}

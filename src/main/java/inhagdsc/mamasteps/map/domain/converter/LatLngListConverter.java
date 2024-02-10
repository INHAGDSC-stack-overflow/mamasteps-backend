package inhagdsc.mamasteps.map.domain.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import inhagdsc.mamasteps.map.domain.LatLng;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Converter(autoApply = true)
public class LatLngListConverter implements AttributeConverter<List<LatLng>, String> {
    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(List<LatLng> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return gson.toJson(attribute);
    }

    @Override
    public List<LatLng> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<LatLng>>(){}.getType();
        return gson.fromJson(dbData, listType);
    }
}
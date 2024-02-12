package inhagdsc.mamasteps.map.domain.converter;

import com.google.gson.Gson;
import inhagdsc.mamasteps.map.domain.LatLng;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LatLngConverter implements AttributeConverter<LatLng, String> {
    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(LatLng attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public LatLng convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return gson.fromJson(dbData, LatLng.class);
    }
}
package inhagdsc.mamasteps.common.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String timeStr = p.getText().trim();
        // 정규 표현식을 사용하여 한 자리 또는 두 자리 숫자를 확인
        if (timeStr.matches("\\d{1,2}")) {
            // 한 자리 또는 두 자리 숫자인 경우, 시간으로 해석하고 분은 0으로 설정
            int hour = Integer.parseInt(timeStr);
            // 유효한 시간 범위인 0~23 사이인지 확인
            if (hour >= 0 && hour <= 23) {
                LocalTime result =  LocalTime.of(hour, 0);
                return result;
            } else {
                throw new IOException("Invalid hour input: " + timeStr);
            }
        } else {
            // "HH:mm" 또는 "HH:mm:ss" 형식이 아닌 경우 예외 처리
            throw new IOException("Invalid time format: " + timeStr);
        }
    }
}
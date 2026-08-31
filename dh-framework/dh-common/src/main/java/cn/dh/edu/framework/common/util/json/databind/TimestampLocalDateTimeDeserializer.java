package cn.dh.edu.framework.common.util.json.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 基于时间戳的 LocalDateTime 反序列化器
 * 支持：Long 时间戳、@JsonFormat 指定格式的字符串
 *
 * @author 老五
 */
@JacksonStdImpl
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime>
        implements com.fasterxml.jackson.databind.deser.ContextualDeserializer {

    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer(null);

    private final DateTimeFormatter formatter;

    public TimestampLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // 如果是字符串且存在格式化器，优先按格式化器解析
        if (p.currentToken() == JsonToken.VALUE_STRING && formatter != null) {
            String text = p.getText().trim();
            if (text.isEmpty()) {
                return null;
            }
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (Exception e) {
                // 仅日期 pattern（如 yyyy-MM-dd）无法直接 parse 为 LocalDateTime，按当天 00:00:00
                try {
                    return java.time.LocalDate.parse(text, formatter).atStartOfDay();
                } catch (Exception ignored) {
                    // 解析失败，降级为时间戳处理
                }
            }
        }

        // 默认：将 Long 时间戳，转换为 LocalDateTime 对象
        long timestamp = p.getValueAsLong();
        if (timestamp == 0 && p.currentToken() == JsonToken.VALUE_STRING) {
            String text = p.getText().trim();
            if (text.isEmpty()) {
                return null;
            }
            // 尝试用默认格式解析
            try {
                return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception ignored) {
            }
            try {
                return java.time.LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            } catch (Exception ignored) {
            }
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            JsonFormat format = property.getAnnotation(JsonFormat.class);
            if (format == null) {
                format = property.getContextAnnotation(JsonFormat.class);
            }
            if (format != null && !format.pattern().isEmpty()) {
                return new TimestampLocalDateTimeDeserializer(DateTimeFormatter.ofPattern(format.pattern()));
            }
        }
        return this;
    }

}

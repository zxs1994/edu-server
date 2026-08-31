package cn.dh.edu.framework.common.util.json.databind;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LocalDate 序列化器：默认输出 ISO 字符串（如 "2026-06-11"），
 * 支持通过 @JsonFormat(pattern=...) 自定义格式。
 * <p>
 * 解决全局 write-dates-as-timestamps=true 导致 LocalDate 输出为数组 [2026,6,11] 的问题。
 *
 * @author QoderWork
 */
@Slf4j
public class LocalDateIsoSerializer extends JsonSerializer<LocalDate> {

    public static final LocalDateIsoSerializer INSTANCE = new LocalDateIsoSerializer();

    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String fieldName = gen.getOutputContext().getCurrentName();
        if (fieldName != null) {
            Object currentValue = gen.getOutputContext().getCurrentValue();
            if (currentValue != null) {
                Class<?> clazz = currentValue.getClass();
                Map<String, Field> fieldMap = FIELD_CACHE.computeIfAbsent(clazz, this::buildFieldMap);
                Field field = fieldMap.get(fieldName);
                if (field != null && field.isAnnotationPresent(JsonFormat.class)) {
                    JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(jsonFormat.pattern());
                        gen.writeString(formatter.format(value));
                        return;
                    } catch (Exception ex) {
                        log.warn("[serialize][({}#{}) 使用 JsonFormat pattern 失败，降级为 ISO 格式]",
                                clazz.getName(), fieldName, ex);
                    }
                }
            }
        }

        // 默认输出 ISO 格式字符串，如 "2026-06-11"
        gen.writeString(value.toString());
    }

    private Map<String, Field> buildFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : ReflectUtil.getFields(clazz)) {
            String fieldName = field.getName();
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null) {
                String value = jsonProperty.value();
                if (StrUtil.isNotEmpty(value) && ObjUtil.notEqual("\u0000", value)) {
                    fieldName = value;
                }
            }
            fieldMap.put(fieldName, field);
        }
        return fieldMap;
    }

}

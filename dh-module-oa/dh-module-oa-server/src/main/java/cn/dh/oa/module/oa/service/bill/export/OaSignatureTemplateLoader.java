package cn.dh.oa.module.oa.service.bill.export;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * 从 classpath:signature-templates/ 加载人员签名图
 */
@Component
public class OaSignatureTemplateLoader {

    private static final String DIR = "signature-templates/";

    public Optional<LoadedSignature> loadByNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return Optional.empty();
        }
        for (String ext : new String[]{".jpg", ".jpeg", ".png"}) {
            ClassPathResource resource = new ClassPathResource(DIR + nickname + ext);
            if (!resource.exists()) {
                continue;
            }
            try (InputStream in = resource.getInputStream()) {
                byte[] data = in.readAllBytes();
                int pictureType = ".png".equals(ext)
                        ? org.apache.poi.ss.usermodel.Workbook.PICTURE_TYPE_PNG
                        : org.apache.poi.ss.usermodel.Workbook.PICTURE_TYPE_JPEG;
                return Optional.of(new LoadedSignature(data, pictureType));
            } catch (IOException ignored) {
                // 尝试下一个扩展名
            }
        }
        return Optional.empty();
    }

    public record LoadedSignature(byte[] data, int pictureType) {
    }

}

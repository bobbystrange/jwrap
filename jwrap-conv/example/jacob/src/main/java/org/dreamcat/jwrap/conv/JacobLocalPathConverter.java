package org.dreamcat.jwrap.conv;

import java.io.File;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.io.FileUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Create by tuke on 2019-03-31
 */
@Slf4j
@RequiredArgsConstructor
public class JacobLocalPathConverter {

    private final BiConsumer<String, String> converter;

    public ResponseEntity<byte[]> convert(
            MultipartFile file,
            String localSourcePath,
            String localTargetPath) {
        String inputFile = localSourcePath + "/" + file.getOriginalFilename();
        String pdfFile = localTargetPath + "/" + file.getOriginalFilename() + ".pdf";
        try {
            log.info("transfering {} to {}", file.getOriginalFilename(), inputFile);
            file.transferTo(new File(inputFile));
            converter.accept(inputFile, pdfFile);
            byte[] data = FileUtil.readAsByteArray(pdfFile);

            if (!new File(inputFile).delete()) {
                log.error("failed to delete file {}", inputFile);
            }
            if (!new File(pdfFile).delete()) {
                log.error("failed to delete file {}", pdfFile);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            String message = e.getMessage();
            log.error(message, e);
            if (message == null) message = HttpStatus.INTERNAL_SERVER_ERROR.toString();
            return new ResponseEntity<>(message.getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

package org.dreamcat.jwrap.conv;

import javax.annotation.Resource;
import org.dreamcat.jwrap.conv.jacob.JacobMSOffice2007Converter;
import org.dreamcat.jwrap.conv.jacob.JacobMSOffice2010Converter;
import org.dreamcat.jwrap.conv.jacob.JacobWps2015Converter;
import org.dreamcat.jwrap.conv.jacob.JacobWpsConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Create by tuke on 2019-03-31
 */
@RestController
public class JacobController {

    @Resource
    private JacobApplication config;

    @Resource
    private JacobMSOffice2010Converter jacobMSOffice2010Converter;

    @Resource
    private JacobMSOffice2007Converter jacobMSOffice2007Converter;

    @Resource
    private JacobWpsConverter jacobWpsConverter;

    @Resource
    private JacobWps2015Converter jacobWps2015Converter;

    @RequestMapping(value = {"/msoffice/convert"}, method = RequestMethod.POST)
    public ResponseEntity<byte[]> convertMSOffice(@RequestParam MultipartFile file) {
        String localSourcePath = config.getLocalSourcePath();
        String localTargetPath = config.getLocalTargetPath();

        return new JacobLocalPathConverter(jacobMSOffice2010Converter::office2Pdf)
                .convert(file, localSourcePath, localTargetPath);
    }

    @RequestMapping(value = {"/msoffice2007/convert"}, method = RequestMethod.POST)
    public ResponseEntity<byte[]> convertMSOffice2007(@RequestParam MultipartFile file) {
        String localSourcePath = config.getLocalSourcePath();
        String localTargetPath = config.getLocalTargetPath();

        return new JacobLocalPathConverter(jacobMSOffice2007Converter::office2Pdf)
                .convert(file, localSourcePath, localTargetPath);
    }

    @RequestMapping(value = {"/wps/convert"}, method = RequestMethod.POST)
    public ResponseEntity<byte[]> convertWps(@RequestParam MultipartFile file) {
        String localSourcePath = config.getLocalSourcePath();
        String localTargetPath = config.getLocalTargetPath();

        return new JacobLocalPathConverter(jacobWpsConverter::office2Pdf)
                .convert(file, localSourcePath, localTargetPath);
    }

    @RequestMapping(value = {"/wps2015/convert"}, method = RequestMethod.POST)
    public ResponseEntity<byte[]> convertWps2015(@RequestParam MultipartFile file) {
        String localSourcePath = config.getLocalSourcePath();
        String localTargetPath = config.getLocalTargetPath();

        return new JacobLocalPathConverter(jacobWps2015Converter::office2Pdf)
                .convert(file, localSourcePath, localTargetPath);
    }

}

package org.dreamcat.jwrap.conv;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.jwrap.conv.jacob.JacobMSOffice2007Converter;
import org.dreamcat.jwrap.conv.jacob.JacobMSOffice2010Converter;
import org.dreamcat.jwrap.conv.jacob.JacobWps2015Converter;
import org.dreamcat.jwrap.conv.jacob.JacobWpsConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Create by tuke on 2019-03-31
 */
@Getter
@Setter
@SpringBootApplication
public class JacobApplication {

    @Value("${storage.local.source:}")
    private String localSourcePath;

    @Value("${storage.local.target:}")
    private String localTargetPath;

    public static void main(String[] args) {
        SpringApplication.run(JacobApplication.class);
    }

    @Bean
    public JacobMSOffice2010Converter jacobMSOffice2010Converter() {
        return new JacobMSOffice2010Converter();
    }

    @Bean
    public JacobMSOffice2007Converter jacobMSOffice2007Converter() {
        return new JacobMSOffice2007Converter();
    }

    @Bean
    public JacobWpsConverter jacobWpsConverter() {
        return new JacobWpsConverter();
    }

    @Bean
    public JacobWps2015Converter jacobWps2015Converter() {
        return new JacobWps2015Converter();
    }

}

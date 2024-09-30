package br.com.foxconcursos.config;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import br.com.foxconcursos.repositories.converters.TipoQuestaoReadingConverter;
import br.com.foxconcursos.repositories.converters.TipoQuestaoWritingConverter;

@Configuration
public class JdbcConfig {
    
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(
            Arrays.asList(
                new TipoQuestaoReadingConverter(),
                new TipoQuestaoWritingConverter()
            )
        );
    }

}

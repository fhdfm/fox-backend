package br.com.foxconcursos.repositories.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import br.com.foxconcursos.domain.TipoQuestao;

@ReadingConverter
@Component
public class TipoQuestaoReadingConverter implements Converter<Integer, TipoQuestao> {

    @Override
    public TipoQuestao convert(Integer source) {
        return TipoQuestao.values()[source];
    }
    
}

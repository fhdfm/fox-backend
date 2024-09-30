package br.com.foxconcursos.repositories.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import br.com.foxconcursos.domain.TipoQuestao;

@WritingConverter
@Component
public class TipoQuestaoWritingConverter implements Converter<TipoQuestao, Integer> {

    @Override
    public Integer convert(TipoQuestao source) {
        return source.getCodigo();
    }
    
}

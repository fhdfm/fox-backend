package br.com.foxconcursos.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.ExampleMatcher;

public class FoxUtils {
    
    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date convertLocalDateToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static <T> T criarObjetoDinamico(String query, Class<T> clazz) throws Exception {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query não pode ser nula ou vazia");
        }

        String[] pares = query.split(",");
        T objeto = getNovaInstancia(clazz);

        // Usar um mapa para armazenar as listas de valores
        Map<String, List<String>> listaDeValores = new HashMap<>();

        for (String par : pares) {
            String[] chaveValor = par.split(":");
            if (chaveValor.length != 2) {
                throw new IllegalArgumentException("Parâmetro inválido: " + par);
            }

            String chave = chaveValor[0].trim();
            String valor = chaveValor[1].trim();

            // Adicionar o valor à lista correspondente no mapa
            listaDeValores.computeIfAbsent(chave, k -> new ArrayList<>()).add(valor);
        }

        for (Map.Entry<String, List<String>> entry : listaDeValores.entrySet()) {
            String chave = entry.getKey();
            List<String> valores = entry.getValue();

            Field field = clazz.getDeclaredField(chave);
            field.setAccessible(true);

            if (field.getType().isEnum()) {
                String valor = valores.get(0); // Assumindo que a enum tem um único valor
                Object[] enums = field.getType().getEnumConstants();
                Object enumValue = null;
                for (Object e : enums) {
                    if (((Enum<?>) e).name().equalsIgnoreCase(valor)) {
                        enumValue = e;
                        break;
                    }
                }

                if (enumValue == null) {
                    throw new IllegalArgumentException("Valor inválido para o enum: " + valor);
                }
                field.set(objeto, enumValue);
            } else if (field.getType() == List.class) {
                // Converta a lista de strings para a lista correta
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

                if (listClass == UUID.class) {
                    List<UUID> uuidList = valores.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
                    field.set(objeto, uuidList);
                } else {
                    field.set(objeto, valores);
                }
            } else if (field.getType() == UUID.class) {
                String valor = valores.get(0); // Assumindo que o campo UUID tem um único valor
                field.set(objeto, UUID.fromString(valor));
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                String valor = valores.get(0);
                field.set(objeto, Boolean.parseBoolean(valor));
            } else {
                String valor = valores.get(0); // Assumindo que campos simples têm um único valor
                field.set(objeto, valor);
            }
        }

        return objeto;
    }

    public static <T> ExampleMatcher createExampleMatcher(Class<T> clazz) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues(); // Ignorar valores nulos

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                matcher = matcher.withMatcher(field.getName(), 
                    ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
            }
        }

        return matcher;
    }    

    private static <T> T getNovaInstancia(Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmpty(Object value) {
        return value == null;
    }

    public static String validarCpf(String cpf) {
        Cpf cpfUtil = new Cpf();
        return cpfUtil.parse(cpf);
    }

    public static String formatarCpf(String cpf) {
        Cpf cpfUtil = new Cpf();
        return cpfUtil.format(cpf);
    }

    public static String removeInvalidXMLCharacters(String input) {
        StringBuilder out = new StringBuilder();
        int codePoint;
        int i = 0;
        while (i < input.length()) {
            codePoint = input.codePointAt(i);
            if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) ||
                    ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                    ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                    ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
                out.append(Character.toChars(codePoint));
            }
            i += Character.charCount(codePoint);
        }
        return out.toString();
    }
    public static String removerTagsP(String texto) {
        texto = texto.replaceAll("^<p[^>]*?>", "");
        texto = texto.replaceAll("</p>$", "");
        return texto;
    }

    public static char obterLetra(int numero) {
        char[] letras = {'A', 'B', 'C', 'D', 'E'};
        if (numero >= 1 && numero <= letras.length) {
            return letras[numero - 1];
        } else {
            throw new IllegalArgumentException("Número fora do intervalo válido.");
        }
    }

    public static LocalDateTime calcularHoraFimSimulado(LocalDateTime dataInicio, String duracao) {
        String[] duracaoSplit = duracao.split(":");
        int horas = Integer.parseInt(duracaoSplit[0]);
        int minutos = Integer.parseInt(duracaoSplit[1]);
        dataInicio = dataInicio.plusHours(horas);
        dataInicio = dataInicio.plusMinutes(minutos);
        dataInicio = dataInicio.plusSeconds(59);
        return dataInicio;
    }

}

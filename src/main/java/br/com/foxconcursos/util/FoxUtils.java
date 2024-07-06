package br.com.foxconcursos.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
        if (query == null || query.isEmpty())
            throw new IllegalArgumentException("Query não pode ser nula ou vazia");
        
        String[] pares = query.split(",");
        
        T objeto = getNovaInstancia(clazz);
        Map<String, List<String>> parametros = new HashMap<>();
        
        for (String par : pares) {
            String[] chaveValor = par.split(":");
            if (chaveValor.length != 2)
                throw new IllegalArgumentException("Parâmetro inválido: " + par);
            
            String chave = chaveValor[0].trim();
            String valor = chaveValor[1].trim();
            
            if (!parametros.containsKey(chave)) {
                parametros.put(chave, new ArrayList<>());
            }
            parametros.get(chave).add(valor);
        }
        
        for (Map.Entry<String, List<String>> entry : parametros.entrySet()) {
            String chave = entry.getKey();
            List<String> valores = entry.getValue();
            Field field = clazz.getDeclaredField(chave);
            field.setAccessible(true);

            if (field.getType().isArray() || List.class.isAssignableFrom(field.getType())) {
                Class<?> componentType = field.getType().isArray() ? field.getType().getComponentType() : String.class;
                if (field.getType().isEnum()) {
                    List<Object> enumValues = new ArrayList<>();
                    for (String valor : valores) {
                        Object[] enums = componentType.getEnumConstants();
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
                        enumValues.add(enumValue);
                    }
                    if (field.getType().isArray()) {
                        Object array = Array.newInstance(componentType, enumValues.size());
                        for (int i = 0; i < enumValues.size(); i++) {
                            Array.set(array, i, enumValues.get(i));
                        }
                        field.set(objeto, array);
                    } else {
                        field.set(objeto, enumValues);
                    }
                } else {
                    if (field.getType().isArray()) {
                        Object array = Array.newInstance(componentType, valores.size());
                        for (int i = 0; i < valores.size(); i++) {
                            Array.set(array, i, valores.get(i));
                        }
                        field.set(objeto, array);
                    } else {
                        field.set(objeto, valores);
                    }
                }
            } else {
                if (field.getType().isEnum()) {
                    Object[] enums = field.getType().getEnumConstants();
                    Object enumValue = null;
                    for (Object e : enums) {
                        if (((Enum<?>) e).name().equalsIgnoreCase(valores.get(0))) {
                            enumValue = e;
                            break;
                        }
                    }
                    if (enumValue == null) {
                        throw new IllegalArgumentException("Valor inválido para o enum: " + valores.get(0));
                    }
                    field.set(objeto, enumValue);
                } else {
                    field.set(objeto, valores.get(0));
                }
            }
        }

        return objeto;
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

    public static String removerTagsP(String texto) {
        texto = texto.replaceAll("^<p[^>]*?>", "");
        texto = texto.replaceAll("</p>$", "");
        return texto;
    }

    public static char obterLetra(int numero) {
        char[] letras = {'a', 'b', 'c', 'd', 'e'};
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

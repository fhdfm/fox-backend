package com.example.demo.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
        for (String par : pares) {
            String[] chaveValor = par.split(":");
            if (chaveValor.length != 2)
                throw new IllegalArgumentException("Parâmetro inválido: " + par);
            
            String chave = chaveValor[0].trim();
            String valor = chaveValor[1].trim();
            Field field = clazz.getDeclaredField(chave);
            field.setAccessible(true);
            field.set(objeto, valor);
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

}

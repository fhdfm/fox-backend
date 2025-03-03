package br.com.foxconcursos.util;

import br.com.foxconcursos.exception.FoxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    public enum LogType {
        INFO, WARN, ERROR, DEBUG
    }

    // Construtor privado para evitar instanciação
    private LogUtil() {}

    /**
     * Realiza log com uma mensagem simples.
     *
     * @param clazz   a classe de onde o log está sendo chamado
     * @param type    o tipo de log (INFO, WARN, ERROR, DEBUG)
     * @param message a mensagem a ser logada
     */
    public static void log(Class<?> clazz, LogType type, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        switch (type) {
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            default:
                logger.info(message);
                break;
        }
    }

    /**
     * Realiza log com mensagem e exceção.
     *
     * @param clazz     a classe de onde o log está sendo chamado
     * @param type      o tipo de log (INFO, WARN, ERROR, DEBUG)
     * @param FoxException a exceção a ser logada
     */
    public static void log(Class<?> clazz, LogType type, FoxException foxException) {
        Logger logger = LoggerFactory.getLogger(clazz);
        switch (type) {
            case WARN:
                logger.warn(foxException.getMessage(), foxException);
                break;
            case ERROR:
                logger.error(foxException.getMessage(), foxException);
                break;
            case DEBUG:
                logger.debug(foxException.getMessage(), foxException);
                break;
            default:
                logger.info(foxException.getMessage(), foxException);
                break;
        }
    }
}

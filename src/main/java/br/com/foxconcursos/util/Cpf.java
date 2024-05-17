package br.com.foxconcursos.util;

public class Cpf {

    public String format(String cpf) {
        
        if (cpf == null)
            throw new IllegalArgumentException("CPF inválido.");

        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");

    }

    public String parse(String cpf) {
        cpf = removeCaracteres(cpf);
        
        if (!possuiTamanhoCorreto(cpf) || todosOsDigitosSaoIguais(cpf))
            throw new IllegalArgumentException("CPF inválido.");
        
        int[] numeros = converteEmIntArray(cpf);
        if (!validaCheksum(numeros))
            throw new IllegalArgumentException("CPF inválido.");

        return cpf;
    }

    private String removeCaracteres(String cpf) {
        return cpf.replaceAll("\\D", "");
    }
    
    private boolean possuiTamanhoCorreto(String cpf) {
        return cpf.length() == 11;
    }

    private boolean todosOsDigitosSaoIguais(String cpf) {
        char primeiroDigito = cpf.charAt(0);
        for (int i = 1; i < 11; i++) {
            if (cpf.charAt(i) != primeiroDigito) {
                return false;
            }
        }
        return true;
    }

    private int[] converteEmIntArray(String cpf) {
        int[] numeros = new int[11];
        for (int i = 0; i < 11; i++) {
            numeros[i] = cpf.charAt(i) - '0';
        }
        return numeros;
    }

    public boolean validaCheksum(int[] numeros) {
       
        int sum = calculaSum(numeros, 9);
        int primeiroDigito = calculaDigito(sum);

        sum = calculaSum(numeros, 10);
        int segundoDigito = calculaDigito(sum);

        return primeiroDigito == numeros[9] 
            && segundoDigito == numeros[10];
    }

    private int calculaSum(int[] numeros, int limite) {
        int sum = 0;
        for (int i = 0; i < limite; i++) {
            sum += numeros[i] * (limite + 1 - i);
        }
        return sum;
    }

    private int calculaDigito(int sum) {
        int digito = 11 - (sum % 11);
        return digito >= 10 ? 0 : digito;
    }
}

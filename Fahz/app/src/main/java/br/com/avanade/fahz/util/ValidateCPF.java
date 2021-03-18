package br.com.avanade.fahz.util;

import java.util.InputMismatchException;

public class ValidateCPF {
    public static boolean isCPF(String CPF){
        char dig10, dig11;
        int sum, r, number, weight;
        if (CPF == null || CPF.length() != 11){
            return false;
        } else if( CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999")) {
            return false;
        } else {
            try {
                // calculo do 1º digito verificador
                sum = 0;
                weight = 10;
                for (int i = 0; i < 9; i++){
                    number = (CPF.charAt(i)-48);
                    sum += number * weight;
                    weight--;
                }
                r = 11 - (sum % 11);
                if ((r == 10)|| (r == 11)) {
                    dig10 = '0';
                } else {
                    dig10 = (char) (r + 48);
                }

                // calculo do 2º digito verificador
                sum = 0;
                weight = 11;
                for (int i = 0; i < 10; i++){
                    number = CPF.charAt(i)-48;
                    sum += number * weight;
                    weight--;
                }
                r = 11 - (sum % 11);
                if ((r == 10)|| (r == 11)) {
                    dig11 = '0';
                } else {
                    dig11 = (char) (r + 48);
                }

                return (dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10));
            }catch (InputMismatchException e) {
                return false;
            }
        }
    }

    public static String removeSpecialCharacters(String CPF){
        if (CPF.contains(".")) {
            CPF = CPF.replace(".", "");
        }
        if (CPF.contains("-")) {
            CPF = CPF.replace("-", "");
        }
        return CPF;
    }
}

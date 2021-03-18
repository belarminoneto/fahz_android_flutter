package br.com.avanade.fahz.util;

public class ValidateBankDataErrorException extends Exception
{
    String message;
    public ValidateBankDataErrorException() {
        super();
    }

    public ValidateBankDataErrorException(String message)
    {
        super(message);

        this.message = message;
    }
}
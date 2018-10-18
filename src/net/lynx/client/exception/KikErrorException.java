package net.lynx.client.exception;

public class KikErrorException extends Exception {
    public KikErrorException(String error){
        System.out.println(error);
    }
}

package net.lynx.client.exception;

public class KikEmptyResponseException extends Exception {
    public KikEmptyResponseException(String error){
        System.out.println(error);
    }
}

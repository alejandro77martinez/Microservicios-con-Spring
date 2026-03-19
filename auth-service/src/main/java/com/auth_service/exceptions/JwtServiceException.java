package com.auth_service.exceptions;

public class JwtServiceException extends Exception {
  
  public JwtServiceException (String message){
    super(message);
  }
}

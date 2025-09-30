package com.fiap.easyconsult.core.exception;

public class DomainException extends RuntimeException {
  private final String code;

  public DomainException(String message, String code) {
    super(message);
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}

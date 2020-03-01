package com.github.af6140.jboss.vault;

@SuppressWarnings("unused")
public class VaultException extends Exception {

  public VaultException() {}

  public VaultException(String message) {
    super(message);
  }

  public VaultException(String message, Throwable cause) {
    super(message, cause);
  }
}

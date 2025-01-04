package com.example.backendtaskmanagement.exceptions;

public class InvalidTaskStatusException extends RuntimeException {
  public InvalidTaskStatusException(String message) {
    super(message);
  }
}

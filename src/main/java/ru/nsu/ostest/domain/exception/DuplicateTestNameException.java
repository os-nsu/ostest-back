package ru.nsu.ostest.domain.exception;

public class DuplicateTestNameException extends RuntimeException {

  private DuplicateTestNameException(String message) {
    super(message);
  }

  public static DuplicateTestNameException of(String name) {
    return new DuplicateTestNameException(String.format("Test with name '%s' already exists", name));
  }
}

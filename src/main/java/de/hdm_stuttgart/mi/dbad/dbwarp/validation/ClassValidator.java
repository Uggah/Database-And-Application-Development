package de.hdm_stuttgart.mi.dbad.dbwarp.validation;

import jakarta.validation.ValidationException;

public interface ClassValidator<T> {

  void validate(T input) throws ValidationException;

}

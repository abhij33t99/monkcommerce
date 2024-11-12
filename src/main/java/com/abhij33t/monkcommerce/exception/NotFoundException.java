package com.abhij33t.monkcommerce.exception;

import com.abhij33t.monkcommerce.dto.Field;

public class NotFoundException extends RuntimeException{
    public <T> NotFoundException(Field field, T id) {
        super("Field " + field + " not found with id " + id);
    }
}

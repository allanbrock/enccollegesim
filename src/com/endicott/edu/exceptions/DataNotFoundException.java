package com.endicott.edu.exceptions;

import java.io.Serializable;

/**
 * Created by abrocken on 7/23/2017.
 */
public class DataNotFoundException extends RuntimeException implements Serializable{

    private static final long serialVersionUID = 1L;

    public DataNotFoundException(String message) {
        super(message);
    }
}

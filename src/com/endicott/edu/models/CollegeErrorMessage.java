package com.endicott.edu.models;

//import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

//@XmlRootElement
public class CollegeErrorMessage implements Serializable {
    private String errorMessage;
    private int errorCode;
    private String documentation;

    public CollegeErrorMessage(String errorMessage, int errorCode, String documentation) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.documentation = documentation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}

package ru.peshekhonov.cloud;

import lombok.Getter;

@Getter
public enum StatusType {
    OK("the file was successfully copied"),
    HANDLED_ERROR1("I/O error"),
    HANDLED_ERROR2("the file already exists"),
    HANDLED_ERROR3("SubsequentData timeout"),
    HANDLED_ERROR4("files information list cannot be obtained"),
    ERROR1("the file does not exist on server"),
    ERROR2("server failed to copy the file"),
    ERROR3("server failed to rename the file"),
    ERROR4("server failed to delete the file"),
    ERROR5("server failed to move the file"),
    WARNING1("server cannot delete non-empty directory");

    private final String text;

    StatusType(String text) {
        this.text = text;
    }
}

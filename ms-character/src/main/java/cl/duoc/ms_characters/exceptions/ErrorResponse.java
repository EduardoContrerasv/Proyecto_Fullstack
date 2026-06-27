package cl.duoc.ms_characters.exceptions;

public record ErrorResponse(int status, String message, String timestamp) {}
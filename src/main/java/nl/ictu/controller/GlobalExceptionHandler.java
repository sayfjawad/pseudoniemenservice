package nl.ictu.controller;

import lombok.extern.slf4j.Slf4j;
import nl.ictu.controller.exception.InvalidOINException;
import nl.ictu.service.exception.IdentifierPrivateKeyException;
import nl.ictu.service.exception.InvalidWsIdentifierRequestTypeException;
import nl.ictu.service.exception.InvalidWsIdentifierTokenException;
import nl.ictu.service.exception.TokenPrivateKeyException;
import nl.ictu.service.exception.WsGetTokenProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catch-all for any unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleGenericException(Exception ex) {

        log.error("Unexpected error occurred", ex);
        return new ResponseEntity<>(
                "An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handles exceptions of type IdentifierPrivateKeyException and returns an appropriate HTTP
     * response with the exception message.
     *
     * @param ex the IdentifierPrivateKeyException to be handled
     * @return a ResponseEntity containing the exception message and an UNPROCESSABLE_ENTITY (422)
     * status
     */
    @ExceptionHandler(IdentifierPrivateKeyException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public String handleIdentifierPrivateKeyException(IdentifierPrivateKeyException ex) {

        return ex.getMessage();
    }

    /**
     * Handles exceptions of type InvalidWsIdentifierRequestTypeException and returns the exception
     * message. This handler sets the HTTP response status to UNPROCESSABLE_ENTITY (422).
     *
     * @param ex the InvalidWsIdentifierRequestTypeException to be handled
     * @return the exception message as a String
     */
    @ExceptionHandler(InvalidWsIdentifierRequestTypeException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public String handleInvalidWsIdentifierRequestTypeException(
            InvalidWsIdentifierRequestTypeException ex) {

        return ex.getMessage();
    }

    /**
     * Handles exceptions of type InvalidWsIdentifierTokenException and returns an appropriate HTTP
     * response with the exception message.
     *
     * @param ex the InvalidWsIdentifierTokenException to be handled
     * @return the exception message as a String
     */
    @ExceptionHandler(InvalidWsIdentifierTokenException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public String handleInvalidWsIdentifierTokenException(
            InvalidWsIdentifierTokenException ex) {

        return ex.getMessage();
    }

    /**
     * Handles exceptions of type TokenPrivateKeyException and returns an appropriate HTTP response
     * with the exception message.
     *
     * @param ex the TokenPrivateKeyException to be handled
     * @return the exception message as a String
     */
    @ExceptionHandler(TokenPrivateKeyException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public String handleTokenPrivateKeyException(
            TokenPrivateKeyException ex) {

        return ex.getMessage();
    }

    /**
     * Handles exceptions of type WsGetTokenProcessingException and returns an appropriate HTTP
     * response with the exception message.
     *
     * @param ex the WsGetTokenProcessingException to be handled
     * @return the exception message as a String
     */
    @ExceptionHandler(WsGetTokenProcessingException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public String handleWsGetTokenProcessingException(
            WsGetTokenProcessingException ex) {

        return ex.getMessage();
    }

    /**
     * Handles exceptions of type InvalidOINException and returns the exception message.
     * This handler sets the HTTP response status to UNPROCESSABLE_ENTITY (422).
     *
     * @param ex the InvalidOINException to be handled
     * @return the exception message as a String
     */
    @ExceptionHandler(InvalidOINException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public String handleInvalidOINException(
            InvalidOINException ex) {

        return ex.getMessage();
    }
}

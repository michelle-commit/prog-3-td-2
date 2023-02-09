package app.foot.exception.ExceptionHandler;

import app.foot.exception.BadRequestException;
import app.foot.exception.NotFoundException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetail> NotFoundExceptionHandling(NotFoundException exception, WebRequest request){
        if (AnnotationUtils.findAnnotation
                (exception.getClass(), ResponseStatus.class) != null)
            throw exception;
        ErrorDetail errorDetails =
                new ErrorDetail(new Date(), exception.getMessage(), request.getDescription(true));

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetail> BadRequestExceptionHandling(BadRequestException exception, WebRequest request){
        if (AnnotationUtils.findAnnotation
                (exception.getClass(), ResponseStatus.class) != null)
            throw exception;
        ErrorDetail errorDetailsFormat =
                new ErrorDetail(new Date(), exception.getMessage(), request.getDescription(true));
        return new ResponseEntity<>(errorDetailsFormat, HttpStatus.BAD_REQUEST);
    }
}

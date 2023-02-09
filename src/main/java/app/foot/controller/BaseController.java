package app.foot.controller;

import app.foot.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BaseController {

    @ExceptionHandler({BadRequestException.class})
    public void handleException() {
        log.info("A bad request exception was handled");
    }


    private RestException toRest(java.lang.Exception e, HttpStatus status) {
        var restException = new RestException();
        restException.setType(status.toString());
        restException.setMessage(e.getMessage());
        return restException;
    }
}

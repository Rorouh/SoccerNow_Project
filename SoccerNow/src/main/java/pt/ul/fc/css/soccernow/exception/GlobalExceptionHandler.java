package pt.ul.fc.css.soccernow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import pt.ul.fc.css.soccernow.service.exceptions.ApplicationException;
import pt.ul.fc.css.soccernow.service.exceptions.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Não encontrado");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAppException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Erro de Aplicação");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle404(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Página não encontrada");
        model.addAttribute("errorMessage", "A página solicitada não existe.");
        return "error/custom-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Erro interno");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }
}

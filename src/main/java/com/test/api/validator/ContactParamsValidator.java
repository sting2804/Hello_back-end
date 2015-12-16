package com.test.api.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
public class ContactParamsValidator {

    /**
     * проверяет, есть ли синтаксические ошибки в регулярном выражении
     * @param regularExpression
     * @return
     */
    public String validateRegex(String regularExpression) {
        try {
            Pattern.compile(regularExpression);
        } catch (PatternSyntaxException exception) {
            return exception.getDescription();
        }
        return null;
    }
}

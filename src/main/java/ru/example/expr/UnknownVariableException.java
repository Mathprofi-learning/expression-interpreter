package ru.example.expr;

/**
 * Ошибка обращения к неизвестной переменной.
 */
public class UnknownVariableException extends EvaluationException {

    private final String variableName;

    /**
     * Создаёт ошибку неизвестной переменной.
     *
     * @param variableName имя неизвестной переменной
     */
    public UnknownVariableException(String variableName) {
        super("Неизвестная переменная: " + variableName);
        this.variableName = variableName;
    }

    /**
     * Возвращает имя неизвестной переменной.
     *
     * @return имя переменной
     */
    public String getVariableName() {
        return variableName;
    }
}
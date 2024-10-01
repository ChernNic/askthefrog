package com.chern.askthefrog;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DialogController {

    @GetMapping("/")
    public String getDialogPage(Model model) {
        model.addAttribute("responseText", "Привет! Я лицо. Чем могу помочь?");
        model.addAttribute("faceGif", "face_idle.gif");
        return "dialog";
    }

    @GetMapping("/calculate")
    @ResponseBody
    public ResponseEntity<String> calculate(@RequestParam("expression") String expression) {
        try {
            double result = evaluateExpression(expression);  // Вызов метода для расчета
            return ResponseEntity.ok("Результат: " + result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Я вас не понял, используйте только нужные символы.");
        }
    }

    private double evaluateExpression(String expression) {
        expression = expression.trim();

        if (expression.matches("[0-9]+\\s*\\+\\s*[0-9]+")) {
            String[] parts = expression.split("\\+");
            return Double.parseDouble(parts[0].trim()) + Double.parseDouble(parts[1].trim());
        } else if (expression.matches("[0-9]+\\s*\\-\\s*[0-9]+")) {
            String[] parts = expression.split("\\-");
            return Double.parseDouble(parts[0].trim()) - Double.parseDouble(parts[1].trim());
        } else if (expression.matches("[0-9]+\\s*\\/\\s*[0-9]+")) {
            String[] parts = expression.split("\\/");
            double divisor = Double.parseDouble(parts[1].trim());
            if (divisor == 0) {
                throw new IllegalArgumentException("Деление на ноль не разрешено");
            }
            return Double.parseDouble(parts[0].trim()) / divisor;
        } else if (expression.matches("[0-9]+\\s*\\*\\s*[0-9]+")) {
            String[] parts = expression.split("\\*");
            return Double.parseDouble(parts[0].trim()) * Double.parseDouble(parts[1].trim());
        } else {
            throw new IllegalArgumentException("Некорректное выражение");
        }
    }

    @GetMapping("/convert")
    @ResponseBody
    public ResponseEntity<String> convert(
            @RequestParam("amount") double amount,
            @RequestParam("fromCurrency") String fromCurrency,
            @RequestParam("toCurrency") String toCurrency) {
        if (amount <= 0) {
            return ResponseEntity.badRequest().body("Введите положительное число для суммы.");
        }

        double conversionRate = getConversionRate(fromCurrency, toCurrency);
        double result = amount * conversionRate;
        return ResponseEntity.ok("Конвертированная сумма: " + result + " " + toCurrency);
    }

    private double getConversionRate(String fromCurrency, String toCurrency) {

        if (fromCurrency.equals("USD") && toCurrency.equals("RUB")) {
            return 75.0;
        } else if (fromCurrency.equals("EUR") && toCurrency.equals("RUB")) {
            return 85.0;
        } else if (fromCurrency.equals("USD") && toCurrency.equals("EUR")) {
            return 0.9;
        } else if (fromCurrency.equals("EUR") && toCurrency.equals("USD")) {
            return 1.1;
        }
        throw new IllegalArgumentException("Неизвестные валюты.");
    }
}

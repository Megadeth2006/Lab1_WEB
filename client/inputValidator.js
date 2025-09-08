export default class inputValidator{
    constructor(responseCode, message) {
        this.responseCode = responseCode;
        this.message = message;
    }
    
    validate(xVal, yVal, rVal){
        // Валидация X - должно быть числом в диапазоне (-5, 5)
        if (isNaN(xVal) || xVal <= -5 || xVal >= 5) {
            this.message = "Значение X должно быть числом в диапазоне (-5, 5)";
            this.responseCode = 0;
            return;
        }
        
        // Валидация Y - должно быть из списка {-4, -3, -2, -1, 0, 1, 2, 3, 4}
        const validYValues = [-4, -3, -2, -1, 0, 1, 2, 3, 4];
        if (!validYValues.includes(yVal)) {
            this.message = "Значение Y должно быть из списка {-4, -3, -2, -1, 0, 1, 2, 3, 4}";
            this.responseCode = 0;
            return;
        }
        
        // Валидация R - должно быть числом в диапазоне (2, 5)
        if (isNaN(rVal) || rVal <= 2 || rVal >= 5) {
            this.message = "Значение R должно быть числом в диапазоне (2, 5)";
            this.responseCode = 0;
            return;
        }
        
        // Все проверки пройдены
        this.responseCode = 1;
        this.message = "Все значения корректны";
    }
    
    // Дополнительная валидация для текстового ввода X
    validateXInput(xInput) {
        // Проверка на пустоту
        if (!xInput || xInput.trim() === '') {
            this.message = "Поле X не может быть пустым";
            this.responseCode = 0;
            return false;
        }
        
        // Проверка на наличие только цифр, точки, минуса и пробелов
        if (!/^[-+]?(\d+\.?\d*|\.\d+)$/.test(xInput.trim())) {
            this.message = "Поле X должно содержать только числа (можно с десятичной точкой и знаком минус)";
            this.responseCode = 0;
            return false;
        }
        
        // Проверка на корректное числовое значение
        const numValue = parseFloat(xInput.trim());
        if (isNaN(numValue)) {
            this.message = "Некорректное числовое значение в поле X";
            this.responseCode = 0;
            return false;
        }
        
        // Проверка диапазона (-5, 5)
        if (numValue <= -5 || numValue >= 5) {
            this.message = "Значение X должно быть в диапазоне (-5, 5)";
            this.responseCode = 0;
            return false;
        }
        
        return true;
    }
    
    // Дополнительная валидация для текстового ввода R
    validateRInput(rInput) {
        // Проверка на пустоту
        if (!rInput || rInput.trim() === '') {
            this.message = "Поле R не может быть пустым";
            this.responseCode = 0;
            return false;
        }
        
        // Проверка на наличие только цифр, точки, минуса и пробелов
        if (!/^[-+]?(\d+\.?\d*|\.\d+)$/.test(rInput.trim())) {
            this.message = "Поле R должно содержать только числа (можно с десятичной точкой и знаком минус)";
            this.responseCode = 0;
            return false;
        }
        
        // Проверка на корректное числовое значение
        const numValue = parseFloat(rInput.trim());
        if (isNaN(numValue)) {
            this.message = "Некорректное числовое значение в поле R";
            this.responseCode = 0;
            return false;
        }
        
        // Проверка диапазона (2, 5)
        if (numValue <= 2 || numValue >= 5) {
            this.message = "Значение R должно быть в диапазоне (2, 5)";
            this.responseCode = 0;
            return false;
        }
        
        return true;
    }
    
    // Валидация для предотвращения ввода некорректных символов
    sanitizeInput(input) {
        // Удаляем все символы кроме цифр, точки, минуса и пробелов
        return input.replace(/[^0-9.\-\s]/g, '');
    }

    getResponseCode(){
        return this.responseCode;
    }
    getMessage(){
        return this.message;
    }
}
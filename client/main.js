import inputValidator from "./inputValidator.js";

// Исправленная система управления уведомлениями
class NotificationManager {
    constructor() {
        this.currentToast = null;
        this.removalTimeout = null;
    }

    showToast(options) {
        // Очищаем предыдущее уведомление
        this.clearCurrentToast();
        
        // Создаем новое уведомление
        this.currentToast = Toastify({
            text: options.text,
            className: options.className || "info",
            style: {
                background: "linear-gradient(to right, #ff6b6b, #ee5a24)",
                border: "1px solid white",
                marginLeft: "20px",
                maxWidth: "400px",
                wordWrap: "break-word"
            },
            position: "top-right",
            gravity: "top",
            stopOnFocus: true,
            newestOnTop: true,
            offset: {
                x: 50,
                y: 60
            },
            duration: 2000,
            close: true,
            onClick: () => {
                this.clearCurrentToast();
            }
        });

        this.currentToast.showToast();
        
        // Привязываем кнопку закрытия к нашему методу
        setTimeout(() => {
            const closeButton = document.querySelector('.toast-close');
            if (closeButton) {
                closeButton.addEventListener('click', (e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.clearCurrentToast();
                });
            }
        }, 100);
        
        // Автоматическое удаление через 2.5 секунды
        this.removalTimeout = setTimeout(() => {
            this.clearCurrentToast();
        }, 2500);
    }

    clearCurrentToast() {
        // Очищаем таймаут
        if (this.removalTimeout) {
            clearTimeout(this.removalTimeout);
            this.removalTimeout = null;
        }
        
        // Удаляем текущее уведомление
        if (this.currentToast) {
            try {
                this.currentToast.hideToast();
            } catch (e) {
                console.log('hideToast failed, removing manually');
            }
            this.currentToast = null;
        }
        
        // Принудительно удаляем все уведомления из DOM
        const allToasts = document.querySelectorAll('.toastify');
        allToasts.forEach(toast => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 200);
        });
    }
}

// Создаем глобальный экземпляр менеджера
const notificationManager = new NotificationManager();

document.addEventListener("DOMContentLoaded", () => {
    // Update Date and Time Function
    function updateDateTime() {
        const now = new Date();
        document.getElementById("date").innerText = now.toDateString();
        document.getElementById("time").innerText = now.toTimeString().substring(0, 8);
    }

    // Initial Date Time Update
    updateDateTime();
    setInterval(updateDateTime, 1000);
});

window.onload = function () {
    // const savedData = JSON.parse(localStorage.getItem('tableData')) || [];
    // savedData.forEach(data => {
    //     addToTable(data.x, data.y, data.r, data.result, data.curr_time, data.exec_time);
    // })
    console.log(localStorage.getItem("session"));
    document.getElementById("output").innerHTML = localStorage.getItem("session");
}

// Добавляем валидацию в реальном времени для текстовых полей
document.addEventListener('DOMContentLoaded', function() {
    const xInput = document.querySelector('#x');
    const rInput = document.querySelector('#r');
    
    // Валидация для поля X
    if (xInput) {
        // Предотвращаем ввод некорректных символов
        xInput.addEventListener('input', function(e) {
            const validator = new inputValidator();
            const sanitizedValue = validator.sanitizeInput(e.target.value);
            if (e.target.value !== sanitizedValue) {
                e.target.value = sanitizedValue;
                notificationManager.showToast({
                    text: "Разрешены только цифры, точка, минус и пробелы",
                });
            }
        });

        // Валидация при потере фокуса
        xInput.addEventListener('blur', function(e) {
            const validator = new inputValidator();
            if (!validator.validateXInput(e.target.value)) {
                notificationManager.showToast({
                    text: validator.getMessage()
                });
                e.target.focus();
            }
        });
    }
    
    // Валидация для поля R
    if (rInput) {
        // Предотвращаем ввод некорректных символов
        rInput.addEventListener('input', function(e) {
            const validator = new inputValidator();
            const sanitizedValue = validator.sanitizeInput(e.target.value);
            if (e.target.value !== sanitizedValue) {
                e.target.value = sanitizedValue;
                notificationManager.showToast({
                    text: "Разрешены только цифры, точка, минус и пробелы",
                });
            }
        });

        // Валидация при потере фокуса
        rInput.addEventListener('blur', function(e) {
            const validator = new inputValidator();
            if (!validator.validateRInput(e.target.value)) {
                notificationManager.showToast({
                    text: validator.getMessage()
                });
                e.target.focus();
            }
        });
    }
});

const mainForm = document.querySelector('input[value="Проверить"]');
let isSubmitting = false; // Флаг для предотвращения множественных отправок

mainForm.addEventListener('click', function (e) {
    // default action is to send the form data to the server and reload the page
    // by calling .preventDefault() i am stopping the browser from doing this, 
    // which allows me to handle the form submission programmatically in your JavaScript code instead.
    e.preventDefault();
    
    // Предотвращаем множественные отправки
    if (isSubmitting) {
        return;
    }
    isSubmitting = true;

    const xElement = document.querySelector('#x');
    const yElement = document.querySelector('input[name="yVal"]:checked');
    const rElement = document.querySelector('#r');

    if (xElement && yElement && rElement) {
        const xVal = parseFloat(xElement.value.trim());
        const yVal = parseFloat(yElement.value);
        const rVal = parseFloat(rElement.value.trim());
        console.log(`X: ${xVal}, Y: ${yVal}, R: ${rVal}`);

        let validator = new inputValidator();
        
        // Сначала валидируем текстовые вводы X и R
        if (!validator.validateXInput(xElement.value)) {
            notificationManager.showToast({
                text: validator.getMessage()
            });
            isSubmitting = false;
            return;
        }
        
        if (!validator.validateRInput(rElement.value)) {
            notificationManager.showToast({
                text: validator.getMessage()
            });
            isSubmitting = false;
            return;
        }
        
        // Затем валидируем числовые значения
        validator.validate(xVal, yVal, rVal);

        if (validator.getResponseCode() === 1) {
            console.log(`everything is ok`);

            // Создаем URLSearchParams для POST запроса
            const formData = new URLSearchParams();
            formData.append('xVal', xVal);
            formData.append('yVal', yVal);
            formData.append('rVal', rVal);

            fetch(`http://localhost:8080/script`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Server responded with bad getaway status: ${response.status}`);
                    }
                    return response.text();
                })
                .then(function (serverAnswer) {
                    localStorage.setItem("session", serverAnswer);
                    document.getElementById("output").innerHTML = serverAnswer;
                    // addToTable(xVal, yVal, rVal, responseData.result, responseData.curr_time, responseData.exec_time);
                    // saveToLocalStorage(xVal, yVal, rVal, responseData.result, responseData.curr_time, responseData.exec_time);
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    alert(`There was an error processing your request: ${error.message}`)
                })
                .finally(() => {
                    // Сбрасываем флаг после завершения запроса
                    isSubmitting = false;
                })
        } else {
            notificationManager.showToast({
                text: validator.getMessage()
            });
            // Сбрасываем флаг при ошибке валидации
            isSubmitting = false;
            return;
        }
    } else {
        notificationManager.showToast({
            text: "Пожалуйста, заполните все поля формы перед отправкой",
        });
        // Сбрасываем флаг при ошибке заполнения полей
        isSubmitting = false;
    }
});



import inputValidator from "./inputValidator.js";

// Простая и надежная система уведомлений
class NotificationManager {
    constructor() {
        this.currentToast = null;
        this.removalTimeout = null;
    }

    showToast(options) {
        // Мгновенно удаляем все существующие уведомления
        this.clearAllToasts();
        
        // Создаем новое уведомление
        this.currentToast = Toastify({
            text: options.text,
            className: options.className || "info",
            style: {
                background: "linear-gradient(to right, #ff6b6b, #ee5a24)",
                border: "1px solid white",
                marginLeft: "20px",
                maxWidth: "400px",
                wordWrap: "break-word",
                cursor: "pointer"
            },
            position: "top-right",
            gravity: "top",
            stopOnFocus: true,
            newestOnTop: true,
            offset: {
                x: 50,
                y: 60
            },
            duration: 3000, // 3 секунды
            close: false, // Отключаем встроенную кнопку
            onClick: () => {
                this.clearAllToasts();
            }
        });

        this.currentToast.showToast();
        
        // Добавляем обработчик клика на весь элемент уведомления
        setTimeout(() => {
            const toastElement = document.querySelector('.toastify');
            if (toastElement) {
                toastElement.addEventListener('click', () => {
                    this.clearAllToasts();
                });
            }
        }, 100);
        
        // Автоматическое удаление через 3.5 секунды
        this.removalTimeout = setTimeout(() => {
            this.clearAllToasts();
        }, 3500);
    }

    clearAllToasts() {
        // Очищаем таймаут
        if (this.removalTimeout) {
            clearTimeout(this.removalTimeout);
            this.removalTimeout = null;
        }
        
        // Удаляем все уведомления из DOM
        const allToasts = document.querySelectorAll('.toastify');
        allToasts.forEach(toast => {
            toast.remove();
        });
        
        if (this.currentToast) {
            this.currentToast = null;
        }
    }
}

// Создаем глобальный экземпляр менеджера
const notificationManager = new NotificationManager();

// Глобальные переменные
let isSubmitting = false;
let mainForm = null;
let formHandlerAttached = false;
let lastSubmissionTime = 0;
let tabId = null;

document.addEventListener("DOMContentLoaded", () => {
    // Генерируем уникальный ID для этой вкладки
    tabId = 'tab_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    console.log('Tab ID:', tabId);
    
    // Update Date and Time Function
    function updateDateTime() {
        const now = new Date();
        document.getElementById("date").innerText = now.toDateString();
        document.getElementById("time").innerText = now.toTimeString().substring(0, 8);
    }

    // Initial Date Time Update
    updateDateTime();
    setInterval(updateDateTime, 1000);

    // Загружаем сохраненные данные
    loadSavedResults();

    // Инициализируем форму
    mainForm = document.querySelector('input[value="Проверить"]');
    if (mainForm) {
        setupFormHandlers();
    }


    
    // Очищаем блокировку при закрытии вкладки
    window.addEventListener('beforeunload', function() {
        const globalSubmissionKey = 'form_submission_active';
        const activeSubmission = localStorage.getItem(globalSubmissionKey);
        if (activeSubmission === tabId) {
            localStorage.removeItem(globalSubmissionKey);
            console.log('Cleared global lock on tab close');
        }
    });

    // Добавляем валидацию в реальном времени для текстовых полей
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

// Функция для настройки обработчиков формы
function setupFormHandlers() {
    if (!mainForm || formHandlerAttached) return;
    
    console.log('Setting up form handlers');
    
    // Добавляем обработчик только один раз
    mainForm.addEventListener('click', handleFormSubmit, { once: false });
    formHandlerAttached = true;
    
    console.log('Form handler attached');
}




// Обработчик отправки формы
function handleFormSubmit(e) {
    // default action is to send the form data to the server and reload the page
    // by calling .preventDefault() i am stopping the browser from doing this, 
    // which allows me to handle the form submission programmatically in your JavaScript code instead.
    e.preventDefault();
    e.stopPropagation();
    
    const currentTime = Date.now();
    
    // Проверяем временную блокировку (минимум 2 секунды между отправками)
    if (currentTime - lastSubmissionTime < 2000) {
        console.log('Too soon since last submission, ignoring click');
        return;
    }
    
    // Проверяем глобальную блокировку через localStorage
    const globalSubmissionKey = 'form_submission_active';
    const activeSubmission = localStorage.getItem(globalSubmissionKey);
    if (activeSubmission && activeSubmission !== tabId) {
        console.log('Another tab is submitting, ignoring click');
        notificationManager.showToast({
            text: "Другая вкладка уже отправляет запрос. Подождите..."
        });
        return;
    }
    
    // Предотвращаем множественные отправки
    if (isSubmitting) {
        console.log('Form is already submitting, ignoring click');
        return;
    }
    
    // Устанавливаем глобальную блокировку
    localStorage.setItem(globalSubmissionKey, tabId);
    
    // Блокируем кнопку и устанавливаем флаг
    isSubmitting = true;
    lastSubmissionTime = currentTime;
    mainForm.disabled = true;
    mainForm.value = "Отправка...";
    
    console.log('Starting form submission at', new Date().toISOString());

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
            resetFormState();
            return;
        }
        
        if (!validator.validateRInput(rElement.value)) {
            notificationManager.showToast({
                text: validator.getMessage()
            });
            resetFormState();
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
            
            // Добавляем sessionId для сохранения результатов между сессиями
            let sessionId = localStorage.getItem('sessionId');
            if (!sessionId) {
                sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
                localStorage.setItem('sessionId', sessionId);
            }
            formData.append('sessionId', sessionId);

            fetch(`http://localhost:8080/fcgi-bin/app.jar`, {
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
                    return response.json();
                })
                .then(function (jsonData) {
                    localStorage.setItem("session", JSON.stringify(jsonData));
                    updateResultsTable(jsonData.results);
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    alert(`There was an error processing your request: ${error.message}`)
                })
                .finally(() => {
                    // Сбрасываем флаг после завершения запроса
                    resetFormState();
                })
        } else {
            notificationManager.showToast({
                text: validator.getMessage()
            });
            resetFormState();
            return;
        }
    } else {
        notificationManager.showToast({
            text: "Пожалуйста, заполните все поля формы перед отправкой",
        });
        resetFormState();
    }
}

// Функция для сброса состояния формы
function resetFormState() {
    console.log('Resetting form state');
    isSubmitting = false;
    
    // Очищаем глобальную блокировку
    const globalSubmissionKey = 'form_submission_active';
    const activeSubmission = localStorage.getItem(globalSubmissionKey);
    if (activeSubmission === tabId) {
        localStorage.removeItem(globalSubmissionKey);
        console.log('Global submission lock cleared');
    }
    
    if (mainForm) {
        mainForm.disabled = false;
        mainForm.value = "Проверить";
    }
    console.log('Form state reset at', new Date().toISOString());
}

// Принудительная очистка уведомлений каждые 500мс
setInterval(() => {
    const toasts = document.querySelectorAll('.toastify');
    if (toasts.length > 1) {
        // Оставляем только последнее уведомление
        for (let i = 0; i < toasts.length - 1; i++) {
            toasts[i].remove();
        }
    }
}, 500);

// Глобальная функция для принудительного закрытия всех уведомлений
window.closeAllToasts = function() {
    const allToasts = document.querySelectorAll('.toastify');
    allToasts.forEach(toast => {
        toast.remove();
    });
};

// Глобальная функция для принудительного сброса формы
window.resetForm = function() {
    console.log('Force resetting form');
    resetFormState();
    lastSubmissionTime = 0;
};

// Глобальная функция для проверки состояния формы
window.checkFormState = function() {
    console.log('Form state:', {
        isSubmitting: isSubmitting,
        formHandlerAttached: formHandlerAttached,
        lastSubmissionTime: lastSubmissionTime,
        timeSinceLastSubmission: Date.now() - lastSubmissionTime,
        tabId: tabId,
        globalLock: localStorage.getItem('form_submission_active')
    });
};

// Глобальная функция для принудительной очистки всех блокировок
window.clearAllLocks = function() {
    console.log('Clearing all locks');
    localStorage.removeItem('form_submission_active');
    resetFormState();
    lastSubmissionTime = 0;
};

// Функция для преобразования координат из системы координат приложения в координаты SVG
function convertToSVGCoordinates(x, y, r, svgWidth = 300, svgHeight = 300) {
    // Центр SVG (150, 150)
    const centerX = svgWidth / 2;
    const centerY = svgHeight / 2;
    
    // Масштаб для отображения (пикселей на единицу координат)
    const scale = 50; // 50 пикселей на единицу координат
    
    // Преобразуем координаты
    const svgX = centerX + (x * scale);
    const svgY = centerY - (y * scale); // Инвертируем Y для правильного отображения
    
    return { x: svgX, y: svgY };
}

// Функция для добавления точки на график
function addPointToGraph(x, y, r, isInArea) {
    const svg = document.querySelector('svg');
    if (!svg) return;
    
    const coords = convertToSVGCoordinates(x, y, r);
    
    // Создаем группу для точки
    const pointGroup = document.createElementNS('http://www.w3.org/2000/svg', 'g');
    pointGroup.setAttribute('class', 'point-group');
    
    // Создаем круг для точки
    const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
    circle.setAttribute('cx', coords.x);
    circle.setAttribute('cy', coords.y);
    circle.setAttribute('r', '4');
    circle.setAttribute('fill', isInArea ? '#00ff00' : '#ff0000');
    circle.setAttribute('stroke', '#000000');
    circle.setAttribute('stroke-width', '1');
    circle.setAttribute('class', isInArea ? 'point-in' : 'point-out');
    
    // Добавляем анимацию появления
    circle.setAttribute('opacity', '0');
    circle.style.transition = 'opacity 0.5s ease-in-out';
    
    pointGroup.appendChild(circle);
    svg.appendChild(pointGroup);
    
    // Анимация появления
    setTimeout(() => {
        circle.setAttribute('opacity', '1');
    }, 100);
    
    // Добавляем подпись с координатами
    const label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
    label.setAttribute('x', coords.x + 8);
    label.setAttribute('y', coords.y - 8);
    label.setAttribute('font-size', '10');
    label.setAttribute('fill', '#ffffff');
    label.setAttribute('class', 'point-label');
    label.textContent = `(${x}, ${y})`;
    
    pointGroup.appendChild(label);
    
}


// Функция для обновления таблицы результатов
function updateResultsTable(results) {
    const tbody = document.getElementById("output");
    if (!tbody) return;
    
    if (!results || results.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #666;">Нет результатов</td></tr>';
        return;
    }
    
    
    let html = '';
    results.forEach(result => {
        const resultText = result.isInArea ? 'Попал!' : 'Не попал!';
        const resultClass = result.isInArea ? 'result-cell-in' : 'result-cell-out';
        
        // Добавляем точку на график
        addPointToGraph(result.x, result.y, result.r, result.isInArea);
        
        html += `
            <tr>
                <td>${result.x}</td>
                <td>${result.y}</td>
                <td>${result.r}</td>
                <td><span class="${resultClass}">${resultText}</span></td>
                <td>${result.currentTime}</td>
                <td>${result.executionTime.toFixed(6)}</td>
            </tr>
        `;
    });
    
    tbody.innerHTML = html;
}

// Функция для загрузки сохраненных результатов
function loadSavedResults() {
    const sessionId = localStorage.getItem('sessionId');
    if (sessionId) {
        // Запрашиваем результаты для текущей сессии
        fetch(`http://localhost:8080/fcgi-bin/app.jar?sessionId=${encodeURIComponent(sessionId)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error(`Server responded with status: ${response.status}`);
        })
        .then(jsonData => {
            if (jsonData && jsonData.results && jsonData.results.length > 0) {
                updateResultsTable(jsonData.results);
                localStorage.setItem("session", JSON.stringify(jsonData));
            } else {
                // Если нет результатов, показываем заглушку
                document.getElementById("output").innerHTML = 
                    '<tr><td colspan="6" style="text-align: center; color: #666;">Нет сохраненных результатов</td></tr>';
            }
        })
        .catch(error => {
            console.error('Error loading saved results:', error);
            // Показываем заглушку при ошибке
            document.getElementById("output").innerHTML = 
                '<tr><td colspan="6" style="text-align: center; color: #666;">Ошибка загрузки результатов</td></tr>';
        });
    } else {
        // Если нет sessionId, показываем заглушку
        document.getElementById("output").innerHTML = 
            '<tr><td colspan="6" style="text-align: center; color: #666;">Нет сохраненных результатов</td></tr>';
    }
}

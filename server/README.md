# Area Checker FastCGI Server

FastCGI сервер на Java для проверки попадания точки в заданную область.

## Архитектура

- **FastCGI сервер** (Java) - обрабатывает запросы через FastCGI протокол
- **SessionManager** - управляет сессиями и сохраняет результаты в файловой системе
- **Клиент** (HTML/JS) - отправляет AJAX запросы и отображает результаты

## Сборка проекта

Проект использует Gradle для сборки.

### Требования
- Java 11 или выше
- Gradle (опционально, используется wrapper)

### Команды сборки

```bash
# Сборка проекта
./gradlew build

# Создание fat jar с зависимостями
./gradlew shadowJar

# Запуск тестов
./gradlew test

# Очистка проекта
./gradlew clean
```

## Запуск сервера

Сервер работает только через веб-сервер с поддержкой FastCGI (Apache, Nginx).

```bash
# Сборка fat jar
./gradlew shadowJar

# Запуск через веб-сервер (см. конфигурацию ниже)
java -jar build/libs/app.jar
```

### Через Gradle
```bash
# Сборка и запуск
./gradlew build shadowJar
```

## FastCGI конфигурация

Сервер использует библиотеку `fastcgi-lib.jar` для работы с FastCGI протоколом.

### Настройка веб-сервера

Для работы с Apache httpd добавьте в конфигурацию:

```apache
# FastCGI конфигурация
LoadModule fcgid_module modules/mod_fcgid.so

<Directory "/path/to/your/project">
    Options +ExecCGI
    AddHandler fcgid-script .fcgi
    FcgidWrapper /path/to/java -jar /path/to/app.jar
</Directory>

# Перенаправление запросов к FastCGI
RewriteEngine On
RewriteRule ^script$ /app.fcgi [L]
```

## Структура проекта

```
server/
├── build.gradle              # Gradle конфигурация
├── settings.gradle           # Настройки проекта
├── gradlew                   # Gradle wrapper (Unix)
├── gradlew.bat              # Gradle wrapper (Windows)
├── lib/
│   └── fastcgi-lib.jar      # FastCGI библиотека
├── src/main/java/lab1/
│   ├── FastCGIServer.java   # Основной FastCGI сервер
│   ├── SessionManager.java  # Управление сессиями
│   ├── AreaChecker.java     # Логика проверки области
│   ├── CoordinatesValidator.java # Валидация координат
│   └── ResponseBuilder.java # Построение ответов
└── sessions/                # Папка для сохранения сессий
```

## API

### POST /script
Обрабатывает запрос на проверку попадания точки в область.

**Параметры:**
- `xVal` (double) - X координата (-5 < x < 5)
- `yVal` (double) - Y координата (из списка: -4, -3, -2, -1, 0, 1, 2, 3, 4)
- `rVal` (double) - Радиус (2 < r < 5)
- `sessionId` (string, опционально) - ID сессии

**Ответ:** HTML таблица с результатами

### GET /script
Загружает сохраненные результаты для сессии.

**Параметры:**
- `sessionId` (string) - ID сессии

**Ответ:** HTML таблица с результатами

## Особенности

1. **Персистентные сессии** - результаты сохраняются в файловой системе
2. **FastCGI протокол** - использует стандартный FastCGI интерфейс
3. **Валидация данных** - проверка корректности входных параметров
4. **CORS поддержка** - для работы с AJAX запросами
5. **Логирование** - подробные логи через SLF4J

## Разработка

### Добавление зависимостей
Отредактируйте `build.gradle`:

```gradle
dependencies {
    implementation 'group:artifact:version'
}
```

### Запуск в режиме разработки
```bash
./gradlew run --continuous
```

### Отладка
```bash
./gradlew run --debug-jvm
```

## Миграция с Maven

Проект был мигрирован с Maven на Gradle:

- ✅ Удален `pom.xml`
- ✅ Удалена папка `target/`
- ✅ Создан `build.gradle`
- ✅ Настроен Gradle wrapper
- ✅ Перенесены все зависимости
- ✅ Настроен shadow plugin для fat jar

## Лицензия

MIT License
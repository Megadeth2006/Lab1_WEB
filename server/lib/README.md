# Папка lib

Поместите сюда локальные JAR библиотеки.

## Структура:
```
server-java/lib/
├── fastCGI.jar          # Ваша FastCGI библиотека
└── README.md           # Этот файл
```

## Как добавить JAR:

1. **Скопируйте** ваш `fastCGI.jar` в эту папку
2. **Обновите** `pom.xml` (уже настроено):
   ```xml
   <dependency>
       <groupId>com.local</groupId>
       <artifactId>fastcgi</artifactId>
       <version>1.0</version>
       <scope>system</scope>
       <systemPath>${project.basedir}/lib/fastCGI.jar</systemPath>
   </dependency>
   ```

## Проверка:
```bash
mvn clean compile
```

Если компиляция прошла успешно - библиотека подключена правильно!

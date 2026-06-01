# Order Management

## Тесты и покрытие

Проект использует Gradle. Запустить тесты и сформировать JaCoCo-отчет можно командой:

```powershell
.\gradlew.bat clean test
```

HTML-отчет покрытия будет доступен по пути:

```text
build/reports/jacoco/test/html/index.html
```

XML-отчет покрытия будет доступен по пути:

```text
build/reports/jacoco/test/jacocoTestReport.xml
```

Полная проверка проекта:

```powershell
.\gradlew.bat check
```

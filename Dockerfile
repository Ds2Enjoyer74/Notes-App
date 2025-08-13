# Стадия сборки
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем все в контейнер
COPY . .

# Сборка проекта
RUN mvn clean package -DskipTests

# Стадия исполнения
FROM eclipse-temurin:21-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

# Открываем порт (если ваше приложение работает, например, на 8080)
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]

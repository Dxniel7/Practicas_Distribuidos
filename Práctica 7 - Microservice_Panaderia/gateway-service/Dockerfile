# Usar una imagen base de OpenJDK
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR desde el directorio de compilación (target) al contenedor
COPY target/gateway-service-*.jar app.jar

# Exponer el puerto en el que corre el servicio (en este caso, 8083)
EXPOSE 8083

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

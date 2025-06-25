# Usar una imagen base de OpenJDK
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR desde el directorio de compilación (target) al contenedor
COPY target/inventario-service-*.jar app.jar

# Exponer el puerto en el que corre el servicio (en este caso, 8081)
EXPOSE 8081

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

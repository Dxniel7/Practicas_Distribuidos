# Usar la imagen oficial de Apache
FROM httpd:alpine

# Copiar archivos estáticos al directorio raíz de Apache
COPY src/main/resources/static/ /usr/local/apache2/htdocs/

# Exponer el puerto por el que Apache sirve el contenido
EXPOSE 8084

# Cambiar el puerto por defecto de Apache (80) al 8084
RUN sed -i 's/Listen 80/Listen 8084/' /usr/local/apache2/conf/httpd.conf

# Prácticas de Sistemas Distribuidos

Este repositorio contiene las soluciones para las prácticas de la asignatura de Sistemas Distribuidos. A continuación, se detalla la estructura del proyecto y las instrucciones para compilar y ejecutar cada práctica.

**Enlace al Repositorio de GitHub:**
[https://github.com/Dxniel7/Practicas_Distribuidos](https://github.com/Dxniel7/Practicas_Distribuidos)

---

### **Consideraciones Generales Importantes:**

* **Conector JDBC de MySQL:** Las prácticas que requieren conexión a una base de datos (Prácticas 4 y 5) necesitan el **conector JDBC de MySQL** (por ejemplo, `mysql-connector-j-9.2.0.jar`). Asegúrate de que el archivo `.jar` esté en la ruta correcta o incluido en el `classpath` al compilar y ejecutar, de igual manera aún así se incluye dentro de cada carpeta necesaria.
* **Terminales Separadas:** Para las prácticas de cliente-servidor (2, 3, 4 y 5), necesitarás abrir varias terminales (como PowerShell o CMD en Windows) para ejecutar los componentes del servidor y del cliente de forma simultánea.
* **Compilación Previa:** Antes de ejecutar los archivos Java, es fundamental compilarlos. Los comandos proporcionados a continuación incluyen los pasos de compilación necesarios.
* **Errores `ClassNotFoundException`:** Si encuentras un error como `Error: Could not find or load main class`, asegúrate de estar ejecutando el comando `java` desde el directorio raíz de las clases compiladas y de usar el nombre de la clase sin la extensión `.java`.
* * **Docker:** La práctica 7 requiere tener Docker y Docker Compose instalados y en ejecución para orquestar los microservicios.


---

### **Práctica 1 - Procesos e Hilos**

**Descripción:** Esta práctica simula una panadería donde múltiples clientes (hilos) intentan comprar pan de forma concurrente de un stock compartido, demostrando el uso de `synchronized` para evitar condiciones de carrera.

**Ubicación de los archivos:** `Práctica 1 - Procesos e Hilos/`

**Pasos para la Ejecución:**

1.  Abre una terminal y navega al directorio de la práctica:
    ```bash
    cd "Práctica 1 - Procesos e Hilos"
    ```
2.  **Compila la clase Java:**
    ```bash
    javac PanaderiaConcurrente.java
    ```
3.  **Ejecuta la simulación:**
    ```bash
    java PanaderiaConcurrente
    ```

---

### **Práctica 2 - Cliente-Servidor**

**Descripción:** Implementa un modelo cliente-servidor básico para una panadería. El servidor gestiona el stock de pan y atiende las peticiones de un cliente a la vez.

**Ubicación de los archivos:** `Práctica 2 - Cliente-Servidor/`

**Pasos para la Ejecución:**

1.  Abre dos terminales y navega en ambas al directorio `Práctica 2 - Cliente-Servidor/`.
2.  **Compila las clases:**
    ```bash
    javac PanaderiaServidor.java PanaderiaCliente.java
    ```
3.  **En la primera terminal (Servidor):** Inicia el servidor de la panadería. Se quedará esperando conexiones de clientes.
    ```bash
    java PanaderiaServidor
    ```
4.  **En la segunda terminal (Cliente):** Ejecuta el cliente para conectarte al servidor.
    ```bash
    java PanaderiaCliente
    ```

---

### **Práctica 3 - Servidor-Multicliente**

**Descripción:** Esta práctica implementa un servidor de panadería capaz de manejar múltiples clientes de forma concurrente utilizando hilos, demostrando la capacidad del servidor para atender varias peticiones simultáneamente.

**Ubicación de los archivos:** `Práctica 3 - Servidor-Multicliente/`

**Pasos para la Ejecución:**

1.  Abre tu terminal y navega al directorio `Práctica 3 - Servidor-Multicliente/`.
2.  **Compila las clases:**
    ```bash
    javac PanaderiaServidor.java PanaderiaCliente.java
    ```
3.  **En una Terminal (Servidor):**
    ```bash
    java PanaderiaServidor
    ```
4.  **En otras Terminales (Clientes):** Puedes abrir varias terminales para simular múltiples clientes.
    ```bash
    java PanaderiaCliente
    ```

---

### **Práctica 4 - Multi-cliente, Multi-servidor con Servidor de Descubrimiento**

**Descripción:** Esta práctica extiende la arquitectura para incluir un "Servidor de Descubrimiento", que permite a múltiples servidores de panadería registrarse y a los clientes encontrarlos dinámicamente. La persistencia del stock se gestiona a través de una base de datos MySQL.

**Ubicación de los archivos:** `Práctica 4 - C-S_Panadeeria/`

**Requisitos:**
* Tener **MySQL** en ejecución.
* Haber creado la base de datos `panaderia` y la tabla `inventario` (puedes usar el archivo `Script.sql` proporcionado).
* Tener el archivo `mysql-connector-j-9.2.0.jar` en el directorio de la práctica o en el classpath, ya esta dicho archivo en la carpeta de la práctica.

**Pasos para la Ejecución:**

1.  Abre tres o más terminales y navega en todas al directorio `Práctica 4 - C-S_Panadeeria/`.
2.  **Compila todas las clases** (el `-cp` indica el classpath para incluir el conector de MySQL):
    ```bash
    javac -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaServidor.java PanaderiaCliente.java ServidorDescubrimiento.java
    ```
3.  **En la Terminal 1 (Servidor de Descubrimiento):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" ServidorDescubrimiento 5050
    ```
4.  **En la Terminal 2 (Servidor de Panadería):** Puedes repetir este paso en más terminales con puertos diferentes.
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaServidor 5000
    ```
5.  **En la Terminal 3 (Cliente):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaCliente
    ```

---

### **Práctica 5 - RMI (Java Remote Method Invocation)**

**Descripción:** Esta práctica utiliza Java RMI para la comunicación distribuida, permitiendo a los clientes invocar métodos en un objeto remoto (la panadería) que reside en el servidor. La gestión del stock también utiliza MySQL.

**Ubicación de los archivos:** `Práctica 5 - RMI_Panaderia/`

**Requisitos:**
* Tener **MySQL** en ejecución y la base de datos inicializada.
* Asegurarse de tener `mysql-connector-j-9.2.0.jar`, de igual manera ya esta presente en dicha carpeta.
* **Importante:** Se cambió la visibilidad del constructor `PanaderiaServidorRMI` de `protected` a `public` para que pudiera ser instanciado por `ServidorRMI`.

**Pasos para la Ejecución:**

1.  Abre dos terminales y navega en ambas al directorio `Práctica 5 - RMI_Panaderia/`.
2.  **Compila todas las clases:**
    ```bash
    javac -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaRMI.java PanaderiaServidorRMI.java ServidorRMI.java ClienteRMI.java
    ```
3.  **En la Terminal 1 (Servidor RMI):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" ServidorRMI
    ```
4.  **En la Terminal 2 (Cliente RMI):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" ClienteRMI
    ```

### **Práctica 6 - Servicios Web**

**Descripción:** Esta práctica implementa un servicio web RESTful utilizando Spring Boot. El backend gestiona el inventario de la panadería a través de una API REST que es consumida por un frontend web (HTML, CSS, JavaScript).

**Ubicación de los archivos:** `Práctica 6 - Servicios Web/`

**Requisitos:**
* Tener **MySQL** en ejecución y la base de datos `panaderia` inicializada con `Script.sql`.
* Tener Apache Maven instalado.

**Pasos para la Ejecución:**

1.  Abre una terminal y navega al directorio `Práctica 6 - Servicios Web/`.
2.  **Ejecuta la aplicación Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```
3.  **Accede al servicio:** Abre tu navegador y ve a `http://localhost:8081` para interactuar con la interfaz web.

---

### **Práctica 7 - Arquitectura de Microservicios**

**Descripción:** Esta práctica descompone la aplicación de la panadería en una arquitectura de microservicios. Incluye servicios para inventario, compras, un servidor de descubrimiento (Eureka) y un API Gateway. Todo el sistema se orquesta y despliega utilizando Docker y Docker Compose.

**Ubicación de los archivos:** `Práctica 7 - Microservice_Panaderia/`

**Requisitos:**
* Tener Docker instalado y en ejecución.

**Pasos para la Ejecución:**

1.  Abre una terminal y navega al directorio `Práctica 7 - Microservice_Panaderia/`.
2.  **Levanta todo el sistema con Docker Compose:**
    ```bash
    docker compose up --build
    ```
4.  **Accede a los servicios:**
    * **Frontend del Cliente:** `http://localhost:8084`
    * **Dashboard de Eureka:** `http://localhost:8761`
    * **API Gateway:** `http://localhost:8083`

---

### **Práctica 8 - Progressive Web App (PWA)**

**Descripción:** Esta práctica convierte el frontend de la panadería en una Progressive Web App (PWA). La aplicación es instalable, funciona sin conexión gracias a un Service Worker y recibe actualizaciones de stock en tiempo real mediante Server-Sent Events (SSE).

**Ubicación de los archivos:** `Práctica 8 - PWA_panaderia/`

**Requisitos:**
* Tener **MySQL** en ejecución y la base de datos `panaderia` inicializada.
* Tener Apache Maven instalado.

**Pasos para la Ejecución:**

1.  Abre una terminal y navega al directorio `Práctica 8 - PWA_panaderia/`.
2.  **Ejecuta la aplicación Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```
3.  **Accede a la PWA:** Abre tu navegador y ve a `http://localhost:8081`. Podrás instalar la aplicación desde la barra de direcciones.

### **Implementaciones en la Nube**

A continuación se presentan los enlaces a las aplicaciones desplegadas en diferentes proveedores de nube.

* **Servicio Web (Panadería):**
    * **Azure:** [https://panaderia-app-bhafafb5hja4axew.canadacentral-01.azurewebsites.net/](https://panaderia-app-bhafafb5hja4axew.canadacentral-01.azurewebsites.net/)

* **Microservicio PWA (Cine - Proyecto en equipo):**
    * Este proyecto, al ser también una Aplicación Web Progresiva (PWA), puede ser instalado en dispositivos compatibles para una experiencia similar a una app nativa.
    * **Google Cloud:** [https://storage.googleapis.com/cine-pwa/index.html](https://storage.googleapis.com/cine-pwa/index.html)
    * **Amazon Web Services (AWS):** [https://cine-pwa.s3.us-east-2.amazonaws.com/index.html](https://cine-pwa.s3.us-east-2.amazonaws.com/index.html)
    * **Microsoft Azure:** [https://witty-rock-0406c590f.1.azurestaticapps.net/](https://witty-rock-0406c590f.1.azurestaticapps.net/)

---

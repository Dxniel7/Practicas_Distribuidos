# Prácticas de Sistemas Distribuidos

Este repositorio contiene las soluciones para las prácticas de la asignatura de Sistemas Distribuidos. A continuación, se detalla la estructura del proyecto y las instrucciones para ejecutar las prácticas de la 3 a la 5.

De igual pmanera se proporciona el enlace al repositorio de GitHub. 

https://github.com/Dxniel7/Practicas_Distribuidos

---

### **Consideraciones Generales Importantes:**

* **Conector JDBC de MySQL:** Las prácticas 4 y 5 requieren conectividad con una base de datos MySQL. Es indispensable el **conector JDBC de MySQL** (por ejemplo, `mysql-connector-j-9.2.0.jar` o la versión más reciente).

* **Terminales Separadas:** Para la mayoría de estas prácticas, necesitaremos abrir varias terminales (PowerShell o Símbolo del Sistema en Windows) simultáneamente para ejecutar el servidor de descubrimiento, el servidor de panadería y los clientes.

* **Compilación Previa:** Siempre es fundamental compilar los archivos Java antes de ejecutarlos. Los comandos proporcionados a continuación incluyen la compilación.

* **Cambio en Práctica 5:** Se realizó un cambio en el constructor de `PanaderiaServidorRMI` de `protected` a `public` para permitir su instanciación por `ServidorRMI`. Asegúrate de que tu copia local del código refleje este cambio.

---

### **Práctica 3 - Servidor-Multicliente**

**Descripción:** Esta práctica implementa un servidor de panadería capaz de manejar múltiples clientes concurrentemente utilizando hilos, demostrando la capacidad de un servidor para atender múltiples peticiones.

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
4.  **En otra Terminal (Cliente 1):**
    ```bash
    java PanaderiaCliente
    ```
5.  **En otra Terminal (Cliente 2, etc.):**
    ```bash
    java PanaderiaCliente
    ```
    *(Puedes abrir tantos clientes como desees para probar la concurrencia).*

---

### **Práctica 4 - Cliente-Servidor (Multi-cliente, Multi-servidor con Servidor de Descubrimiento)**

**Descripción:** Esta práctica extiende la arquitectura cliente-servidor para incluir un "Servidor de Descubrimiento", que permite a múltiples servidores de panadería registrarse y a los clientes encontrarlos dinámicamente. La persistencia del stock de pan se gestiona a través de una base de datos MySQL.

**Ubicación de los archivos:** `Práctica 4 - C-S_Panadeeria/`

**Requisito:** **MySQL debe estar ejecutándose** y la base de datos `panaderia` con la tabla `inventario` inicializada. Podemos usar el archivo `Script.sql`.

**Pasos para la Ejecución:**

1.  **Asegurarnos de tener `mysql-connector-j-9.2.0.jar`**.
2.  Abre tu terminal y navega al directorio `Práctica 4 - C-S_Panadeeria/`.
3.  **Compila todas las clases:**
    ```bash
    javac -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaServidor.java PanaderiaCliente.java ServidorDescubrimiento.java
    ```
4.  **En una Terminal (Servidor de Descubrimiento):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" ServidorDescubrimiento 5050
    ```
    *(El puerto `5050` es un ejemplo; asegurarnos de que sea un puerto disponible y que todos los componentes que necesiten conectarse a este servidor lo utilicen. Mantén esta terminal abierta).*
5.  **En otra Terminal (Servidor de Panadería 1):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaServidor 5000
    ```
    *(El puerto `5000` es un ejemplo. Podemos iniciar múltiples instancias de `PanaderiaServidor` en puertos diferentes para simular un entorno distribuido).*
6.  **En otra Terminal (Cliente 1):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaCliente 
    ```
    *(Ejemplo: `java -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaCliente`. El cliente se conectará al servidor de panadería en el puerto especificado, el cual ya se habrá registrado con el servidor de descubrimiento).*

---

### **Práctica 5 - RMI (Java Remote Method Invocation)**

**Descripción:** Esta práctica utiliza Java RMI para la comunicación distribuida, permitiendo que los clientes invoquen métodos en un objeto remoto (la panadería) que reside en el servidor. La gestión del stock también se realiza a través de MySQL.

**Ubicación de los archivos:** `Práctica 5 - RMI_Panaderia/`

**Requisito:** **MySQL debe estar ejecutándose** y la base de datos `panaderia` con la tabla `inventario` inicializada.

**Pasos para la Ejecución:**

1.  **Nuevamente asegurarnos de tener `mysql-connector-j-9.2.0.jar`**.
2.  **Abre tu terminal** y navega al directorio `Práctica 5 - RMI_Panaderia/`.
3.  **Compila todas las clases:**
    ```bash
    javac -cp ".;mysql-connector-j-9.2.0.jar" PanaderiaRMI.java PanaderiaServidorRMI.java ServidorRMI.java ClienteRMI.java
    ```
6.  **En una Terminal (Servidor RMI):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" ServidorRMI
    ```
    *(Esta terminal iniciará el registro RMI en el puerto 1099 (o 1098 si está en uso) y el servidor de panadería. Mantén esta terminal abierta y en ejecución).*
7.  **En otra Terminal (Cliente RMI):**
    ```bash
    java -cp ".;mysql-connector-j-9.2.0.jar" ClienteRMI
    ```
    *(El cliente se conectará al servidor RMI, solicitará tu nombre y te permitirá interactuar con la panadería de forma remota).*

---
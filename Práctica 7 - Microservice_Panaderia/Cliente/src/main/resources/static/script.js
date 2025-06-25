const API_URL = "http://localhost:8083"; // API Gateway

// Obtener el stock disponible y la lista de productos
async function obtenerProductos() {
    try {
        const response = await fetch(`${API_URL}/inventario/productos`);  // Solicitar al inventario-service
        if (response.ok) {
            const productos = await response.json();
            mostrarProductos(productos);
        } else {
            document.getElementById("stock").innerText = "Error al obtener productos";
        }
    } catch (error) {
        document.getElementById("stock").innerText = "No se pudo conectar al servidor";
    }
}

// Mostrar los productos en el frontend
function mostrarProductos(productos) {
    const productosDiv = document.getElementById("productos");
    productosDiv.innerHTML = '';  // Limpiar el contenedor de productos antes de agregar nuevos

    productos.forEach((producto) => {
        const productoDiv = document.createElement("div");
        productoDiv.classList.add("producto");

        productoDiv.innerHTML = `
            <label for="cantidad-${producto.id}">${producto.producto} - Stock: ${producto.stock}</label>
            <input type="number" id="cantidad-${producto.id}" min="0" max="${producto.stock}" value="0" />
        `;

        productosDiv.appendChild(productoDiv);
    });
}

// Realizar la compra de pan
async function comprarPan() {
    const nombre = document.getElementById("nombreCliente").value;
    
    if (!nombre) {
        document.getElementById("mensaje").innerText = "Por favor, ingresa tu nombre.";
        return;
    }

    const compras = [];
    
    // Recopilar los productos y sus cantidades seleccionadas
    const productos = document.querySelectorAll("#productos div");
    productos.forEach((productoDiv) => {
        const productoId = productoDiv.querySelector("input").id.split("-")[1];
        const cantidad = document.getElementById(`cantidad-${productoId}`).value;

        if (cantidad > 0) {
            compras.push({
                producto: productoDiv.querySelector("label").innerText.split(" - ")[0].trim(),
                cantidad: parseInt(cantidad),
            });
        }
    });

    if (compras.length === 0) {
        document.getElementById("mensaje").innerText = "Por favor, selecciona al menos un producto.";
        return;
    }

    // Preparar el JSON para el envío
    const data = {
        nombreCliente: nombre,
        compras: compras,
    };

    try {
        const response = await fetch(`${API_URL}/compras`, {  // Solicitar al compra-service
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data), // Enviar el JSON correctamente formado
        });

        if (response.ok) {
            const mensaje = await response.text();
            document.getElementById("mensaje").innerText = mensaje;
        } else {
            document.getElementById("mensaje").innerText = "Error al realizar la compra";
        }
    } catch (error) {
        document.getElementById("mensaje").innerText = "No se pudo conectar al servidor";
    }

    // Actualizar el stock después de la compra
    obtenerProductos();
}

// Inicializar con los productos disponibles
obtenerProductos();

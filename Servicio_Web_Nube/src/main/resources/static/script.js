const API_URL = "https://panaderia-app-bhafafb5hja4axew.canadacentral-01.azurewebsites.net/panaderia";

// Obtener el stock disponible y la lista de productos
async function obtenerProductos() {
    try {
        const response = await fetch(`${API_URL}/productos`);
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

// Mostrar los productos disponibles en la interfaz
function mostrarProductos(productos) {
    const productosDiv = document.getElementById("productos");
    productosDiv.innerHTML = "";  // Limpiar la lista actual

    productos.forEach((producto) => {
        const productoDiv = document.createElement("div");
        productoDiv.innerHTML = `
            <label>
                ${producto.producto} - Stock: <span id="stock-${producto.id}">${producto.stock}</span> 
                <input type="number" id="cantidad-${producto.id}" placeholder="Cantidad" min="1" max="${producto.stock}">
            </label>
            <br>
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
        const response = await fetch(`${API_URL}/comprar`, {
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

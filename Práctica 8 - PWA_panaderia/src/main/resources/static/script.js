const API_URL = "http://localhost:8081/panaderia";

async function obtenerProductos() {
    try {
        const response = await fetch(`${API_URL}/productos`);
        if (response.ok) {
            const productos = await response.json();
            mostrarProductos(productos);
        } else {
            document.getElementById("mensaje").innerText = "Error al obtener productos";
        }
    } catch (error) {
        document.getElementById("mensaje").innerText = "No se pudo conectar al servidor";
    }
}

function mostrarProductos(productos) {
    const productosDiv = document.getElementById("productos");
    productosDiv.innerHTML = "";

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

async function comprarPan() {
    const nombre = document.getElementById("nombreCliente").value;
    
    if (!nombre) {
        document.getElementById("mensaje").innerText = "Por favor, ingresa tu nombre.";
        return;
    }

    const compras = [];
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

    const data = {
        nombreCliente: nombre,
        compras: compras,
    };

    try {
        const response = await fetch(`${API_URL}/comprar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
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

    obtenerProductos();
}

// Escuchar eventos del servidor para actualizar stock automáticamente
function iniciarSSE() {
    if (!!window.EventSource) {
        const eventSource = new EventSource(`${API_URL}/eventos`);

        eventSource.onmessage = function (event) {
            const mensaje = event.data;
        
            const notificaciones = document.getElementById("notificaciones");
            const p = document.createElement("p");
            p.textContent = mensaje;
            notificaciones.appendChild(p);
        
            // Recargar toda la lista de productos para reflejar stock actualizado
            obtenerProductos();
        
            setTimeout(() => {
                p.remove();
            }, 10000);
        };
        

        eventSource.onerror = function (error) {
            console.error("Error en SSE:", error);
        };
    } else {
        console.warn("SSE no es compatible con este navegador.");
    }
}

obtenerProductos();
iniciarSSE();

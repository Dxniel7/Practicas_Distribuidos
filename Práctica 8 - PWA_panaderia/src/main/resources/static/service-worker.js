const CACHE_NAME = "panaderia-cache-v1";
const urlsToCache = [
  "/",
  "/index.html",
  "/style.css",
  "/script.js",
  "/manifest.json",
  "/icons/icon-192.png",
  "/icons/icon-512.png"
];

// Instalación del Service Worker
self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(urlsToCache);
    })
  );
});

// Activación del Service Worker - limpieza de caché antigua
self.addEventListener("activate", (event) => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (!cacheWhitelist.includes(cacheName)) {
            return caches.delete(cacheName); // Elimina cachés viejas
          }
        })
      );
    })
  );
});

// Manejo de peticiones de red - actualiza la caché en segundo plano
self.addEventListener("fetch", (event) => {
  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      const fetchRequest = event.request.clone();
      
      // Si la respuesta está en caché, la devuelve inmediatamente
      if (cachedResponse) {
        // Actualiza la caché con la nueva respuesta sin interrumpir la experiencia del usuario
        fetch(fetchRequest).then((response) => {
          caches.open(CACHE_NAME).then((cache) => {
            cache.put(event.request, response.clone());
          });
        });
        return cachedResponse;
      }

      // Si no está en caché, realiza la solicitud normalmente
      return fetch(fetchRequest);
    })
  );
});

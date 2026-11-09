# Morphin Alert Ω

Aplicación Android Java clásica, preparada para AIDE.

## Funciones
- Detecta notificaciones publicadas por WhatsApp y WhatsApp Business mediante NotificationListenerService.
- Reproduce una secuencia electrónica sintetizada y una vibración breve.
- Incluye botón de prueba de sonido.
- Incluye WebView auxiliar para abrir WhatsApp Web; el radar Android no depende de ella.
- No lee ni guarda el texto de los mensajes ni el nombre de los contactos.

## Compilar en AIDE
1. Clona o descarga este repositorio.
2. Abre la carpeta raíz como proyecto Android/Gradle.
3. Compila e instala.
4. Abre Morphin Alert Ω y activa el acceso a notificaciones desde el botón correspondiente.
5. Regresa y prueba el sonido.

## Límites
El servicio solo detecta notificaciones que Android publica. Si WhatsApp no genera una notificación, esta aplicación no puede detectar el mensaje. Android no ofrece una confirmación fiable de que el sonido original se haya escuchado. El modo No molestar, el silencio, el volumen y las políticas de batería del fabricante pueden afectar el sonido de respaldo.

WhatsApp Web dentro de WebView es auxiliar y experimental: puede bloquear el inicio de sesión o suspenderse en segundo plano. No se utiliza para leer mensajes.

El tono es una secuencia electrónica original sintetizada; no contiene audio extraído de Power Rangers.

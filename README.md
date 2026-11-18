# Sistema de Gestión para el Taller Automotriz CarMotors

Proyecto Java 17 + MySQL (JDBC) del Grupo 02.


## Estructura inicial

```
proyecto-java-grupo02/
├── src/main/java/com/carmotors/
│   ├── vista/          ← interacción con el usuario por consola
│   ├── controlador/    ← comunica la vista con los servicios
│   ├── modelo/         ← entidades del negocio
│   ├── servicio/       ← reglas de negocio
│   ├── dao/            ← acceso a MySQL con JDBC
│   ├── config/         ← conexión a la base de datos
│   └── excepcion/      ← excepciones personalizadas
├── database/           ← scripts SQL
├── docs/               ← diagramas
├── pom.xml
└── .gitignore
```

## Flujo de trabajo (Git Flow)

- `main`: versiones estables.
- `develop`: integración.
- `feature/<funcionalidad>`: una rama por funcionalidad, creada desde `develop`.

Commits con **Conventional Commits** en inglés (`feat:`, `fix:`, `docs:`, `refactor:`, `chore:`).

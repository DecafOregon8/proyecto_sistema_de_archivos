import java.util.List;
import java.util.Scanner;


public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static ArbolArchivos arbol;

    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE ARCHIVOS (ABB) ===");

        while (arbol == null) {
            String nombreRaiz = leer("Nombre del directorio raíz: ");
            try {
                arbol = new ArbolArchivos(nombreRaiz);
                System.out.println("Directorio raíz '" + nombreRaiz.trim() + "' creado.");
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            System.out.println();
            switch (opcion) {
                case 1: crearDirectorio(); break;
                case 2: insertarArchivo(); break;
                case 3: buscarArchivo(); break;
                case 4: eliminar(); break;
                case 5: mostrarRecorridos(); break;
                case 6: mostrarInformacion(); break;
                case 7: validarReglas(); break;
                case 0: System.out.println("Hasta luego."); break;
                default: System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n----------- MENÚ -----------");
        System.out.println("1. Crear directorio");
        System.out.println("2. Insertar archivo");
        System.out.println("3. Buscar un archivo por nombre");
        System.out.println("4. Eliminar archivo o directorio");
        System.out.println("5. Mostrar contenido (recorridos)");
        System.out.println("6. Mostrar información de un elemento");
        System.out.println("7. Validar reglas del sistema");
        System.out.println("0. Salir");
    }


    private static void crearDirectorio() {
        String nombre = leer("Nombre del directorio: ");
        if (arbol.insertar(Nodo.directorio(nombre))) {
            System.out.println("Directorio '" + nombre.trim() + "' creado.");
        }
    }

    private static void insertarArchivo() {
        String nombre = leer("Nombre del archivo: ");
        String extension = leer("Extensión (ej. pdf): ");
        Long tamano = leerTamano("Tamaño en bytes: ");
        if (tamano == null) {
            return;
        }
        if (arbol.insertar(Nodo.archivo(nombre, extension, tamano))) {
            System.out.println("Archivo '" + nombre.trim() + "' insertado.");
        }
    }

    private static void buscarArchivo() {
        String nombre = leer("Nombre a buscar: ");
        Nodo encontrado = arbol.buscar(nombre);
        if (encontrado == null) {
            System.out.println("El elemento no existe en el sistema de archivos.");
        } else {
            System.out.println("Elemento encontrado");
            System.out.println(encontrado.informacion());
        }
    }

    private static void eliminar() {
        String nombre = leer("Nombre del elemento a eliminar: ");
        arbol.eliminar(nombre);
    }

    private static void mostrarRecorridos() {
        System.out.println("1. Inorden");
        System.out.println("2. Preorden");
        System.out.println("3. Postorden");
        int tipo = leerEntero("Recorrido: ");
        List<Nodo> lista;
        String titulo;
        switch (tipo) {
            case 1: lista = arbol.inorden();    titulo = "INORDEN"; break;
            case 2: lista = arbol.preorden();   titulo = "PREORDEN"; break;
            case 3: lista = arbol.postorden();  titulo = "POSTORDEN"; break;
            default:
                System.out.println("Opción no válida.");
                return;
        }
        System.out.println("\n--- Recorrido " + titulo + " ---");
        for (Nodo n : lista) {
            System.out.println(n);
        }
    }

    private static void mostrarInformacion() {
        String nombre = leer("Nombre del elemento: ");
        Nodo n = arbol.buscar(nombre);
        if (n == null) {
            System.out.println("El elemento no existe en el sistema de archivos.");
            return;
        }
        System.out.println(n.informacion());
        System.out.println("Hijo izquierdo: " + (n.getIzquierdo() == null ? "-" : n.getIzquierdo().getNombre()));
        System.out.println("Hijo derecho: " + (n.getDerecho() == null ? "-" : n.getDerecho().getNombre()));
        if (n == arbol.getRaiz()) {
            System.out.println("(Es el directorio raíz)");
        }
    }

    private static void validarReglas() {
        List<String> problemas = arbol.validarSistema();
        if (problemas.isEmpty()) {
            System.out.println("El sistema de archivos cumple todas las reglas:");
            System.out.println("- La raíz es un directorio.");
            System.out.println("- Los archivos son hojas del árbol.");
            System.out.println("- No hay nombres duplicados y se respeta el orden del ABB.");
            System.out.println("- Todos los elementos tienen datos válidos.");
        } else {
            System.out.println("Se encontraron problemas:");
            for (String p : problemas) {
                System.out.println("- " + p);
            }
        }
    }


    private static String leer(String mensaje) {
        System.out.print(mensaje);
        return sc.hasNextLine() ? sc.nextLine() : "";
    }

    private static int leerEntero(String mensaje) {
        String texto = leer(mensaje).trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Long leerTamano(String mensaje) {
        String texto = leer(mensaje).trim();
        try {
            long valor = Long.parseLong(texto);
            if (valor < 0) {
                System.out.println("ERROR: El tamaño de un archivo debe ser mayor o igual a cero.");
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            System.out.println("ERROR: El tamaño debe ser un número entero.");
            return null;
        }
    }
}

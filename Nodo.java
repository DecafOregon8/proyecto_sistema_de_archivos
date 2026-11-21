/**
 * Nodo del árbol: representa un archivo o un directorio del sistema de archivos.
 */
public class Nodo {

    public enum Tipo {
        DIRECTORIO("Directorio"),
        ARCHIVO("Archivo");

        private final String etiqueta;

        Tipo(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    private final String nombre;
    private final String extension; // vacío para directorios
    private final long tamano;      // en bytes (0 para directorios)
    private final Tipo tipo;

    Nodo izquierdo;
    Nodo derecho;

    public Nodo(String nombre, String extension, long tamano, Tipo tipo) {
        this.nombre = nombre == null ? "" : nombre.trim();
        this.extension = extension == null ? "" : extension.trim();
        this.tamano = tamano;
        this.tipo = tipo;
    }

    /** Crea un directorio (sin extensión y con tamaño 0). */
    public static Nodo directorio(String nombre) {
        return new Nodo(nombre, "", 0, Tipo.DIRECTORIO);
    }

    /** Crea un archivo. */
    public static Nodo archivo(String nombre, String extension, long tamano) {
        return new Nodo(nombre, extension, tamano, Tipo.ARCHIVO);
    }

    public String getNombre() { return nombre; }
    public String getExtension() { return extension; }
    public long getTamano() { return tamano; }
    public Tipo getTipo() { return tipo; }
    public Nodo getIzquierdo() { return izquierdo; }
    public Nodo getDerecho() { return derecho; }

    public boolean esArchivo() { return tipo == Tipo.ARCHIVO; }
    public boolean esDirectorio() { return tipo == Tipo.DIRECTORIO; }
    public boolean esHoja() { return izquierdo == null && derecho == null; }

    private String extensionParaMostrar() {
        return extension.isEmpty() ? "-" : extension;
    }

    /** Información del elemento en el formato pedido. */
    public String informacion() {
        return "Nombre: " + nombre + "\n"
             + "Extensión: " + extensionParaMostrar() + "\n"
             + "Tipo: " + tipo + "\n"
             + "Tamaño: " + tamano + " bytes";
    }

    /** Información en una sola línea (para los recorridos). */
    @Override
    public String toString() {
        return "Nombre: " + nombre
             + " | Extensión: " + extensionParaMostrar()
             + " | Tamaño: " + tamano + " bytes"
             + " | Tipo: " + tipo;
    }
}

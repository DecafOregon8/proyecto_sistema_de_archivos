import java.util.ArrayList;
import java.util.List;

/**
 * Sistema de archivos simulado con un árbol binario de búsqueda (ABB).
 * La clave de comparación es el nombre del elemento (sin distinguir mayúsculas).
 */
public class ArbolArchivos {

    private Nodo raiz;

    /** Inicia el sistema creando el directorio raíz. */
    public ArbolArchivos(String nombreRaiz) {
        String error = validarNombre(nombreRaiz);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        raiz = Nodo.directorio(nombreRaiz.trim());
    }

    public Nodo getRaiz() {
        return raiz;
    }

    // ------------------------------------------------------------------
    // Validaciones
    // ------------------------------------------------------------------

    /** Devuelve un mensaje de error, o null si el nombre es válido. */
    public static String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre del elemento no puede estar vacío.";
        }
        return null;
    }

    /** Valida los datos de un elemento. Devuelve un mensaje de error, o null si todo está bien. */
    public static String validarElemento(Nodo nodo) {
        String error = validarNombre(nodo.getNombre());
        if (error != null) {
            return error;
        }
        if (nodo.esArchivo()) {
            if (nodo.getExtension().isEmpty()) {
                return "Un archivo debe tener extensión.";
            }
            if (nodo.getTamano() < 0) {
                return "El tamaño de un archivo debe ser mayor o igual a cero.";
            }
        } else {
            if (!nodo.getExtension().isEmpty()) {
                return "Un directorio no debe tener extensión.";
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Inserción
    // ------------------------------------------------------------------

    /** Inserta un elemento aplicando los criterios de inserción. Devuelve true si se insertó. */
    public boolean insertar(Nodo nuevo) {
        String error = validarElemento(nuevo);
        if (error != null) {
            System.out.println("ERROR: " + error);
            return false;
        }

        Nodo actual = raiz;
        while (true) {
            int cmp = nuevo.getNombre().compareToIgnoreCase(actual.getNombre());

            if (cmp == 0) {
                System.out.println("ERROR: El elemento ya existe en el sistema de archivos.");
                return false;
            }
            if (actual.esArchivo()) {
                System.out.println("ERROR: No se puede insertar el elemento.");
                System.out.println("Un archivo no puede contener otros elementos.");
                return false;
            }

            // El nodo actual es un directorio: se continúa según el orden alfabético
            if (cmp < 0) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = nuevo;
                    return true;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = nuevo;
                    return true;
                }
                actual = actual.derecho;
            }
        }
    }

    // ------------------------------------------------------------------
    // Búsqueda
    // ------------------------------------------------------------------

    /** Busca un elemento por nombre. Devuelve null si no existe. */
    public Nodo buscar(String nombre) {
        if (nombre == null) {
            return null;
        }
        Nodo actual = raiz;
        while (actual != null) {
            int cmp = nombre.trim().compareToIgnoreCase(actual.getNombre());
            if (cmp == 0) {
                return actual;
            }
            actual = (cmp < 0) ? actual.izquierdo : actual.derecho;
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Eliminación
    // ------------------------------------------------------------------

    /** Elimina un archivo o directorio por nombre. Devuelve true si se eliminó. */
    public boolean eliminar(String nombre) {
        String error = validarNombre(nombre);
        if (error != null) {
            System.out.println("ERROR: " + error);
            return false;
        }
        if (nombre.trim().equalsIgnoreCase(raiz.getNombre())) {
            System.out.println("ERROR: El directorio raíz no puede eliminarse.");
            return false;
        }

        Nodo objetivo = buscar(nombre);
        if (objetivo == null) {
            System.out.println("El elemento no existe en el sistema de archivos.");
            return false;
        }

        // Con dos hijos, el reemplazo debe ser un directorio; si fuera un archivo
        // quedaría con hijos y se rompería la regla de que los archivos son hojas.
        if (objetivo.izquierdo != null && objetivo.derecho != null
                && !minimo(objetivo.derecho).esDirectorio()
                && !maximo(objetivo.izquierdo).esDirectorio()) {
            System.out.println("ERROR: No se puede eliminar el directorio '" + objetivo.getNombre() + "'.");
            System.out.println("Su reemplazo sería un archivo y un archivo no puede tener hijos.");
            System.out.println("Elimine primero los archivos que lo rodean.");
            return false;
        }

        raiz = eliminarRec(raiz, nombre.trim());
        System.out.println("Elemento '" + objetivo.getNombre() + "' eliminado.");
        return true;
    }

    private Nodo eliminarRec(Nodo actual, String nombre) {
        if (actual == null) {
            return null;
        }
        int cmp = nombre.compareToIgnoreCase(actual.getNombre());
        if (cmp < 0) {
            actual.izquierdo = eliminarRec(actual.izquierdo, nombre);
        } else if (cmp > 0) {
            actual.derecho = eliminarRec(actual.derecho, nombre);
        } else {
            // Situación 1 y 2: hoja o un solo hijo
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            if (actual.derecho == null) {
                return actual.izquierdo;
            }
            // Situación 3: dos hijos -> sucesor inorden (o predecesor si el sucesor es archivo)
            Nodo sucesor = minimo(actual.derecho);
            if (sucesor.esDirectorio()) {
                Nodo nuevaDerecha = eliminarMinimo(actual.derecho);
                sucesor.izquierdo = actual.izquierdo;
                sucesor.derecho = nuevaDerecha;
                return sucesor;
            }
            Nodo predecesor = maximo(actual.izquierdo);
            Nodo nuevaIzquierda = eliminarMaximo(actual.izquierdo);
            predecesor.izquierdo = nuevaIzquierda;
            predecesor.derecho = actual.derecho;
            return predecesor;
        }
        return actual;
    }

    private Nodo minimo(Nodo nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    private Nodo maximo(Nodo nodo) {
        while (nodo.derecho != null) {
            nodo = nodo.derecho;
        }
        return nodo;
    }

    /** Quita el nodo mínimo del subárbol y devuelve la nueva raíz del subárbol. */
    private Nodo eliminarMinimo(Nodo nodo) {
        if (nodo.izquierdo == null) {
            return nodo.derecho;
        }
        nodo.izquierdo = eliminarMinimo(nodo.izquierdo);
        return nodo;
    }

    /** Quita el nodo máximo del subárbol y devuelve la nueva raíz del subárbol. */
    private Nodo eliminarMaximo(Nodo nodo) {
        if (nodo.derecho == null) {
            return nodo.izquierdo;
        }
        nodo.derecho = eliminarMaximo(nodo.derecho);
        return nodo;
    }

    // ------------------------------------------------------------------
    // Recorridos
    // ------------------------------------------------------------------

    public List<Nodo> inorden() {
        List<Nodo> resultado = new ArrayList<>();
        inorden(raiz, resultado);
        return resultado;
    }

    private void inorden(Nodo nodo, List<Nodo> salida) {
        if (nodo == null) return;
        inorden(nodo.izquierdo, salida);
        salida.add(nodo);
        inorden(nodo.derecho, salida);
    }

    public List<Nodo> preorden() {
        List<Nodo> resultado = new ArrayList<>();
        preorden(raiz, resultado);
        return resultado;
    }

    private void preorden(Nodo nodo, List<Nodo> salida) {
        if (nodo == null) return;
        salida.add(nodo);
        preorden(nodo.izquierdo, salida);
        preorden(nodo.derecho, salida);
    }

    public List<Nodo> postorden() {
        List<Nodo> resultado = new ArrayList<>();
        postorden(raiz, resultado);
        return resultado;
    }

    private void postorden(Nodo nodo, List<Nodo> salida) {
        if (nodo == null) return;
        postorden(nodo.izquierdo, salida);
        postorden(nodo.derecho, salida);
        salida.add(nodo);
    }

    // ------------------------------------------------------------------
    // Validación general del sistema
    // ------------------------------------------------------------------

    /** Revisa que todo el árbol cumpla las reglas. Devuelve la lista de problemas (vacía si es válido). */
    public List<String> validarSistema() {
        List<String> problemas = new ArrayList<>();

        if (raiz == null || !raiz.esDirectorio()) {
            problemas.add("La raíz debe ser un directorio.");
        }

        List<Nodo> orden = inorden();
        for (int i = 0; i < orden.size(); i++) {
            Nodo n = orden.get(i);

            String error = validarElemento(n);
            if (error != null) {
                problemas.add("'" + n.getNombre() + "': " + error);
            }
            if (n.esArchivo() && !n.esHoja()) {
                problemas.add("'" + n.getNombre() + "': un archivo debe ser una hoja del árbol.");
            }
            // Orden estricto en inorden => se cumple el ABB y no hay nombres duplicados
            if (i > 0 && orden.get(i - 1).getNombre().compareToIgnoreCase(n.getNombre()) >= 0) {
                problemas.add("'" + n.getNombre() + "': rompe el orden del ABB o está duplicado.");
            }
        }
        return problemas;
    }
}

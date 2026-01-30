package nameshortener;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class NameShortener {
	
    @ConfigProperty(name = "card.name-character-limit")
    int MAX_CHAR_LIMIT;
   
    private static final String INITIAL_REGEX = "^[A-ZÁÉÍÓÚÑ]\\.\\s*$|^[A-ZÁÉÍÓÚÑ]\\.$";
    private static final Locale LOCALE = Locale.ROOT;

    /**
     * Método que invocará el endpoint para acortar los nombres
     */
    public List<String> cardNameShortener(String givenNames, String surname1, String surname2) {


    			// --- Limpieza básica + quitar tildes y caracteres especiales ---

				String cleanedGivenNames = sanitizeName(givenNames);
				String primarySurname    = sanitizeName(surname1);
				String secondarySurname  = sanitizeName(surname2);
				
				// --- Tokenización de nombres ---
				List<String> givenNameTokens = tokenize(cleanedGivenNames);
				
				String firstGiven = givenNameTokens.isEmpty() ? "" : givenNameTokens.get(0);
				List<String> additionalGivens = extractAdditionalGivens(givenNameTokens);


    	        // --- Helpers ---
    	        Function<String, String> toInitial = this::toInitial;
    	        Function<List<String>, String> joinWithSpaces = this::joinWithSpaces;

    	        // ============== VARIANTE 1 =========

				String v1 = buildVariant1(
				        firstGiven, additionalGivens,
				        primarySurname, secondarySurname,
				        toInitial, joinWithSpaces
				);


    	        // ============ VARIANTE 2 ==============

				String v2 = buildVariant2(
				        givenNameTokens,
				        primarySurname, secondarySurname,
				        toInitial, joinWithSpaces
				);


    	        // ============== VARIANTE 3 ============

				String v3 = buildVariant3(
				        firstGiven, additionalGivens,
				        primarySurname, secondarySurname,
				        toInitial, joinWithSpaces
				);

    	        // Comprobación final: nunca superar límite y poner en mayus (MISMA LÓGICA)
    	        v1 = trimToLimit(v1).toUpperCase(LOCALE);
    	        v2 = trimToLimit(v2).toUpperCase(LOCALE);
    	        v3 = trimToLimit(v3).toUpperCase(LOCALE);

    	        return List.of(v1, v2, v3);
    	    }
    

    // ========================= VARIANTE 1 =========================		
    private String buildVariant1(
            String firstGiven,
            List<String> additionalGivens,
            String primarySurname,
            String secondarySurname,
            Function<String, String> toInitial,
            Function<List<String>, String> joinWithSpaces
    ) {
        // Prioriza: Primer Nombre (full) + Primer Apellido (full)
        // Composición inicial:
        //  - firstGiven (full)
        //  - adicionales: iniciales
        //  - surname1 (full)
        //  - surname2 (full)
        List<String> parts = new ArrayList<>();
        int idxFirstGiven, idxSurname1, idxSurname2;

        parts.add(firstGiven);
        idxFirstGiven = parts.size() - 1;

        List<Integer> idxAdditional = new ArrayList<>();
        for (String g : additionalGivens) {
            parts.add(toInitial.apply(g));
            idxAdditional.add(parts.size() - 1);
        }

        parts.add(primarySurname);
        idxSurname1 = parts.size() - 1;

        parts.add(secondarySurname);
        idxSurname2 = parts.size() - 1;

        String value = joinWithSpaces.apply(parts);

        if (value.length() > MAX_CHAR_LIMIT) {
            // 1) surname2 -> inicial
            parts.set(idxSurname2, toInitial.apply(secondarySurname));
            value = joinWithSpaces.apply(parts);
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 2) eliminar iniciales extra de nombres adicionales (desde el final)
            for (int i = idxAdditional.size() - 1; i >= 0 && value.length() > MAX_CHAR_LIMIT; i--) {
                int idx = idxAdditional.get(i);
                parts.remove(idx);

                // actualizar índices posteriores
                if (idx < idxSurname1) idxSurname1--;
                if (idx < idxSurname2) idxSurname2--;

                value = joinWithSpaces.apply(parts);
            }
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 3) firstGiven -> inicial
            parts.set(idxFirstGiven, toInitial.apply(firstGiven));
            value = joinWithSpaces.apply(parts);
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 4) surname1 -> inicial
            parts.set(idxSurname1, toInitial.apply(primarySurname));
            value = joinWithSpaces.apply(parts);
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 5) último recurso: eliminar más iniciales si quedara alguno
            for (int i = parts.size() - 1; i >= 0 && value.length() > MAX_CHAR_LIMIT; i--) {
                String token = parts.get(i);
                if (token != null && token.matches(INITIAL_REGEX)) {
                    parts.remove(i);

                    // reajustar índices básicos si hace falta
                    if (i < idxSurname1) idxSurname1--;
                    if (i < idxSurname2) idxSurname2--;
                    if (i < idxFirstGiven) idxFirstGiven--;

                    value = joinWithSpaces.apply(parts);
                }
            }
        }

        return joinWithSpaces.apply(parts);
    }

    // =================== VARIANTE 2 =============================		
    private String buildVariant2(
            List<String> givenNameTokens,
            String primarySurname,
            String secondarySurname,
            Function<String, String> toInitial,
            Function<List<String>, String> joinWithSpaces
    ) {
        // Prioriza: Segundo nombre (o el más largo si hay más de uno) + Primer Apellido
        int preferredGivenIdx = determinePreferredGivenIndex(givenNameTokens); // detemina que nombre se va a priorizar

        List<String> parts = new ArrayList<>();
        List<Integer> idxGiven = new ArrayList<>();

        for (int i = 0; i < givenNameTokens.size(); i++) {
            String g = givenNameTokens.get(i);
            if (i == preferredGivenIdx) {
                parts.add(g); // preferido en full
            } else {
                parts.add(toInitial.apply(g)); // el resto iniciales
            }
            idxGiven.add(parts.size() - 1);
        }

        int idxSurname1, idxSurname2;
        int idxPreferredGiven = idxGiven.isEmpty() ? -1 : idxGiven.get(preferredGivenIdx);

        parts.add(primarySurname);
        idxSurname1 = parts.size() - 1;

        parts.add(secondarySurname);
        idxSurname2 = parts.size() - 1;

        String value = joinWithSpaces.apply(parts);

        if (value.length() > MAX_CHAR_LIMIT) {
            // 1) surname2 -> inicial
            parts.set(idxSurname2, toInitial.apply(secondarySurname));
            value = joinWithSpaces.apply(parts);
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 2) eliminar iniciales de nombres no preferidos (desde el final)
            for (int i = idxGiven.size() - 1; i >= 0 && value.length() > MAX_CHAR_LIMIT; i--) {
                int idx = idxGiven.get(i);
                if (idx != idxPreferredGiven) {
                    parts.remove(idx);

                    // reajustar índices por desplazamiento
                    if (idx < idxSurname1) idxSurname1--;
                    if (idx < idxSurname2) idxSurname2--;
                    if (idx < idxPreferredGiven) idxPreferredGiven--;

                    for (int j = 0; j < idxGiven.size(); j++) {
                        if (idxGiven.get(j) > idx) idxGiven.set(j, idxGiven.get(j) - 1);
                    }

                    value = joinWithSpaces.apply(parts);
                }
            }
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 3) preferido -> inicial (mantener surname1)
            if (idxPreferredGiven >= 0) {
                String preferredOriginal = givenNameTokens.get(preferredGivenIdx);
                parts.set(idxPreferredGiven, toInitial.apply(preferredOriginal));
                value = joinWithSpaces.apply(parts);
            }
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 4) surname1 -> inicial
            parts.set(idxSurname1, toInitial.apply(primarySurname));
            value = joinWithSpaces.apply(parts);
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 5) último recurso: eliminar iniciales sobrantes si quedara alguna
            for (int i = parts.size() - 1; i >= 0 && value.length() > MAX_CHAR_LIMIT; i--) {
                String token = parts.get(i);
                if (token != null && token.matches(INITIAL_REGEX)) {
                    parts.remove(i);
                    if (i < idxSurname1) idxSurname1--;
                    if (i < idxSurname2) idxSurname2--;
                    if (i < idxPreferredGiven) idxPreferredGiven--;
                    value = joinWithSpaces.apply(parts);
                }
            }
        }

        return joinWithSpaces.apply(parts);
    }

    private int determinePreferredGivenIndex(List<String> givenNameTokens) {
        int preferredGivenIdx;

        if (givenNameTokens.isEmpty()) {
            preferredGivenIdx = -1; // no hay nombres
        } else if (givenNameTokens.size() == 1) {
            preferredGivenIdx = 0;  // solo uno
        } else {
            // Hay al menos 2: el "más largo" NO puede ser el primero.
            int maxLen = -1;
            int maxIdx = 1; // por defecto el segundo (existe)

            for (int i = 1; i < givenNameTokens.size(); i++) {
                String t = givenNameTokens.get(i);
                if (t == null) continue;
                t = t.trim();
                if (t.isEmpty()) continue;

                int len = t.length();
                if (len > maxLen) {
                    maxLen = len;
                    maxIdx = i;
                }
            }
            preferredGivenIdx = maxIdx; // siempre >= 1
        }

        return preferredGivenIdx;
    }

    // ============================ VARIANTE 3 ========================		
    private String buildVariant3(
            String firstGiven,
            List<String> additionalGivens,
            String primarySurname,
            String secondarySurname,
            Function<String, String> toInitial,
            Function<List<String>, String> joinWithSpaces
    ) {
        // Nombre en inicial + apellidos completos; si no caben, apellidos a iniciales hasta cumplir 24
        List<String> parts = new ArrayList<>();

        if (!firstGiven.isBlank()) parts.add(toInitial.apply(firstGiven));
        for (String g : additionalGivens) parts.add(toInitial.apply(g));

        int idxSurname1At, idxSurname2At;

        parts.add(primarySurname);
        idxSurname1At = parts.size() - 1;

        parts.add(secondarySurname);
        idxSurname2At = parts.size() - 1;

        String value = joinWithSpaces.apply(parts);

        if (value.length() > MAX_CHAR_LIMIT) {
            // 1) surname2 -> inicial
            parts.set(idxSurname2At, toInitial.apply(secondarySurname));
            value = joinWithSpaces.apply(parts);
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 2) surname1 -> inicial
            parts.set(idxSurname1At, toInitial.apply(primarySurname));
            value = joinWithSpaces.apply(parts);
        }

        if (value.length() > MAX_CHAR_LIMIT) {
            // 3) eliminar iniciales de nombres adicionales (desde el final)
            for (int i = parts.size() - 1; i >= 0 && value.length() > MAX_CHAR_LIMIT; i--) {
                String token = parts.get(i);
                // eliminar sólo iniciales de nombres (no apellidos)
                if (i < idxSurname1At && token != null && token.matches(INITIAL_REGEX)) {
                    parts.remove(i);
                    idxSurname1At--;
                    idxSurname2At--;
                    value = joinWithSpaces.apply(parts);
                }
            }
        }

        return joinWithSpaces.apply(parts);
    }

    
	// ========================= HELPERS ==========================

	/**
	 * Divide una cadena de nombres ya limpiada en tokens separados por espacio.
	 * Si la cadena está vacía o en blanco, devuelve una lista vacía.
	 */
    private List<String> tokenize(String cleanedGivenNames) {
        return cleanedGivenNames.isBlank()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(cleanedGivenNames.split(" ")));
    }

	/**
	 * Devuelve todos los nombres excepto el primero.
	 * Si solo hay un nombre (o ninguno), devuelve una lista vacía.
	 */
    private List<String> extractAdditionalGivens(List<String> givenNameTokens) {
        return givenNameTokens.size() <= 1
                ? new ArrayList<>()
                : new ArrayList<>(givenNameTokens.subList(1, givenNameTokens.size()));
    }

	/**
	 * Convierte un token en su inicial seguida de un punto (por ejemplo, "Juan" → "J.").
	 * Si el token es nulo o está vacío, devuelve una cadena vacía.
	 */
    private String toInitial(String token) {
        return (token == null || token.isBlank())
                ? ""
                : token.substring(0, 1).toUpperCase(LOCALE) + ".";
    }

	/**
	 * Une una lista de cadenas no vacías en una sola, separadas por espacios.
	 * Ignora elementos nulos o en blanco.
	 */
    private String joinWithSpaces(List<String> parts) {
        return parts.stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
    }
    
	/**
	 * Elimina todos los caracteres raros de los nombres/apellidos que le lleguen
	 */
	private String sanitizeName(String input) {

		// Control de nulos
	    if (input == null) return "";

	    // 1) Quitar grupos deSe pue espacios primero
	    String s = input.trim().replaceAll("\\s+", " ");
	    if (s.isEmpty()) return "";

	    // 2) Normaliza (separa letras de diacríticos: á -> a +  ́)
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);

	    // 3) Elimina marcas diacríticas (categoría Unicode "Mark")
	    s = s.replaceAll("\\p{M}+", "");

	    // 4) Elimina caracteres especiales (deja letras, dígitos y espacios)
		s = s.replace('-', ' ').replace('–', ' ').replace('—', ' ');
		s = s.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit} ]+", "");


	    // 5) Re-colapsa espacios y trim final
	    s = s.trim().replaceAll("\\s+", " ");

	    return s;
	}
	
	/**
	 * Recorta un String para que no supere el límite MAX_CHAR_LIMIT.
	 * - Si el String es null, devuelve "" (cadena vacía).
	 * - Si ya está dentro del límite, lo devuelve tal cual (sin modificar).
	 * - Si se pasa del límite, lo recorta a los primeros 'limit' caracteres
	 *   y aplica trim() para evitar espacios al principio/fin por el recorte.
	 */
	private String trimToLimit(String result) {

	    if (result == null) return "";
	    int limit = Math.max(0, MAX_CHAR_LIMIT);
	    if (result.length() <= limit) return result;
	    return result.substring(0, limit).trim();
	}
}


/*
Resumen:
- Genera 3 variantes garantizando que la longitud total (incluyendo espacios y puntos) no supere MAX_CHAR_LIMIT (config: card.name-character-limit).
- Prioridad al recortar (de completo a inicial): 1º primer apellido, 2º primer nombre; el resto se degradan antes.
- V1: prioriza primer nombre y primer apellido; si excede, convierte otros a iniciales, luego 2º apellido a inicial, después primer nombre a inicial y, en último lugar, primer apellido a inicial.
- V2: prioriza el segundo (o el más largo) nombre y el primer apellido; aplica la misma degradación hasta cumplir el límite.
- V3: nombre en inicial + apellidos completos; si no cabe, pasa apellidos a iniciales hasta entrar en el límite.
- Último recurso: elimina iniciales de nombres adicionales si aún excede.
*/
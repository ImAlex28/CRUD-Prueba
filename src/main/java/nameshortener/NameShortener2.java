package nameshortener;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class NameShortener2 {
	
    @ConfigProperty(name = "card.name-character-limit")
    int MAX_CHAR_LIMIT;
      
	/**
	 * Método privado para eliminar todos los caracteres raros de los nombres/apellidos que le lleguen
	 * @param input
	 * @return
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

	    //    Control de nulos
	    if (result == null) return "";

	    //    Asegurar que el límite no sea negativo
	    int limit = Math.max(0, MAX_CHAR_LIMIT);

	    //    Si el String ya cabe en el límite (longitud menor o igual),
	    //    devolvemos el String original sin recortarlo.
	    if (result.length() <= limit) return result;

	    // 4) Si excede el límite:
	    //    substring(0, limit) devuelve los primeros caracteres:
	    //      - beginIndex (0) INCLUIDO
	    //      - endIndex (limit) EXCLUIDO
	    //    Luego trim() elimina espacios al principio y al final.
	    return result.substring(0, limit).trim();
	}

    /**
     * Método que invocará el endpoint para acortar los nombres
     */
    public List<String> cardNameShortener(String givenNames, String surname1, String surname2) {

    	// --- Limpieza básica + sanitización (sin tildes ni caracteres especiales) ---
    	String cleanedGivenNames = sanitizeName(givenNames);
    	String primarySurname    = sanitizeName(surname1);
    	String secondarySurname  = sanitizeName(surname2);

        // --- Tokenización de nombres ---
        List<String> givenNameTokens = cleanedGivenNames.isBlank()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(cleanedGivenNames.split(" ")));

        String firstGiven = givenNameTokens.isEmpty() ? "" : givenNameTokens.get(0);
        List<String> additionalGivens = givenNameTokens.size() <= 1
                ? new ArrayList<>()
                : new ArrayList<>(givenNameTokens.subList(1, givenNameTokens.size()));

        // --- Helpers ---
        Function<String, String> toInitial =
                token -> (token == null || token.isBlank()) ? "" : token.substring(0, 1).toUpperCase(Locale.ROOT) + ".";

        Function<List<String>, String> joinWithSpaces =
                parts -> parts.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(" "));

        // Utilidad para ajustar a límite degradando según el orden recibido
        Function<List<String>, String> fitToLimit = parts -> {
            // Clonar lista para modificaciones
            List<String> current = new ArrayList<>(parts);
            String joined = joinWithSpaces.apply(current);
            if (joined.length() <= MAX_CHAR_LIMIT) return joined;

            // Esta función acorta una posición dada "full" -> "initial" si procede
            // Devuelve true si ha aplicado un cambio
            java.util.function.BiFunction<Integer, String, Boolean> shortenIndexToInitial = (idx, original) -> {
                if (idx == null || idx < 0 || idx >= current.size()) return false;
                String val = current.get(idx);
                if (val == null || val.isBlank()) return false;
                // Ya es inicial?
                String expectedInitial = toInitial.apply(original);
                if (val.equals(expectedInitial)) return false;
                // Acortar a inicial
                current.set(idx, expectedInitial);
                return true;
            };

            // Quitar una posición (usado como último recurso para iniciales de nombres extra)
            java.util.function.Function<Integer, Boolean> removeIndex = idx -> {
                if (idx == null || idx < 0 || idx >= current.size()) return false;
                current.remove((int) idx);
                return true;
            };

            return joinWithSpaces.apply(current).length() <= MAX_CHAR_LIMIT
                    ? joinWithSpaces.apply(current)
                    : joinWithSpaces.apply(current); // valor inicial por si no hay reglas
        };

        // ============== VARIANTE 1 ==============
        // Prioriza: Primer Nombre (full) + Primer Apellido (full)
        // Composición inicial:
        //  - firstGiven (full)
        //  - adicionales: iniciales
        //  - surname1 (full)
        //  - surname2 (full)
        List<String> v1Parts = new ArrayList<>();
        int idxV1_firstGiven, idxV1_surname1, idxV1_surname2;
        v1Parts.add(firstGiven);                       idxV1_firstGiven = v1Parts.size() - 1;
        List<Integer> idxV1_additional = new ArrayList<>();
        for (String g : additionalGivens) { v1Parts.add(toInitial.apply(g)); idxV1_additional.add(v1Parts.size() - 1); }
        v1Parts.add(primarySurname);                  idxV1_surname1 = v1Parts.size() - 1;
        v1Parts.add(secondarySurname);                idxV1_surname2 = v1Parts.size() - 1;

        String v1 = joinWithSpaces.apply(v1Parts);
        if (v1.length() > MAX_CHAR_LIMIT) {
            // Degradación por prioridad (mantener surname1, luego firstGiven):
            // 1) surname2 -> inicial
            v1Parts.set(idxV1_surname2, toInitial.apply(secondarySurname));
            v1 = joinWithSpaces.apply(v1Parts);
        }
        if (v1.length() > MAX_CHAR_LIMIT) {
            // 2) (ya están iniciales) -> eliminar iniciales extra de nombres adicionales (desde el final) si sigue pasando
            for (int i = idxV1_additional.size() - 1; i >= 0 && v1.length() > MAX_CHAR_LIMIT; i--) {
                int idx = idxV1_additional.get(i);
                v1Parts.remove(idx);
                // actualizar índices posteriores
                if (idx < idxV1_surname1) idxV1_surname1--;
                if (idx < idxV1_surname2) idxV1_surname2--;
                v1 = joinWithSpaces.apply(v1Parts);
            }
        }
        if (v1.length() > MAX_CHAR_LIMIT) {
            // 3) firstGiven -> inicial
            v1Parts.set(idxV1_firstGiven, toInitial.apply(firstGiven));
            v1 = joinWithSpaces.apply(v1Parts);
        }
        if (v1.length() > MAX_CHAR_LIMIT) {
            // 4) surname1 -> inicial (último en degradar según prioridad)
            v1Parts.set(idxV1_surname1, toInitial.apply(primarySurname));
            v1 = joinWithSpaces.apply(v1Parts);
        }
        if (v1.length() > MAX_CHAR_LIMIT) {
            // 5) último recurso: si sigue excediendo, eliminar más iniciales de nombres adicionales si quedara alguno
            // (en este punto sólo quedarían initials y apellidos posiblemente en initial)
            // Se intenta eliminar desde el final para no afectar "primer nombre" si quedara.
            for (int i = v1Parts.size() - 1; i >= 0 && v1.length() > MAX_CHAR_LIMIT; i--) {
                String token = v1Parts.get(i);
                if (token != null && token.matches("^[A-ZÁÉÍÓÚÑ]\\.\\s*$|^[A-ZÁÉÍÓÚÑ]\\.$")) {
                    v1Parts.remove(i);
                    // reajustar índices básicos si hace falta
                    if (i < idxV1_surname1) idxV1_surname1--;
                    if (i < idxV1_surname2) idxV1_surname2--;
                    if (i < idxV1_firstGiven) idxV1_firstGiven--;
                    v1 = joinWithSpaces.apply(v1Parts);
                }
            }
        }

        // ============== VARIANTE 2 ==============
        // Prioriza: Segundo nombre (o el más largo si hay más de uno) + Primer Apellido

		int preferredGivenIdx;
		
		if (givenNameTokens.isEmpty()) {
		    preferredGivenIdx = -1; // no hay nombres
		} else if (givenNameTokens.size() == 1) {
		    preferredGivenIdx = 0;  // solo uno: necesariamente el primero
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


        List<String> v2Parts = new ArrayList<>();
        List<Integer> idxV2_given = new ArrayList<>();
        for (int i = 0; i < givenNameTokens.size(); i++) {
            String g = givenNameTokens.get(i);
            if (i == preferredGivenIdx) {
                v2Parts.add(g); // preferido en full
            } else {
                v2Parts.add(toInitial.apply(g)); // el resto iniciales
            }
            idxV2_given.add(v2Parts.size() - 1);
        }
        int idxV2_surname1, idxV2_surname2, idxV2_preferredGiven = idxV2_given.isEmpty() ? -1 : idxV2_given.get(preferredGivenIdx);
        v2Parts.add(primarySurname);   idxV2_surname1 = v2Parts.size() - 1;
        v2Parts.add(secondarySurname); idxV2_surname2 = v2Parts.size() - 1;

        String v2 = joinWithSpaces.apply(v2Parts);
        if (v2.length() > MAX_CHAR_LIMIT) {
            // 1) surname2 -> inicial
            v2Parts.set(idxV2_surname2, toInitial.apply(secondarySurname));
            v2 = joinWithSpaces.apply(v2Parts);
        }
        if (v2.length() > MAX_CHAR_LIMIT) {
            // 2) eliminar iniciales de nombres no preferidos (desde el final)
            for (int i = idxV2_given.size() - 1; i >= 0 && v2.length() > MAX_CHAR_LIMIT; i--) {
                int idx = idxV2_given.get(i);
                if (idx != idxV2_preferredGiven) {
                    v2Parts.remove(idx);
                    // reajustar índices por desplazamiento
                    if (idx < idxV2_surname1) idxV2_surname1--;
                    if (idx < idxV2_surname2) idxV2_surname2--;
                    if (idx < idxV2_preferredGiven) idxV2_preferredGiven--;
                    for (int j = 0; j < idxV2_given.size(); j++) {
                        if (idxV2_given.get(j) > idx) idxV2_given.set(j, idxV2_given.get(j) - 1);
                    }
                    v2 = joinWithSpaces.apply(v2Parts);
                }
            }
        }
        if (v2.length() > MAX_CHAR_LIMIT) {
            // 3) preferido -> inicial (mantener surname1)
            if (idxV2_preferredGiven >= 0) {
                String preferredOriginal = givenNameTokens.get(preferredGivenIdx);
                v2Parts.set(idxV2_preferredGiven, toInitial.apply(preferredOriginal));
                v2 = joinWithSpaces.apply(v2Parts);
            }
        }
        if (v2.length() > MAX_CHAR_LIMIT) {
            // 4) surname1 -> inicial (último en degradar según prioridad global)
            v2Parts.set(idxV2_surname1, toInitial.apply(primarySurname));
            v2 = joinWithSpaces.apply(v2Parts);
        }
        if (v2.length() > MAX_CHAR_LIMIT) {
            // 5) último recurso: eliminar iniciales sobrantes si quedara alguna
            for (int i = v2Parts.size() - 1; i >= 0 && v2.length() > MAX_CHAR_LIMIT; i--) {
                String token = v2Parts.get(i);
                if (token != null && token.matches("^[A-ZÁÉÍÓÚÑ]\\.\\s*$|^[A-ZÁÉÍÓÚÑ]\\.$")) {
                    v2Parts.remove(i);
                    if (i < idxV2_surname1) idxV2_surname1--;
                    if (i < idxV2_surname2) idxV2_surname2--;
                    if (i < idxV2_preferredGiven) idxV2_preferredGiven--;
                    v2 = joinWithSpaces.apply(v2Parts);
                }
            }
        }

        // ============== VARIANTE 3 ==============
        // Nombre en inicial + apellidos completos; si no caben, apellidos a iniciales hasta cumplir 24
        List<String> v3Parts = new ArrayList<>();
        if (!firstGiven.isBlank()) v3Parts.add(toInitial.apply(firstGiven));
        for (String g : additionalGivens) v3Parts.add(toInitial.apply(g)); 
        int idxV3_surname1At, idxV3_surname2At;
        v3Parts.add(primarySurname);   idxV3_surname1At = v3Parts.size() - 1;
        v3Parts.add(secondarySurname); idxV3_surname2At = v3Parts.size() - 1;

        String v3 = joinWithSpaces.apply(v3Parts);
        if (v3.length() > MAX_CHAR_LIMIT) {
            // 1) surname2 -> inicial
            v3Parts.set(idxV3_surname2At, toInitial.apply(secondarySurname));
            v3 = joinWithSpaces.apply(v3Parts);
        }
        if (v3.length() > MAX_CHAR_LIMIT) {
            // 2) surname1 -> inicial
            v3Parts.set(idxV3_surname1At, toInitial.apply(primarySurname));
            v3 = joinWithSpaces.apply(v3Parts);
        }
        if (v3.length() > MAX_CHAR_LIMIT) {
            // 3) eliminar iniciales de nombres adicionales (desde el final)
            for (int i = v3Parts.size() - 1; i >= 0 && v3.length() > MAX_CHAR_LIMIT; i--) {
                String token = v3Parts.get(i);
                // eliminar sólo iniciales de nombres (no apellidos)
                if (i < idxV3_surname1At && token != null && token.matches("^[A-ZÁÉÍÓÚÑ]\\.\\s*$|^[A-ZÁÉÍÓÚÑ]\\.$")) {
                    v3Parts.remove(i);
                    idxV3_surname1At--; idxV3_surname2At--;
                    v3 = joinWithSpaces.apply(v3Parts);
                }
            }
        }

        // Comprobación final: nunca superar límite y poner en mayus
		v1 = trimToLimit(joinWithSpaces.apply(v1Parts)).toUpperCase(Locale.ROOT);
		v2 = trimToLimit(joinWithSpaces.apply(v2Parts)).toUpperCase(Locale.ROOT);
		v3 = trimToLimit(joinWithSpaces.apply(v3Parts)).toUpperCase(Locale.ROOT);



        return List.of(v1, v2, v3);
    }

}


/*
Resumen:
- Genera 3 variantes garantizando que la longitud total (incluyendo espacios y puntos) no supere MAX_CHAR_LIMIT (config: card.name-character-limit).
- Prioridad al recortar (de full a inicial): 1º primer apellido, 2º primer nombre; el resto se degradan antes.
- V1: prioriza primer nombre y primer apellido; si excede, convierte otros a iniciales, luego 2º apellido a inicial, después primer nombre a inicial y, en último lugar, primer apellido a inicial.
- V2: prioriza el segundo (o el más largo) nombre y el primer apellido; aplica la misma degradación hasta cumplir el límite.
- V3: nombre en inicial + apellidos completos; si no cabe, pasa apellidos a iniciales hasta entrar en el límite.
- Último recurso: elimina iniciales de nombres adicionales si aún excede.
*/
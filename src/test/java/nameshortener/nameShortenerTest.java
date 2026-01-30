package nameshortener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NameShortenerTest {

    private NameShortener sut;

    @BeforeEach
    void setUp() throws Exception {
        sut = new NameShortener();
        setMaxCharLimit(sut, 24);
    }

    private static void setMaxCharLimit(NameShortener target, int limit) throws Exception {
        Field f = NameShortener.class.getDeclaredField("MAX_CHAR_LIMIT");
        f.setAccessible(true);
        f.setInt(target, limit);
    }

    @ParameterizedTest(name = "[{index}] {0} {1} {2}")
    @MethodSource("cases")
    @DisplayName("Debe generar 3 variantes correctas para los casos con límite 24 chars")
    void should_generate_expected_variants(
            String givenNames,
            String surname1,
            String surname2,
            String expectedV1,
            String expectedV2,
            String expectedV3
    ) {
        List<String> result = sut.cardNameShortener(givenNames, surname1, surname2);

        assertEquals(List.of(expectedV1, expectedV2, expectedV3), result);

        // mayúsculas y límite
        result.forEach(v -> {
            assertEquals(v, v.toUpperCase(), "La variante debe estar en mayúsculas");
            assertTrue(v.length() <= 24, "La variante no puede superar el límite de 24 caracteres");
        });
    }

    /**
     *
     * cardNameShortener(givenNames, surname1, surname2)
     *
     *	Contiene los casos de prueba
     */
    static Stream<Arguments> cases() {
        return Stream.of(
            // 1) José Luis Rodríguez Zapatero (28)
            Arguments.of("José Luis", "Rodríguez", "Zapatero",
                    "JOSE L. RODRIGUEZ Z.",
                    "J. LUIS RODRIGUEZ Z.",
                    "J. L. RODRIGUEZ ZAPATERO"
            ),

            // 2) María del Carmen Fernández González (37)
            Arguments.of("María del Carmen", "Fernández", "González",
                    "MARIA D. C. FERNANDEZ G.",
                    "M. CARMEN FERNANDEZ G.",
                    "M. D. C. FERNANDEZ G."
            ),

            // 3) Carlos Saiz del Barrio (25) -> interpretado como apellido2 "del Barrio"
            Arguments.of("Carlos", "Saiz", "del Barrio",
                    "CARLOS SAIZ DEL BARRIO",
                    "CARLOS SAIZ DEL BARRIO",
                    "C. SAIZ DEL BARRIO"
            ),

            // 4) Jean-Claude Van Damme (23) -> "Jean Claude", apellidos Van / Damme
            Arguments.of("Jean-Claude", "Van", "Damme",
                    "JEAN C. VAN DAMME",
                    "J. CLAUDE VAN DAMME",
                    "J. C. VAN DAMME"
            ),

            // 5) François-Henri Pinault (23) -> "Francois Henri", 1 apellido
            Arguments.of("François-Henri", "Pinault", "",
                    "FRANCOIS H. PINAULT",
                    "F. HENRI PINAULT",
                    "F. H. PINAULT"
            ),

            // 6) Wolfgang Amadeus Mozart (24) -> 1 apellido
            Arguments.of("Wolfgang Amadeus", "Mozart", "",
                    "WOLFGANG A. MOZART",
                    "W. AMADEUS MOZART",
                    "W. A. MOZART"
            ),

            // 7) Aleksandr Sergeyevich Pushkin (32) -> 1 apellido
            Arguments.of("Aleksandr Sergeyevich", "Pushkin", "",
                    "ALEKSANDR S. PUSHKIN",
                    "A. SERGEYEVICH PUSHKIN",
                    "A. S. PUSHKIN"
            ),

            // 8) Ludwig van Beethoven (19) -> apellidos van / Beethoven
            Arguments.of("Ludwig", "van", "Beethoven",
                    "LUDWIG VAN BEETHOVEN",
                    "LUDWIG VAN BEETHOVEN",
                    "L. VAN BEETHOVEN"
            ),

            // 9) Gabriel José García Márquez (27)
            Arguments.of("Gabriel José", "García", "Márquez",
                    "GABRIEL J. GARCIA M.",
                    "G. JOSE GARCIA MARQUEZ",
                    "G. J. GARCIA MARQUEZ"
            ),

            // 10) Juan Martín del Potro (24) -> apellidos del / Potro
            Arguments.of("Juan Martín", "del", "Potro",
                    "JUAN M. DEL POTRO",
                    "J. MARTIN DEL POTRO",
                    "J. M. DEL POTRO"
            ),

            // 11) Abdulrahman bin Faisal Al Saud (32) -> apellidos Al / Saud
            Arguments.of("Abdulrahman bin Faisal", "Al", "Saud",
                    "ABDULRAHMAN B. F. AL S.",
                    "A. B. FAISAL AL SAUD",
                    "A. B. F. AL SAUD"
            ),

            // 12) Giancarlo Esposito Fernández (28) -> 1 apellido
            Arguments.of("Giancarlo Esposito", "Fernández", "",
                    "GIANCARLO E. FERNANDEZ",
                    "G. ESPOSITO FERNANDEZ",
                    "G. E. FERNANDEZ"
            ),

            // 13) Christopher Jonathan Nolan (27) -> 1 apellido
            Arguments.of("Christopher Jonathan", "Nolan", "",
                    "CHRISTOPHER J. NOLAN",
                    "C. JONATHAN NOLAN",
                    "C. J. NOLAN"
            ),

            // 14) Maximilian Alexander Müller (28)
            Arguments.of("Maximilian Alexander", "Müller", "",
                    "MAXIMILIAN A. MULLER",
                    "M. ALEXANDER MULLER",
                    "M. A. MULLER"
            ),

            // 15) Santiago Ramón y Cajal (24) -> apellidos y / Cajal
            Arguments.of("Santiago Ramón", "y", "Cajal",
                    "SANTIAGO R. Y CAJAL",
                    "S. RAMON Y CAJAL",
                    "S. R. Y CAJAL" 
            )
        );
    }
}


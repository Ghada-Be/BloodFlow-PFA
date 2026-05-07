package com.bloodflow.medical.util;

/**
 * Utilitaires de validation simples pour le service médical.
 */
public class ValidationUtils {

    private ValidationUtils() {}

    // Valider un groupe sanguin (A+, A-, B+, B-, AB+, AB-, O+, O-)
    public static boolean isValidGroupeSanguin(String groupe) {
        if (groupe == null) return false;
        return groupe.matches("^(A|B|AB|O)[+-]$");
    }

    // Valider qu'une chaîne n'est pas vide
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Valider un numéro de poche (format : PC-XXXXXXXX)
    public static boolean isValidNumeroPoche(String numero) {
        if (numero == null) return false;
        return numero.matches("^[A-Z]{2,3}-[A-Z0-9]{4,12}$");
    }
}

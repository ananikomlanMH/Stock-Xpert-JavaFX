package com.anani.stockxpert.Util;

public class ConversionNombreEnLettre {
    private static final String[] units = {
            "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix", "onze", "douze", "treize",
            "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"
    };

    private static final String[] tens = {
            "", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante", "quatre-vingt", "quatre-vingt"
    };

    public static String convertirEnLettre(int nombre) {
        if (nombre == 0) {
            return "zéro";
        } else if (nombre < 0) {
            return "moins " + convertirEnLettre(Math.abs(nombre));
        }

        return convertirPartie(nombre);
    }

    private static String convertirPartie(int nombre) {
        String lettre = "";

        if (nombre < 20) {
            lettre = units[nombre];
        } else if (nombre < 100) {
            int unite = nombre % 10;
            int dizaine = nombre / 10;
            lettre = tens[dizaine];

            if (unite > 0) {
                if (dizaine == 7 || dizaine == 9) {
                    lettre += "-";
                } else {
                    lettre += " ";
                }

                lettre += units[unite];
            }
        } else if (nombre < 1000) {
            int centaine = nombre / 100;
            int reste = nombre % 100;
            lettre = units[centaine] + " cent";

            if (reste > 0) {
                lettre += " " + convertirPartie(reste);
            }
        } else {
            int puissance = 0;
            int tempNombre = nombre;

            while (tempNombre >= 1000) {
                tempNombre /= 1000;
                puissance++;
            }

            int partieBas = nombre / (int) Math.pow(1000, puissance);
            int partieHaut = nombre % (int) Math.pow(1000, puissance);
            lettre = convertirPartie(partieBas) + " " + getSuffixe(puissance);

            if (partieHaut > 0) {
                lettre += " " + convertirPartie(partieHaut);
            }
        }

        return lettre;
    }

    private static String getSuffixe(int puissance) {
        String suffixe = "";

        switch (puissance) {
            case 1:
                suffixe = "mille";
                break;
            case 2:
                suffixe = "million";
                break;
            case 3:
                suffixe = "milliard";
                break;
            case 4:
                suffixe = "billion";
                break;
            // Ajoutez d'autres puissances au besoin

            default:
                suffixe = "puissance non supportée";
                break;
        }

        return suffixe;
    }

    public static void main(String[] args) {
        int nombre = 123456789;
        String enLettre = convertirEnLettre(nombre);
        System.out.println(nombre + " en lettre : " + enLettre);
    }
}


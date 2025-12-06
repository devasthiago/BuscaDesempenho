package br.com.seuusuario.busca;

import java.nio.file.*;
import java.io.IOException;

public class SerialCPU {

    public static Resultado buscar(String arquivo, String palavra) throws IOException {

        long inicio = System.currentTimeMillis();

        String texto = Files.readString(Path.of(arquivo));
        String[] palavras = texto.split("\\W+");

        int contador = 0;

        for (String p : palavras) {
            if (p.equalsIgnoreCase(palavra)) {
                contador++;
            }
        }

        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;

        return new Resultado("SerialCPU", arquivo, palavra, contador, tempo);
    }
}

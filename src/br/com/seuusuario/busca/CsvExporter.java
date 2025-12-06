// src/main/java/br/com/seuusuario/busca/CsvExporter.java
package br.com.seuusuario.busca;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    public static void exportarResultados(
            List<SearchResult> resultados,
            String caminhoArquivo
    ) throws IOException {

        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            writer.write("algoritmo,arquivo,palavra,num_threads,execucao,tempo_ms,ocorrencias\n");
            for (SearchResult r : resultados) {
                writer.write(String.format(
                        "%s,%s,%s,%d,%d,%d,%d%n",
                        r.getAlgoritmo(),
                        r.getArquivo(),
                        r.getPalavra(),
                        r.getNumThreads(),
                        r.getExecucao(),
                        r.getTempoMs(),
                        r.getOcorrencias()
                ));
            }
        }
    }
}

// src/main/java/br/com/seuusuario/busca/SearchResult.java
package br.com.seuusuario.busca;

public class SearchResult {
    private final String algoritmo;
    private final String arquivo;
    private final String palavra;
    private final int numThreads;
    private final long tempoMs;
    private final long ocorrencias;
    private final int execucao;

    public SearchResult(
            String algoritmo,
            String arquivo,
            String palavra,
            int numThreads,
            long tempoMs,
            long ocorrencias,
            int execucao
    ) {
        this.algoritmo = algoritmo;
        this.arquivo = arquivo;
        this.palavra = palavra;
        this.numThreads = numThreads;
        this.tempoMs = tempoMs;
        this.ocorrencias = ocorrencias;
        this.execucao = execucao;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public String getArquivo() {
        return arquivo;
    }

    public String getPalavra() {
        return palavra;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public long getTempoMs() {
        return tempoMs;
    }

    public long getOcorrencias() {
        return ocorrencias;
    }

    public int getExecucao() {
        return execucao;
    }
}

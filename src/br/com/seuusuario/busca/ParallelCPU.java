package br.com.seuusuario.busca;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Implementação da versão paralela em CPU para contagem de ocorrências
 * de uma palavra em um texto.
 *
 * Nesta versão, o número de threads é configurável, permitindo avaliar
 * explicitamente o impacto de 2, 4, 8 (ou mais) threads no desempenho.
 */
public class ParallelCPU {

    /**
     * Método principal de busca paralela. Lê o arquivo, distribui o trabalho
     * entre as threads e consolida o resultado.
     *
     * @param arquivo    Caminho do arquivo de texto.
     * @param palavra    Palavra alvo a ser buscada.
     * @param numThreads Quantidade de threads a serem utilizadas.
     * @return           Objeto Resultado contendo método, arquivo, palavra,
     *                   total de ocorrências e tempo de execução em ms.
     */
    public static Resultado buscar(String arquivo, String palavra, int numThreads) throws IOException, InterruptedException, ExecutionException {

        // Leitura do texto completo
        String texto = Files.readString(Path.of(arquivo));
        String[] palavras = texto.split("\\W+");

        // Pool de threads fixo, de acordo com a configuração desejada
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Integer>> futuros = new ArrayList<>();

        // Estratégia simples de divisão: fatiar o array de palavras em blocos
        int totalPalavras = palavras.length;
        int tamanhoBloco = (int) Math.ceil(totalPalavras / (double) numThreads);

        long inicio = System.currentTimeMillis();

        for (int t = 0; t < numThreads; t++) {
            int inicioBloco = t * tamanhoBloco;
            int fimBloco = Math.min(inicioBloco + tamanhoBloco, totalPalavras);

            if (inicioBloco >= fimBloco) {
                break;
            }

            final int ini = inicioBloco;
            final int fim = fimBloco;

            Callable<Integer> tarefa = () -> {
                int count = 0;
                for (int i = ini; i < fim; i++) {
                    if (palavras[i].equalsIgnoreCase(palavra)) {
                        count++;
                    }
                }
                return count;
            };

            futuros.add(executor.submit(tarefa));
        }

        int totalOcorrencias = 0;
        for (Future<Integer> f : futuros) {
            totalOcorrencias += f.get();
        }

        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;

        executor.shutdown();

        // Aqui já deixo explícito no nome do método a quantidade de threads utilizada,
        // para facilitar a leitura e análise posterior no CSV.
        String nomeMetodo = "ParallelCPU-" + numThreads + "T";

        return new Resultado(nomeMetodo, arquivo, palavra, totalOcorrencias, tempo);
    }

    /**
     * Sobrecarga opcional: usa o número de núcleos lógicos da máquina
     * como configuração padrão de threads.
     */
    public static Resultado buscar(String arquivo, String palavra) throws IOException, ExecutionException, InterruptedException {
        int defaultThreads = Runtime.getRuntime().availableProcessors();
        return buscar(arquivo, palavra, defaultThreads);
    }
}

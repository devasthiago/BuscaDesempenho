package br.com.seuusuario.busca;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Classe principal responsável por orquestrar toda a execução do trabalho.
 * Aqui consolidamos a lógica de testes, repetição de execuções,
 * variação de número de threads na CPU e geração do arquivo CSV.
 *
 * A ideia é ter uma visão clara do comportamento de:
 * - Versão serial em CPU
 * - Versões paralelas em CPU com 2, 4 e 8 threads
 * - Versão paralela em GPU (OpenCL / JOCL)
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // Conjunto de dados fornecidos (textos longos, o que favorece análise de desempenho)
        String[] arquivos = {
                "data/Dracula-165307.txt",
                "data/MobyDick-217452.txt",
                "data/DonQuixote-388208.txt"
        };

        // Palavra alvo – escolhida de forma a ocorrer com frequência nos textos em inglês
        String palavra = "the";

        // Quantidade de repetições de cada experimento
        int repeticoes = 3;

        // Configurações explícitas de threads para o ParallelCPU
        int[] configuracoesThreads = { 2, 4, 8 };

        // Lista mestre de resultados
        List<Resultado> resultados = new ArrayList<>();

        System.out.println("\n================ INÍCIO DA EXECUÇÃO ================\n");

        for (String arquivo : arquivos) {

            System.out.println("Processando arquivo: " + arquivo);

            // ===============================
            // Execução Serial na CPU
            // ===============================
            for (int exec = 1; exec <= repeticoes; exec++) {
                Resultado r = SerialCPU.buscar(arquivo, palavra);
                resultados.add(r);
                System.out.printf("SerialCPU (exec %d): %d ocorrências em %d ms%n",
                        exec, r.ocorrencias, r.tempoMs);
            }

            // ===============================
            // Execução Paralela na CPU (2, 4, 8 threads)
            // ===============================
            for (int nt : configuracoesThreads) {
                for (int exec = 1; exec <= repeticoes; exec++) {
                    Resultado r = executarParallelCPU(arquivo, palavra, nt);
                    resultados.add(r);
                    System.out.printf("%s (exec %d): %d ocorrências em %d ms%n",
                            r.metodo, exec, r.ocorrencias, r.tempoMs);
                }
            }

            // ===============================
            // Execução Paralela na GPU (OpenCL)
            // ===============================
            for (int exec = 1; exec <= repeticoes; exec++) {
                Resultado r = ParallelGPU.buscar(arquivo, palavra);
                resultados.add(r);
                System.out.printf("ParallelGPU (exec %d): %d ocorrências em %d ms%n",
                        exec, r.ocorrencias, r.tempoMs);
            }

            System.out.println();
        }

        // Geração do CSV consolidando todas as execuções
        gerarCSV(resultados, "results/resultados.csv");

        System.out.println("\nCSV final gerado em: results/resultados.csv\n");
        System.out.println("================ FIM DA EXECUÇÃO ================");
    }

    /**
     * Método auxiliar apenas para deixar o bloco de tratamento de exceções
     * do ParallelCPU isolado, mantendo o fluxo principal mais limpo.
     */
    private static Resultado executarParallelCPU(String arquivo, String palavra, int numThreads) {
        try {
            return ParallelCPU.buscar(arquivo, palavra, numThreads);
        } catch (ExecutionException | InterruptedException | java.io.IOException e) {
            // Em um cenário real, poderíamos ter um tratamento mais elaborado.
            // Para o contexto deste trabalho, apenas registramos o erro e retornamos
            // um resultado "nulo" para não interromper o restante dos testes.
            e.printStackTrace();
            return new Resultado("ParallelCPU-" + numThreads + "T-ERRO", arquivo, palavra, 0, 0);
        }
    }

    /**
     * Responsável por materializar os resultados em um arquivo CSV.
     * Formato:
     * metodo,arquivo,palavra,ocorrencias,tempoMs
     */
    private static void gerarCSV(List<Resultado> resultados, String caminho) throws Exception {
        FileWriter fw = new FileWriter(caminho);
        fw.write("metodo,arquivo,palavra,ocorrencias,tempoMs\n");

        for (Resultado r : resultados) {
            fw.write(r.toCSV() + "\n");
        }

        fw.close();
    }
}

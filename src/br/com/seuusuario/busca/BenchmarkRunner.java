package br.com.seuusuario.busca;

import java.io.FileWriter;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {

        String[] arquivos = {
                "data/Dracula-165307.txt",
                "data/MobyDick-217452.txt",
                "data/DonQuixote-388208.txt"
        };

        String palavra = "the";

        FileWriter csv = new FileWriter("results/resultados.csv");
        csv.write("metodo,arquivo,palavra,ocorrencias,tempoMs\n");

        for (String arquivo : arquivos) {

            for (int i = 1; i <= 3; i++) {

                Resultado r1 = SerialCPU.buscar(arquivo, palavra);
                csv.write(r1.toCSV() + "\n");

                Resultado r2 = ParallelCPU.buscar(arquivo, palavra);
                csv.write(r2.toCSV() + "\n");

                Resultado r3 = ParallelGPU.buscar(arquivo, palavra);
                csv.write(r3.toCSV() + "\n");
            }
        }

        csv.close();
        System.out.println("CSV gerado em results/resultados.csv");
    }
}

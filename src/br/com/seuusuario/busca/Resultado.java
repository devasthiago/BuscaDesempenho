package br.com.seuusuario.busca;

public class Resultado {

    public String metodo;
    public String arquivo;
    public String palavra;
    public int ocorrencias;
    public long tempoMs;

    public Resultado(String metodo, String arquivo, String palavra, int ocorrencias, long tempoMs) {
        this.metodo = metodo;
        this.arquivo = arquivo;
        this.palavra = palavra;
        this.ocorrencias = ocorrencias;
        this.tempoMs = tempoMs;
    }

    public String toCSV() {
        return metodo + "," + arquivo + "," + palavra + "," + ocorrencias + "," + tempoMs;
    }
}

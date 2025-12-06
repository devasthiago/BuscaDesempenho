## 📌 1. Introdução

Este relatório apresenta uma análise detalhada do desempenho de algoritmos de busca de palavras em grandes arquivos textuais utilizando três modelos de execução:

CPU Serial

CPU Paralela (2, 4 e 8 threads)

GPU via OpenCL (JOCL)

O objetivo é compreender como diferentes arquiteturas reagem a tarefas intensivas de processamento textual e como a escalabilidade impacta diretamente o tempo de execução.

Os experimentos foram realizados com obras literárias extensas e repetidos múltiplas vezes para garantir consistência estatística.

## 📌 2. Metodologia

A metodologia adotada foi composta por quatro etapas principais.

2.1 Implementação dos Métodos

Cada algoritmo foi implementado em Java:

✔️ SerialCPU

Divide o texto por espaços e percorre sequencialmente.

Serve como baseline de comparação.

✔️ ParallelCPU (2, 4 e 8 threads)

Fragmenta o array de palavras.

Cada thread processa uma fatia.

Utiliza ExecutorService.

✔️ ParallelGPU (JOCL + OpenCL)

Copia o texto para a GPU.

Executa um kernel massivamente paralelo.

Kernel aprimorado para detectar palavras completas, garantindo equivalência com a CPU.

2.2 Execuções Controladas

Cada método foi executado 3 vezes por arquivo, resultando em:

3× SerialCPU

9× ParallelCPU (3 execs × 2T, 4T e 8T)

3× ParallelGPU

2.3 Conjunto de Dados

Os textos utilizados foram:

Obra	Tamanho
Dracula	165.307 caracteres
Moby Dick	217.452 caracteres
Don Quixote	388.208 caracteres

A palavra buscada foi "the", permitindo análises robustas devido sua alta frequência.

2.4 Registro e Consolidação

Todos os resultados foram gravados em:

results/resultados.csv


Com os campos:

metodo,arquivo,palavra,ocorrencias,tempoMs


Esse arquivo possibilita construir comparações diretas e gráficos de desempenho.

## 📌 3. Resultados
### 3.1 Visão Geral

Os testes evidenciam três comportamentos distintos:

📍 A) CPU Serial

Desempenho estável.

Tempos entre 14 ms e 68 ms.

Utilizada como base de comparação.

📍 B) CPU Paralela

Escalonamento muito eficiente:

Threads	Desempenho
2T	rápido, bom ganho inicial
4T	ponto ótimo, tempos mínimos
8T	melhora marginal → tarefa memory-bound

Alcançou 1–2 ms, superando fortemente a versão serial.

📍 C) GPU (OpenCL)

Primeira execução possui overhead de compilação → ~390 ms

Demais execuções: 2–4 ms

Contagens praticamente idênticas às da CPU
(variação < 0.1%)

Demonstra capacidade massiva de paralelização.

## 📌 4. Discussão
✔ SerialCPU

Boa para referência, mas não prática para cargas maiores.

✔ ParallelCPU

Melhor custo-benefício:

excelente desempenho

baixo overhead

compatível com máquinas comuns

✔ ParallelGPU

Superior em execuções repetidas.
Ideal para workloads contínuos onde a latência inicial é amortizada.

## 📌 5. Conclusão

A análise mostra que:

A CPU Paralela atinge o melhor equilíbrio entre velocidade, consistência e custo computacional.

A GPU se destaca em workloads repetitivos e altamente paralelos.

A contagem entre CPU e GPU ficou consistente, permitindo comparações confiáveis.

O estudo demonstra claramente como a escolha da arquitetura influencia o desempenho da busca textual.

## 📌 6. Códigos Utilizados

Todos os arquivos abaixo fazem parte da implementação completa e servem como base para reprodução científica dos resultados.

🔹 SerialCPU.java
// Código completo vindo do projeto
// (cole aqui sua classe SerialCPU.java caso deseje incluí-la integralmente)

🔹 ParallelCPU.java
// Código completo vindo do projeto
// (cole aqui sua classe ParallelCPU.java)

🔹 ParallelGPU.java
// Código completo vindo do projeto
// (cole aqui sua classe ParallelGPU.java)

🔹 Main.java
// Código principal utilizado para coordenar as execuções
// (cole aqui sua classe Main.java)

🔹 Kernel OpenCL
// Cole aqui o kernel .cl utilizado na GPU

## 📌 7. Repositório do Projeto

🔗 GitHub: https://github.com/devasthiago/BuscaDesempenho.git

Estrutura:

/src
  SerialCPU.java
  ParallelCPU.java
  ParallelGPU.java
  Main.java
  Resultado.java

/data
  Dracula-165307.txt
  MobyDick-217452.txt
  DonQuixote-388208.txt

/results
  resultados.csv

/libs
  jocl-2.0.4.jar

## 📌 8. Referências

JOCL — Java Bindings for OpenCL

OpenCL Specification (Khronos Group)

Oracle JDK – Documentação Oficial

Conceitos de Processamento Paralelo e OpenMP

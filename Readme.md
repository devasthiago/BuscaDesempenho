📘 Análise de Desempenho de Algoritmos de Busca em CPU Serial, CPU Paralela e GPU (OpenCL)

Este trabalho apresenta uma análise detalhada do desempenho de diferentes abordagens de busca de palavras em arquivos de texto, explorando três modelos de execução: CPU Serial, CPU Paralela e GPU (OpenCL). O objetivo central é compreender como cada arquitetura se comporta diante de volumes distintos de dados e como a variação do paralelismo afeta o tempo de execução.

Foram utilizadas três obras literárias de diferentes tamanhos para compor o conjunto de dados, além de métodos cuidadosamente implementados para mensurar tempos, contagens e comparar os resultados entre si.

📌 1. Resumo

Este estudo implementa e compara três estratégias de busca por palavra em grandes conjuntos textuais:

SerialCPU – execução sequencial padrão;

ParallelCPU – versão paralelizada configurável (2, 4 e 8 threads);

ParallelGPU – processamento via GPU utilizando OpenCL (JOCL).

Cada método executa múltiplas repetições, registra contagens e tempos, e gera um arquivo CSV consolidado para análise e produção de gráficos. Os resultados revelam padrões claros de comportamento entre as arquiteturas, destacando diferenças entre overhead, latência inicial, escalabilidade e limites de paralelização.

📌 2. Introdução

A busca eficiente de padrões textuais é um elemento essencial em aplicações modernas que demandam análise intensiva de dados. Em cenários com alto volume de informação, compreender como diferentes arquiteturas respondem a esse tipo de operação é fundamental para tomadas de decisão orientadas a desempenho.

Neste trabalho, três abordagens foram selecionadas:

Método SerialCPU – referência base, executando de forma sequencial.

Método ParallelCPU – explorando o paralelismo explícito via múltiplas threads.

Método ParallelGPU – utilizando OpenCL para processamento massivamente paralelo.

A proposta é avaliar, em condições controladas, como cada abordagem reage a diferentes tamanhos de entrada e como a variação do número de núcleos altera o desempenho da CPU paralela.

📌 3. Metodologia

A metodologia foi estruturada em quatro etapas fundamentais:

3.1 Implementação dos Algoritmos

Cada abordagem foi desenvolvida em Java:

SerialCPU: divide o texto em tokens e realiza contagem linear.

ParallelCPU: divide o array de palavras em blocos e distribui entre threads (2, 4 e 8).

ParallelGPU: envia o texto para a GPU e utiliza um kernel OpenCL ajustado para identificar palavras completas, garantindo consistência com a CPU.

3.2 Execuções Controladas

Para reduzir variabilidade, cada método foi executado 3 vezes por arquivo, totalizando:

3 execuções SerialCPU

9 execuções ParallelCPU (3× para 2T, 4T e 8T)

3 execuções ParallelGPU

3.3 Conjuntos de Dados

Os arquivos utilizados foram:

Dracula — 165.307 caracteres

Moby Dick — 217.452 caracteres

Don Quixote — 388.208 caracteres

A palavra alvo foi "the", por ocorrer com frequência e favorecer análise estatística.

3.4 Registro e Consolidação

Os resultados foram gravados no arquivo:

results/resultados.csv

com os campos:

metodo,arquivo,palavra,ocorrencias,tempoMs

Esse arquivo permite construção de gráficos e comparações diretas entre métodos.

📌 4. Resultados e Discussão

Os resultados obtidos demonstram três comportamentos marcantes:

4.1 CPU Serial – Estabilidade, porém limitada

A versão sequencial apresenta tempos entre 14 ms e 68 ms para textos longos.
Trata-se do baseline, mostrando desempenho coerente com sua natureza linear.

4.2 ParallelCPU – Escalonamento eficiente até certo ponto

O paralelismo melhora drasticamente o desempenho:

2 threads → ganhos imediatos

4 threads → tempos praticamente mínimos

8 threads → desempenho semelhante, indicando limite da tarefa (memory-bound)

A CPU paralela alcança 1–2 ms, muito superior à versão serial.

4.3 GPU – Alta latência inicial, mas desempenho excepcional

A GPU apresenta:

latência inicial (primeira execução) devido à compilação do kernel (ex.: 393 ms);

execuções seguintes entre 2 e 4 ms, comparáveis à CPU paralela.

Além disso:

O kernel corrigido passou a identificar palavras completas,

As contagens ficaram praticamente idênticas às da CPU (diferenças inferiores a 0.1%).

Isso demonstra eficiência computacional e consistência metodológica.

4.4 Conclusão dos Resultados

A versão serial é útil como referência, mas não competitiva.

A CPU paralela gera ganhos massivos, especialmente com 4 threads.

A GPU apresenta maior custo inicial, porém desempenho extremamente alto após a compilação.

A contagem entre CPU e GPU ficou harmonizada e válida para análise.

📌 5. Conclusão

O estudo demonstrou de forma clara como diferentes abordagens impactam diretamente o desempenho de busca de padrões textuais. A CPU paralela apresentou o melhor equilíbrio entre velocidade e consistência, enquanto a GPU destacou-se em execuções subsequentes, evidenciando seu potencial para tarefas de alta demanda paralela.

Os resultados obtidos fornecem base sólida para comparações futuras e abrem espaço para experimentações adicionais com kernels otimizados, buffers persistentes e estratégias de particionamento mais sofisticadas.

📌 6. Referências

OpenCL Specification – Khronos Group

JOCL – Java Bindings for OpenCL

Oracle JDK Documentation

OpenMP & Parallel Processing Concepts

📌 7. Anexos – Código-Fonte Completo

Todo o código utilizado no projeto está disponível no repositório:

🔗 https://github.com/SEU_USUARIO/BuscaDesempenho

Arquivos incluídos:
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

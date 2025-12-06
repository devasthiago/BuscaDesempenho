package br.com.seuusuario.busca;

import org.jocl.*;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.jocl.CL.*;

/**
 * Versão paralela em GPU utilizando OpenCL (via JOCL).
 * Aqui a lógica foi ajustada para contar apenas ocorrências
 * da palavra alvo como "token" isolado, respeitando limites
 * de palavra (sem contar "the" dentro de "other", por exemplo).
 */
public class ParallelGPU {

    // Kernel OpenCL com verificação de limites de palavra
    private static final String KERNEL_SOURCE =
            "__kernel void countWord(__global const char* text, "
          + "                        const int textLength, "
          + "                        __global const char* word, "
          + "                        const int wordLength, "
          + "                        __global int* result) {"
          + "    int i = get_global_id(0);"
          + "    if (i + wordLength > textLength) return;"
          + "    int isPrevAlnum = 0;"
          + "    int isNextAlnum = 0;"
          + "    if (i > 0) {"
          + "        char cPrev = text[i - 1];"
          + "        if ((cPrev >= 'a' && cPrev <= 'z') ||"
          + "            (cPrev >= 'A' && cPrev <= 'Z') ||"
          + "            (cPrev >= '0' && cPrev <= '9')) {"
          + "            isPrevAlnum = 1;"
          + "        }"
          + "    }"
          + "    if (isPrevAlnum == 1) return;"
          + "    if (i + wordLength < textLength) {"
          + "        char cNext = text[i + wordLength];"
          + "        if ((cNext >= 'a' && cNext <= 'z') ||"
          + "            (cNext >= 'A' && cNext <= 'Z') ||"
          + "            (cNext >= '0' && cNext <= '9')) {"
          + "            isNextAlnum = 1;"
          + "        }"
          + "    }"
          + "    if (isNextAlnum == 1) return;"
          + "    int match = 1;"
          + "    for (int j = 0; j < wordLength; j++) {"
          + "        if (text[i + j] != word[j]) {"
          + "            match = 0;"
          + "            break;"
          + "        }"
          + "    }"
          + "    if (match == 1) {"
          + "        atomic_inc(result);"
          + "    }"
          + "}";

    /**
     * Método principal de busca na GPU.
     *
     * @param arquivo Caminho do arquivo de texto.
     * @param palavra Palavra alvo a ser contada.
     * @return        Resultado com contagem e tempo em ms.
     */
    public static Resultado buscar(String arquivo, String palavra) throws Exception {

        // Leitura e normalização para minúsculas (coerente com CPU)
        String texto = Files.readString(Path.of(arquivo));
        String textoLower = texto.toLowerCase();
        String palavraLower = palavra.toLowerCase();

        byte[] textBytes = textoLower.getBytes();   // assumindo texto ASCII/UTF-8 simples
        byte[] wordBytes = palavraLower.getBytes();

        int textLength = textBytes.length;
        int wordLength = wordBytes.length;

        if (textLength == 0 || wordLength == 0 || wordLength > textLength) {
            return new Resultado("ParallelGPU", arquivo, palavra, 0, 0);
        }

        CL.setExceptionsEnabled(true);

        long inicio = System.currentTimeMillis();

        // Plataforma
        cl_platform_id[] plataformas = new cl_platform_id[1];
        clGetPlatformIDs(1, plataformas, null);

        // Dispositivo GPU
        cl_device_id[] dispositivos = new cl_device_id[1];
        clGetDeviceIDs(plataformas[0], CL_DEVICE_TYPE_GPU, 1, dispositivos, null);
        cl_device_id dispositivo = dispositivos[0];

        // Contexto e fila de comandos
        cl_context contexto = clCreateContext(null, 1, new cl_device_id[]{dispositivo}, null, null, null);
        cl_command_queue fila = clCreateCommandQueue(contexto, dispositivo, 0, null);

        // Programa e kernel
        cl_program programa = clCreateProgramWithSource(contexto, 1,
                new String[]{KERNEL_SOURCE}, null, null);
        clBuildProgram(programa, 0, null, null, null, null);

        cl_kernel kernel = clCreateKernel(programa, "countWord", null);

        // Buffers
        cl_mem textMem = clCreateBuffer(
                contexto,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_char * textLength,
                Pointer.to(textBytes),
                null
        );

        cl_mem wordMem = clCreateBuffer(
                contexto,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_char * wordLength,
                Pointer.to(wordBytes),
                null
        );

        int[] resultadoArray = new int[]{0};
        cl_mem resultMem = clCreateBuffer(
                contexto,
                CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int,
                Pointer.to(resultadoArray),
                null
        );

        // Argumentos do kernel
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(textMem));
        clSetKernelArg(kernel, 1, Sizeof.cl_int, Pointer.to(new int[]{textLength}));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(wordMem));
        clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[]{wordLength}));
        clSetKernelArg(kernel, 4, Sizeof.cl_mem, Pointer.to(resultMem));

        // Um work-item por posição possível do texto
        long[] globalWorkSize = new long[]{textLength};

        clEnqueueNDRangeKernel(fila, kernel, 1, null, globalWorkSize, null, 0, null, null);
        clFinish(fila);

        // Leitura do resultado de volta para a CPU
        clEnqueueReadBuffer(
                fila,
                resultMem,
                CL_TRUE,
                0,
                Sizeof.cl_int,
                Pointer.to(resultadoArray),
                0,
                null,
                null
        );

        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;

        // Libera recursos OpenCL
        clReleaseMemObject(textMem);
        clReleaseMemObject(wordMem);
        clReleaseMemObject(resultMem);
        clReleaseKernel(kernel);
        clReleaseProgram(programa);
        clReleaseCommandQueue(fila);
        clReleaseContext(contexto);

        int ocorrencias = resultadoArray[0];

        return new Resultado("ParallelGPU", arquivo, palavra, ocorrencias, tempo);
    }
}

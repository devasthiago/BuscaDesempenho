package br.com.seuusuario.busca;

import org.jocl.*;

public class TestJOCL {
    public static void main(String[] args) {
        CL.setExceptionsEnabled(true);

        int[] numPlatforms = new int[1];
        CL.clGetPlatformIDs(0, null, numPlatforms);

        System.out.println("JOCL está funcionando. Plataformas disponíveis: " + numPlatforms[0]);
    }
}

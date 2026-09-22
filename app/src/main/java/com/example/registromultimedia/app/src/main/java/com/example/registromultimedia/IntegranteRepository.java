package com.example.registromultimedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Lista de Integrante compartida por TODA la app — mismo patrón que UserRepository.
  * Antes vivía como variable local dentro de MainActivity; al separar Agregar Integrante
   * y Lista de Integrantes en pantallas propias, ambas necesitan ver los mismos datos,
    * por eso pasa a ser static y vive acá.
     */
     public class IntegranteRepository {

         public static List<Integrante> lista = new ArrayList<>();

             // Se llama una sola vez, la primera vez que la app la necesita.
                 public static void cargarDatosIniciales() {
                         if (!lista.isEmpty()) return; // ya se cargó antes, no duplicar

                                 lista.add(new Integrante("Lucas Bilbao", "Desarrollador", "Java, Android", "No especificado", R.drawable.lucas, 5.0f));
                                         lista.add(new Integrante("Bruno Antio", "Analista", "Python, SQL", "No especificado", R.drawable.bruno, 3.0f));
                                                 lista.add(new Integrante("Anghel Lopez", "Diseñador", "Figma, UX", "No especificado", R.drawable.anghel, 4.0f));
                                                     }
                                                     }
                                                     

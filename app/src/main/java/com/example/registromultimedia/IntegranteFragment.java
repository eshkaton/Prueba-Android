package com.example.registromultimedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class IntegranteFragment extends Fragment {

    private RecyclerView recyclerIntegrante;
    private IntegranteAdapter adapter;
    private ArrayList<Integrante> listaIntegrante;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_integrante, container, false);

        recyclerIntegrante = view.findViewById(R.id.recyclerIntegrantes);

        cargarIntegrantes();

        return view;
    }

    private void cargarIntegrantes() {
        listaIntegrante = new ArrayList<>();
        listaIntegrante.add(new Integrante("Lucas Bilbao", "Ingeniería en Informática", "Java, Android", "Diurno", R.drawable.lucas, 5.0f));
        listaIntegrante.add(new Integrante("Bruno Antio", "Analista Programador", "Python, SQL", "Diurno", R.drawable.bruno, 4.5f));
        listaIntegrante.add(new Integrante("Anghel Lopez", "Diseñador", "Figma, UX", "Diurno", R.drawable.anghel, 4.0f));

        recyclerIntegrante.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IntegranteAdapter(listaIntegrante);
        recyclerIntegrante.setAdapter(adapter);
    }
}
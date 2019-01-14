package com.example.projectsc;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BinFragment extends Fragment {


    public BinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bin, container, false);

        String[] clubList = {"ถัง2", "ถัง3", "ถัง4",
                "ถัง5", "ถัง6", "ถัง7", "ถัง8", "ถัง2", "ถัง3", "ถัง4",
                "ถัง5", "ถัง6", "ถัง7", "ถัง8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                clubList);

        ListView lv = view.findViewById(R.id.lvf2);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "position = " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),Navigationbottom.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        return view;
    }

}

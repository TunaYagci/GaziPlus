package com.hp2m.GaziPlus;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment4 extends Fragment {


    public Fragment4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment4, container, false);

        Button button = (Button) rootView.findViewById(R.id.button);
        // hi sadr, here will be a list contains our YemekList and Timetable etc.
        // for now there is only a button
        // we add the list later, that's the easiest part, leave it to me if you can't
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), TimetableActivity.class));
            }
        });
        // so the above code sends us to TimetableActivity
        return rootView;
    }


    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            //new NotTask().execute();

        }
    }*/
}

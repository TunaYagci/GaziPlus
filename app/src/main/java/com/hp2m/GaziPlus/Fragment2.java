package com.hp2m.GaziPlus;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {


    private static float origx1, origx2, origy1, origy2;
    final EventBus bus = EventBus.getDefault();
    private Button loginButton;
    private View loadingCircle;
    private ImageView topLine, bottomLine;
    private CheckBox checkBox;
    private SharedPreferences sharedP;
    private SharedPreferences.Editor editor;
    private TextView ogrenciNoStatus, parolaStatus, fragment2MainText, loginErrorText;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ImageButton clearParola, clearOgrNo;
    private FloatingActionButton fab;
    private FragmentManager fm;
    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment2, container, false);
        sharedP = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedP.edit();
        bus.register(this);

        fm = this.getFragmentManager();
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        clearParola = (ImageButton) rootView.findViewById(R.id.clearParola);
        clearOgrNo = (ImageButton) rootView.findViewById(R.id.clearOgrNo);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.not_list);
        adapter = new NotAdapter(getActivity(), getNotData(), fm);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setVisibility(View.GONE);
        loginErrorText = (TextView) rootView.findViewById(R.id.login_error);
        loginButton = (Button) rootView.findViewById(R.id.ogrenciLoginLayout);
        bottomLine = (ImageView) rootView.findViewById(R.id.bottomLine);
        topLine = (ImageView) rootView.findViewById(R.id.topLine);
        topLine.setVisibility(View.VISIBLE);
        bottomLine.setVisibility(View.VISIBLE);
        fragment2MainText = (TextView) rootView.findViewById(R.id.fragment2_title);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        ogrenciNoStatus = (TextView) rootView.findViewById(R.id.ogrenci_status);
        parolaStatus = (TextView) rootView.findViewById(R.id.parola_status);
        loadingCircle = rootView.findViewById(R.id.loadingBar);
        final FrameLayout motherLayout = (FrameLayout) rootView.findViewById(R.id.fragment2_motherLayout);
        final EditText ogrNo = (EditText) rootView.findViewById(R.id.ogrenci_numarasi);
        final EditText parola = (EditText) rootView.findViewById(R.id.parola);

        // lets retrieve stored login and password

        clearOgrNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ogrNo.setText("");
                ogrNo.clearFocus();
            }
        });
        clearParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parola.setText("");
                parola.clearFocus();
            }
        });

        if (getSharedPrefData("checkbox", "unchecked").equals("checked")) {
            Log.i("tuna", "checkbox was checked, retrieving data");
            ogrNo.setText(getSharedPrefData("ogrNo", ""));
            if (ogrNo.getText().toString().length() == 9)
                ogrNo.setTextColor(getResources().getColor(R.color.login_success_text_color));
            else
                ogrNo.setTextColor(getResources().getColor(R.color.login_error_text_color));
            parola.setText(getSharedPrefData("parola", ""));
        } else {
            Log.i("tuna", "checkbox wasn't checked");
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    Log.i("tuna", "checkbox is checked, putting data");
                    setSharedPrefData("checkbox", "checked");
                    setSharedPrefData("ogrNo", ogrNo.getText().toString());
                    setSharedPrefData("parola", parola.getText().toString());
                } else {
                    Log.i("tuna", "checkbox is unchecked");
                    setSharedPrefData("checkbox", "unchecked");
                    //Log.i("tuna", "checkbox is unchecked");
                }
            }
        });

        motherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motherLayout.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ogrNo.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(parola.getWindowToken(), 0);
            }
        });

        //ogrNo.setImeOptions(EditorInfo.IME_ACTION_DONE);
        ogrNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ogrNo.getText().toString().length() == 9) {
                    ogrNo.setTextColor(getResources().getColor(R.color.login_success_text_color));
                    if (ogrenciNoStatus.getVisibility() == View.VISIBLE) {
                        ogrenciNoStatus.setVisibility(View.GONE);
                    }
                } else {
                    ogrNo.setTextColor(getResources().getColor(R.color.login_error_text_color));
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //parola.setImeOptions(EditorInfo.IME_ACTION_DONE);
        parola.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (parolaStatus.getVisibility() == View.VISIBLE) {
                    if (parola.getText().length() > 0)
                        parolaStatus.setVisibility(View.GONE);
                    else
                        parolaStatus.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ogrNo.getText().length() < 9) {
                    ogrenciNoStatus.setVisibility(View.VISIBLE);
                }
                if (parola.getText().toString().length() == 0) {
                    parolaStatus.setVisibility(View.VISIBLE);
                } else { // no problem on fields
                    if (isNetworkAvailable()) {
                        ogrenciNoStatus.setVisibility(View.INVISIBLE);
                        if (checkBox.isChecked()) {
                            Log.i("tuna", "checkbox is checked, putting data");
                            setSharedPrefData("checkbox", "checked");
                            setSharedPrefData("ogrNo", ogrNo.getText().toString());
                            setSharedPrefData("parola", parola.getText().toString());
                        } else {
                            Log.i("tuna", "checkbox is unchecked");
                            setSharedPrefData("checkbox", "unchecked");
                            //Log.i("tuna", "checkbox is unchecked");
                        }
                        fragment2MainText.setVisibility(View.INVISIBLE);
                        ogrNo.setVisibility(View.INVISIBLE);
                        parola.setVisibility(View.INVISIBLE);
                        checkBox.setVisibility(View.INVISIBLE);
                        loginButton.setVisibility(View.INVISIBLE);
                        loadingCircle.setVisibility(View.VISIBLE);
                        clearParola.setVisibility(View.INVISIBLE);
                        clearOgrNo.setVisibility(View.INVISIBLE);

                        origx1 = topLine.getX();
                        origx2 = bottomLine.getX();
                        origy1 = topLine.getY();
                        origy2 = bottomLine.getY();
                        final float y1 = topLine.getY();
                        final float y3 = loadingCircle.getY();
                        final float value = (float) ((y3 - y1) / 1.8);
                        topLine.animate().setDuration(1000).translationY(value);
                        bottomLine.animate().setDuration(1000).translationY(-value - 50);
                        new NotTask(getActivity(), ogrNo.getText().toString(), parola.getText().toString()).execute();
                    } else {
                        Snackbar.make(motherLayout, "Ýnternete baðlanýlamýyor", Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });


        return rootView;
    }

    public List<NotInformation> getNotData() {
        Log.i("tuna", "on get not data");
        //List<NotInformation> data = Collections.emptyList();
        //return data;
        return Collections.emptyList();

    }


    @Override
    public void onResume() {
        super.onResume();

        // nor checkbox checked or not we are getting ogrenci_numarasi
        // unless this is a visitor login
        // do this after created the first screen

        if (getSharedPrefData("checkbox", "firstTime") .equals( "checked")) {
            //Log.i("tuna", "in CHECKED, getting data");
            ((EditText) getView().findViewById(R.id.ogrenci_numarasi)).setText(getSharedPrefData("ogrNo", "ERROR"));
            ((EditText) getView().findViewById(R.id.parola)).setText(getSharedPrefData("parola", ""));
        } else if (getSharedPrefData("checkbox", "firstTime") .equals( "unchecked")) {
            //Log.i("tuna", "in unchecked, clearing data");
            ((EditText) getView().findViewById(R.id.ogrenci_numarasi)).setText("");
            ((EditText) getView().findViewById(R.id.parola)).setText("");
            checkBox.setChecked(false);
        }

    }

    public void onEvent(NotDownloadCompleted event) {
        ArrayList<String> notList;
        loadingCircle.setVisibility(View.GONE);
        if (event.message.equals("error")) {
            loginErrorText.setVisibility(View.VISIBLE);
            final float y1 = topLine.getY();
            final float y3 = loginErrorText.getY();
            final float value = (float) ((y3 - y1) / 1.4);
            topLine.animate().setDuration(3500).translationY(value);
            bottomLine.animate().setDuration(3500).translationY(-value).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(final Animator animation) {
                    super.onAnimationEnd(animation);
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            bottomLine.setX(origx2);
                            bottomLine.setY(origy2);
                            topLine.setX(origx1);
                            topLine.setY(origy1);
                            topLine.clearAnimation();
                            bottomLine.clearAnimation();
                            animation.cancel();
                        }
                    };
                    getActivity().runOnUiThread(r);
                    loginErrorText.setVisibility(View.GONE);
                    getView().findViewById(R.id.parola).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.ogrenci_numarasi).setVisibility(View.VISIBLE);
                    fragment2MainText.setVisibility(View.VISIBLE);
                    checkBox.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    clearParola.setVisibility(View.VISIBLE);
                    clearOgrNo.setVisibility(View.VISIBLE);

                }
            });

            return; // are you sure???
        } else if (event.message.equals("GoGoGo")) {
            topLine.animate().setDuration(2000).translationY(-3000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    topLine.setVisibility(View.GONE);
                }
            });
            bottomLine.animate().setDuration(2000).translationY(3000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    bottomLine.setVisibility(View.GONE);
                }
            });


            fragment2MainText.setVisibility(View.GONE);
            getView().findViewById(R.id.parola).setVisibility(View.GONE);
            getView().findViewById(R.id.ogrenci_numarasi).setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            clearParola.setVisibility(View.GONE);
            clearOgrNo.setVisibility(View.GONE);


            //isUserLoggedIn = true;
            Log.i("tuna", "fetch success");
            notList = event.notList2;

            List<NotInformation> data = new ArrayList<>();
            NotInformation current = new NotInformation();
            // first, add header
            current.name = event.idList.get(0);
            current.ogrNo = event.idList.get(1);
            //SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
            //SharedPreferences.Editor editor2 = sharedPreferences.edit();
            editor.putString("currentOgrNo", event.idList.get(1));
            editor.putString("currentOgrName", event.idList.get(0));
            if(event.idList.get(1).equals(sharedP.getString("defaultOgrNo", "hata"))){
                editor.putString("defaultOgrName", event.idList.get(0));
            }
            editor.commit();
            current.imageLink = event.idList.get(2);
            current.genelOrtNumber = event.genelOrt;
            // 0=name 1=ogrNo 2=imageLink
            data.add(current);

            // then add items. Adapter will notice who is sub-header by itself
            // String dersKodu, dersAdi, vizeNotu, finalNotu, butNotu, basariNotu, kredi, sinifOrt; // ITEMS

            Log.i("tuna", "empty List size is=" + notList.size());
            if (notList.size() > 3) {
                NotlarDB db = new NotlarDB(this.getActivity());
                db.deleteAll();
                for (int i = 0; i < notList.size(); i++) {
                    if (notList.get(i).startsWith("2") && notList.get(i).endsWith("ý")) {
                        current = new NotInformation();
                        current.donemAdi = notList.get(i);
                        data.add(current);
                        Log.i("tuna", "donemAdi = " + notList.get(i));
                    } else {
                        String[] parsedNot = parseUpcomingNot(notList.get(i));
                        current = new NotInformation();
                        // ders kodlarý ayný hizada olsun diye, max ders kodu sayýsý 7
                        for (int i2 = parsedNot[0].length(); i2 < 8; i2++) {
                            if (parsedNot[0].length() == 7) {
                                parsedNot[0] += " ";
                                break;
                            }
                            parsedNot[0] += "  ";
                        }
                        current.ogrNo = event.idList.get(1);
                        current.dersKodu = parsedNot[0];
                        current.dersAdi = parsedNot[1];
                        current.vizeNotu = parsedNot[2];
                        current.finalNotu = parsedNot[3];
                        current.butNotu = parsedNot[4];
                        current.basariNotu = parsedNot[5];
                        current.kredi = parsedNot[6];
                        current.sinifOrt = parsedNot[7];
                        // db'ye ekle. Baþka öðrencinin notlarý varsa onlarý silecek.
                        db.addorUpdateNot(current);
                        data.add(current);
                    }
                }
            } else {
                data = Collections.emptyList();
            }


            final List<NotInformation> data2 = data;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    adapter = new NotAdapter(getActivity(), data2, fm);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);//
                    // if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    YoYo.with(Techniques.ZoomIn)
                            .duration(1000)
                            .playOn(fab);
                    YoYo.with(Techniques.ZoomIn)
                            .duration(500)
                            .playOn(recyclerView);
                    // }
                }
            };

            getActivity().runOnUiThread(r);
        }

        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFabClicks();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean hideToolBar = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (hideToolBar) {
                    fab.animate().translationY(300);
                } else {
                    fab.animate().translationY(0);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) {
                    hideToolBar = true;

                } else if (dy < -5) {
                    hideToolBar = false;
                }
            }
        });
    }

    private void handleFabClicks() {
        int animationDuration = 1000;
        YoYo.with(Techniques.RotateOutDownLeft)
                .duration(500)
                .playOn(fab);
        YoYo.with(Techniques.ZoomOut)
                .duration(animationDuration)
                .playOn(recyclerView);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.GONE);
                getView().findViewById(R.id.parola).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.ogrenci_numarasi).setVisibility(View.VISIBLE);
                fragment2MainText.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                clearParola.setVisibility(View.VISIBLE);
                clearOgrNo.setVisibility(View.VISIBLE);
                topLine.clearAnimation();
                bottomLine.clearAnimation();
                topLine.setVisibility(View.VISIBLE);
                bottomLine.setVisibility(View.VISIBLE);
            }
        }, animationDuration);


    }


    private String[] parseUpcomingNot(String satir) {
        satir+="  -";
        String nx2 = satir.replace("                   ", "-    -    -    -    -   ");
        String cc = nx2.replace("                ", "    -    -    -    -   ");
        String nx3 = cc.replace("                ", "    -    -    -    -   ");
        String nx4 = nx3.replace("                 ", "    -    -    -    -   ");
        String cx1 = nx4.replace("              ", "    -    -    -    -    ");
        String cx6 = cx1.replace("             ","    -    -    -    ");
        String cx2 = cx6.replace("            ", "    -    -    ");
        String cx3 = cx2.replace("        ", "    -    ");
        String cx4 = cx3.replace("    ", "  ");
        String cx5 = cx4.replace("   ", "  ");
        return cx5.split("  ");
    }

    public void setSharedPrefData(String key, String value) {
        //editor = sharedP.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedPrefData(String key, String defValue) {
        return sharedP.getString(key, defValue);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}



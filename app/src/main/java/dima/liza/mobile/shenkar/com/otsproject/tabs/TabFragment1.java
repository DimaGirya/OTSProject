package dima.liza.mobile.shenkar.com.otsproject.tabs;

/**
 * Created by Girya on 08/03/2016.
*/
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dima.liza.mobile.shenkar.com.otsproject.R;

public class TabFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }
}
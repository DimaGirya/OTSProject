package dima.liza.mobile.shenkar.com.otsproject.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import dima.liza.mobile.shenkar.com.otsproject.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    int optionTime;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();
        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);

        np.setMaxValue(30);
        np.setMinValue(1);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                optionTime = newVal;
                Log.d(TAG,"UpdateTime:"+newVal);
            }
        });
    }


    public void onClickSaveOption(View view) {
        sPref = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("UpdateTime", optionTime);
        ed.commit();
        Log.d(TAG,"UpdateTime save:"+optionTime);
        finish();
    }
}

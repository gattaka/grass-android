package cz.gattserver.mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String msg = "GrassAPP: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout mainLayout = findViewById(R.id.mainLayout);
        TextView lastTv = null;
        for (int i = 0; i < 5; i++) {
            TextView tv = new TextView(this);
            tv.setText("TEXT " + i);
            tv.setId(100 + i);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (lastTv != null)
                p.addRule(RelativeLayout.BELOW, lastTv.getId());
            tv.setLayoutParams(p);
            mainLayout.addView(tv);
            lastTv = tv;
        }

        Log.d(msg, "The onCreate() event");
    }
}

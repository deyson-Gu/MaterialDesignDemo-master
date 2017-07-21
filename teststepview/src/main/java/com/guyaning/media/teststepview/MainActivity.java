package com.guyaning.media.teststepview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HistoGram histoGram = (HistoGram) findViewById(R.id.histogram);
        String[] data = {"100", "20", "40", "20", "80", "20", "60", "30", "5", "20", "60", "30", "5", "5", "20", "60", "30", "5"};
        final String[] title = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "6", "7", "8", "9", "9", "6", "7", "8", "9"};
        histoGram.setNum(title.length);
        histoGram.setData(data);
        histoGram.setxTitleString(title);


//        histoGram.setOnChartClickListener(new HistoGram.OnChartClickListener() {
//
//
//            @Override
//            public void onClick(int num, float x, float y, float value) {
//
//                //显示提示窗
//                View inflate = View.inflate(MainActivity.context, R.layout.popupwindow, null);
//                TextView textView = (TextView) inflate.findViewById(R.id.main_tv);
//                textView.setText(value + "%\n" + title[num - 1]);
//                if (mPopupWindow != null) {
//                    mPopupWindow.dismiss();
//                }
//                mPopupWindow = new PopupWindow(inflate, 140, 60, true);
//                mPopupWindow.setTouchable(true);
//                mPopupWindow.showAsDropDown(histoGram, (int) (x - 40), (int) ((-histoGram.getHeight()) + y - 85));
//            }
//        });

    }
}

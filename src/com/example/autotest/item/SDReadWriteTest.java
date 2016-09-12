package com.example.autotest.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SDReadWriteTest extends Fragment {

    String title = "SD 卡读写";
    boolean isMounted = false;
    String test = "welcom to sprocomm";
    TextView tv_status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sdrw, null);
        TextView tv_title = (TextView) view.findViewById(R.id.sd_head);
        tv_status = (TextView) view.findViewById(R.id.sd_content);
        tv_title.setText(title);
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isMounted = checkSDCard();
        if (!isMounted) {
            tv_status.setText("no sdcard");
            ((TestActivity) getActivity()).next(TestCode.Browser_3G);
        } else {
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                byte[] bytes = test.getBytes();
                byte bytesRead[] = new byte[100];
                File fp = new File(Environment.getExternalStorageDirectory(), "test.txt");
                if (fp.exists())
                    fp.delete();
                fp.createNewFile();
                out = new FileOutputStream(fp);
                out.write(bytes);
                out.close();
                if (fp.length() > 0) {
                    tv_status.setText("写入成功\n");
                }
                SystemClock.sleep(2000);
                in = new FileInputStream(fp);
                in.read(bytesRead);
                in.close();
                if (bytesRead.length > 0) {
                    tv_status.append("读出成功：" + new String(bytesRead));
                    ((TestActivity) getActivity()).next(TestCode.Browser_3G);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

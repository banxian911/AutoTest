package com.example.autotest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;

import android.util.Log;

public class MainActivity extends Activity implements
        AdapterView.OnItemClickListener {

    private ListView listView;
    private TestItemAdapter testItemAdapter;
    SharedPreferences preferences;
    Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(android.R.id.list);
        listView.addHeaderView(LayoutInflater.from(this).inflate(
                R.layout.list_header, null));
        listView.setOnItemClickListener(this);
        testItemAdapter = new TestItemAdapter();
        listView.setAdapter(testItemAdapter);
    }
    
    @Override
    protected void onResume() {
        AutoTestApp.initTime();
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (position == 0) {
            Log.i("hgg", "点击开始测试");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClass(this, TestActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            setTimeGap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTimeGap() {
	    
	    LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.settings, null);
        final EditText et_LCDBackLight_time = (EditText) view.findViewById(R.id.et_LCDBackLight_time);
        final EditText et_flight_time = (EditText) view.findViewById(R.id.et_flight_time);
        final EditText et_FlightMode_time = (EditText) view.findViewById(R.id.et_FlightMode_time);
        final EditText et_vibrate_time = (EditText) view.findViewById(R.id.et_vibrate_time);
        final EditText et_guester_time = (EditText) view.findViewById(R.id.et_guester_time);
        final EditText et_video_time = (EditText) view.findViewById(R.id.et_video_time);
        final EditText et_music_time = (EditText) view.findViewById(R.id.et_music_time);
        preferences = this.getSharedPreferences("test_option", Context.MODE_PRIVATE);
        editor = preferences.edit();
		final EditText et = new EditText(this);
		et.setRawInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
			new AlertDialog.Builder(this).setTitle("设置时间间隔")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				    String LCDBackLight_time = et_LCDBackLight_time.getText().toString();
				    String flight_time = et_flight_time.getText().toString();
				    String FlightMode_time = et_FlightMode_time.getText().toString();
				    String vibrate_time = et_vibrate_time.getText().toString();
				    String guester_time = et_guester_time.getText().toString();
				    String video_time = et_video_time.getText().toString();
				    String music_time = et_music_time.getText().toString();
				    
				    
				    if(LCDBackLight_time!=null && !LCDBackLight_time.equals("")){
				        editor.putInt("LCDBackLight_time", Integer.parseInt(LCDBackLight_time));
				    }
				    if(flight_time!=null && !flight_time.equals("")){
                        editor.putInt("flight_time", Integer.parseInt(flight_time));
                    }
				    if(FlightMode_time!=null && !FlightMode_time.equals("")){
                        editor.putInt("FlightMode_time", Integer.parseInt(FlightMode_time));
                    }
				    if(vibrate_time!=null && !vibrate_time.equals("")){
                        editor.putInt("vibrate_time", Integer.parseInt(vibrate_time));
                    }
				    if(guester_time!=null && !vibrate_time.equals("")){
                        editor.putInt("guester_time", Integer.parseInt(guester_time));
                    }
				    if(video_time!=null && !video_time.equals("")){
                        editor.putInt("video_time", Integer.parseInt(video_time));
                    }
				    if(music_time!=null && !music_time.equals("")){
                        editor.putInt("music_time", Integer.parseInt(music_time));
                    }
                    editor.commit();
				}
			})
		.setNegativeButton("取消", null)
			.show();
	}
}

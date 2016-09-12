package com.example.autotest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class TestItemAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return AutoTestApp.getInstance().getTestItems().size();
    }

    @Override
    public Object getItem(int position) {
        return AutoTestApp.getInstance().getTestItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = null;
        if (convertView != null) {
            holder = (viewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(AutoTestApp.getInstance()).inflate(R.layout.test_item, null);
            holder = new viewHolder();
            holder.testItemNo = (TextView) convertView.findViewById(R.id.testItemNo);
            holder.testItemName = (TextView) convertView.findViewById(R.id.testItem);
            holder.test_chose = (CheckBox) convertView.findViewById(R.id.cb_test);
            holder.test_des = (TextView) convertView.findViewById(R.id.testItem_des);
            convertView.setTag(holder);
        }

        final Util.TestCode item = AutoTestApp.getInstance().getTestItems().get(position);
        holder.testItemNo.setText(String.valueOf(position + 1));
        holder.testItemName.setText(item.toString());
        String des = item.getDes();
        if(des!=null){
            holder.test_des.setText(des);
            holder.test_des.setVisibility(View.VISIBLE);
        }else{
            holder.test_des.setText("");
            holder.test_des.setVisibility(View.GONE);
        }
        if(item.getCode()==1001){
            holder.test_chose.setBackgroundResource(R.drawable.btn_check_on_disable);
            holder.test_chose.setClickable(false);
        } else {
            holder.test_chose.setClickable(true);
            holder.test_chose.setBackgroundResource(R.drawable.ck_selector);
            holder.test_chose.setChecked(item.isChose());
            holder.test_chose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (((CheckBox) arg0).isChecked()) {
                        item.setChose(true);
                    } else {
                        item.setChose(false);
                    }

                }
            });
        }
        return convertView;
    }

    class viewHolder {
        TextView testItemNo;
        TextView testItemName;
        CheckBox test_chose;
        TextView test_des;
    }
}

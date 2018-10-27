package wuxian.me.stkapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wuxian on 27/10/2018.
 */

public class CSVAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> list;
    public CSVAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.csv_item,null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.csv_file_name);
        name.setText(list.get(position));
        return convertView;
    }
}

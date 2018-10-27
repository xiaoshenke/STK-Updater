package wuxian.me.stkapp;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wuxian.me.stkapi.CSVUtil;
import wuxian.me.stkapi.ITodayAllListener;
import wuxian.me.stkapi.TodayAll;

public class MainActivity extends AppCompatActivity {

    private TextView mProgressText;
    private TextView mDownloadText;
    private ListView mListView;
    private CSVAdapter mAdapter;
    private String mCsvName;
    private File mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String name = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        final String csvName = name + ".csv";
        mCsvName = csvName;
        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        setContentView(R.layout.activity_main);

        mProgressText = (TextView) findViewById(R.id.progress);
        mDownloadText = (TextView) findViewById(R.id.download);
        mListView = (ListView) findViewById(R.id.listview);

        initView();
    }

    private List<String> listCSVFiles() {
        final List<String> csvs = new ArrayList<>();
        File path = mPath;
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".csv")) {
                    csvs.add(f.getName());
                }
            }
        }
        return csvs;
    }

    private void initView() {

        final List<String> csvs = listCSVFiles();

        mProgressText.setVisibility(View.INVISIBLE);
        mAdapter = new CSVAdapter(mProgressText.getContext());
        mAdapter.setData(csvs);
        mListView.setAdapter(mAdapter);

        mDownloadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listCSVFiles().contains(mCsvName)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("本地文件已经存在,是否依旧下载？");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doDownload();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();

                    return;
                }
                doDownload();
            }
        });

        mAdapter.notifyDataSetChanged();

    }

    private void doDownload() {
        TodayAll t = new TodayAll();
        //t.setStart(1);
        //t.setEnd(1);

        t.setListener(new ITodayAllListener() {
            @Override
            public void onPerformReqStart(final String s, final Integer integer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressText.setText("目前在下载 type: " + s + " page: " + integer);
                    }
                });
            }

            @Override
            public void onPerformReqEnd(final String s, final Integer integer) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressText.setText("完成下载 type: " + s + " page: " + integer);
                    }
                });
            }

            @Override
            public void onRequestStart() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressText.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onRequestSuccess(final List<TodayAll.Item> list) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressText.setVisibility(View.INVISIBLE);

                        CSVUtil.writeTodayAllItems(mPath.getAbsolutePath() + File.separator + mCsvName,list);

                        mAdapter.setData(listCSVFiles());
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void onRequestFail(String s) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressText.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onRequestFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mProgressText.setVisibility(View.VISIBLE);
                        //mProgressText.setText("下载结束！");
                    }
                });
            }
        });
        t.getTodayAll();
    }
}

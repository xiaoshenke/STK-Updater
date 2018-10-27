package wuxian.me.stkapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wuxian on 27/10/2018.
 */

public class CocoExceptionHandler implements Thread.UncaughtExceptionHandler {

    public final static String RESTART_INTENT_KEY = "intent_restart";
    public final static int CRASHED_CODE = 1101;
    private PendingIntent m_restartIntent;
    private Context ctx = null;

    public static String sdcarRootPath;

    static {
        try {
            //sdcarRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + App.getContext().getPackageName() + "/log/";

            sdcarRootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    + File.separator
                    + App.getContext().getPackageName()
                    + "/log";

            //saveFile(sdcarRootPath + File.separator + "tmp.txt", "hello");

        } catch (Exception e) {

            System.out.print(e);

        }
    }

    public CocoExceptionHandler(Context ctx) {
        this.ctx = ctx;

    }

    @Override
    public void uncaughtException(Thread t, Throwable th) {

//        th.printStackTrace();
        try {
            //将Crash的Exception信息保存到SDcard中
            savaInfoToSD(ctx, th);
            //Thread.sleep(1000);
            //System.exit(-1);

        } catch (Throwable e1) {

            System.out.print(e1);
        } finally {

        }
    }

    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        mPrintWriter.close();
        return mStringWriter.toString();
    }

    /**
     * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
     *
     * @param context
     * @param ex
     * @return
     */
    private String savaInfoToSD(Context context, Throwable ex) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : HelperFunc.obtainSimpleInfo(context).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }

        sb.append(obtainExceptionInfo(ex));

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(sdcarRootPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                fileName = dir.toString() + File.separator + new SimpleDateFormat("yyyyMMdd_HHmmss_", Locale.US).format(new Date());
                saveFile(fileName, sb.toString());
            } catch (Throwable th) {
            }
        }

        return fileName;
    }

    private static void saveFile(String f, String content) throws Exception {
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
    }

}

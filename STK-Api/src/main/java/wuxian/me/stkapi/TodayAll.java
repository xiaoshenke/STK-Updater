package wuxian.me.stkapi;

import com.alibaba.fastjson.JSON;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wuxian on 27/10/2018.
 */
public class TodayAll implements Runnable {

    private Thread mThread = null;
    private boolean started = false;
    private List<Item> mList = null;
    private int mStart = 1;
    private int mEnd = 60;
    private ITodayAllListener mListener = null;

    public void setListener(ITodayAllListener listener) {
        mListener = listener;
    }

    public void setStart(int start) {
        mStart = start;
    }

    public void setEnd(int end) {
        mEnd = end;
    }

    public void getTodayAll() {

        if (started) {
            return;
        }
        if (mThread == null) {
            mThread = new Thread(this);
            mThread.start();
            started = true;
        }
    }

    private void realRun() throws Exception {

        String type = "hs_a";

        List<Item> full = new ArrayList<Item>();
        for (int i = mStart; i < mEnd; i++) {

            if (mListener != null) {
                mListener.onPerformReqStart(type, i);
            }
            List<Item> tmp = performReq(type, i);

            if (mListener != null) {
                mListener.onPerformReqEnd(type, i);
            }

            if (tmp.size() == 0) {
                continue;
            }
            full.addAll(tmp);
        }

        type = "shfxjs";
        if (mListener != null) {
            mListener.onPerformReqStart(type, 1);
        }
        List<Item> tmp = performReq(type, 1);
        if (mListener != null) {
            mListener.onPerformReqEnd(type, 1);
        }
        if (tmp.size() > 0) {
            full.addAll(tmp);
        }


        mList = new ArrayList<Item>();
        for (int i=0;i<full.size();i++) {
            if (full.get(i).volume > 0){
                mList.add(full.get(i));
            }
        }
    }

    private List<Item> performReq(String type, Integer page) throws Exception {

        HttpClient client = new DefaultHttpClient();

        ((DefaultHttpClient) client).setHttpRequestRetryHandler(
                new DefaultHttpRequestRetryHandler(3, false));

        String url = String.format("http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?num=80&sort=code&asc=0&node=%s&symbol=&_s_r_a=page&page=%s"
                , "hs_a", page);

        HttpGet req = new HttpGet(url);

        HttpResponse response = null;

        response = client.execute(req);

        if (response.getStatusLine().getStatusCode() != 200) {

            System.out.println("res.statusCode is " + response.getStatusLine().getStatusCode());
            throw new Exception("performReq of type: " + type + " page: " + page + " ,but statuscode is "
                    + response.getStatusLine().getStatusCode());
        }

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(),"GBK"));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        if (result.toString().equals("null")) {
            return new ArrayList<Item>(0);
        }

        List<Item> itemList = JSON.parseArray(result.toString(), Item.class);

        return itemList;
    }

    public void run() {
        try {

            if (mListener != null) {
                mListener.onRequestStart();
            }
            realRun();

            if (mListener != null) {
                mListener.onRequestSuccess(mList);
            }
        } catch (Exception e) {

            System.out.println(e);

            if (mListener != null) {
                mListener.onRequestFail(e.getMessage());
            }
        } finally {
            started = false;
            mThread = null;
        }

    }

    public static class Item {
        public String symbol;
        public String code;
        public String name;
        public String trade;
        public String pricechange;
        public String changepercent;
        public String buy;
        public String sell;
        public String settlement;
        public String open;
        public String high;
        public String low;
        public Long volume;
        public String amount;
        public String ticktime;
        public String per;
        public String pb;
        public String mktcap;
        public String nmc;
        public String turnoverratio;

        @Override
        public String toString() {
            return "Item{" +
                    "symbol='" + symbol + '\'' +
                    ", code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", trade='" + trade + '\'' +
                    ", pricechange='" + pricechange + '\'' +
                    ", changepercent='" + changepercent + '\'' +
                    ", buy='" + buy + '\'' +
                    ", sell='" + sell + '\'' +
                    ", settlement='" + settlement + '\'' +
                    ", open='" + open + '\'' +
                    ", high='" + high + '\'' +
                    ", low='" + low + '\'' +
                    ", volume='" + volume + '\'' +
                    ", amount='" + amount + '\'' +
                    ", ticktime='" + ticktime + '\'' +
                    ", per='" + per + '\'' +
                    ", pb='" + pb + '\'' +
                    ", mktcap='" + mktcap + '\'' +
                    ", nmc='" + nmc + '\'' +
                    ", turnoverratio='" + turnoverratio + '\'' +
                    '}';
        }
    }


    public static void main(String[] args) throws Exception {
        final String path = "/Users/wuxian/Desktop/today.csv";
        TodayAll t = new TodayAll();
        t.setStart(1);
        t.setEnd(3);
        t.setListener(new ITodayAllListener() {
            public void onPerformReqStart(String type, Integer page) {
                System.out.println("onPerformReqStart type: " + type + " page: " + page);
            }

            public void onPerformReqEnd(String type, Integer page) {
                System.out.println("onPerformReqEnd type: " + type + " page: " + page);

            }

            public void onRequestStart() {
                System.out.println("onStart");
            }

            public void onRequestSuccess(List<Item> list) {
                System.out.println("onSuccess,size: " + list.size() + " \nlist: " + list);
                CSVUtil.writeTodayAllItems(path,list);
            }

            public void onRequestFail(String msg) {
                System.out.println("onFail,msg: " + msg);
            }
        });
        t.getTodayAll();
    }
}

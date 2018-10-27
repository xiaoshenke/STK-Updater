package wuxian.me.stkapi;

import java.util.List;

/**
 * Created by wuxian on 27/10/2018.
 */
public interface ITodayAllListener {

    void onPerformReqStart(String type, Integer page);

    void onPerformReqEnd(String type, Integer page);

    void onRequestStart();

    void onRequestSuccess(List<TodayAll.Item> list);

    void onRequestFail(String message);

    void onRequestFinish();
}

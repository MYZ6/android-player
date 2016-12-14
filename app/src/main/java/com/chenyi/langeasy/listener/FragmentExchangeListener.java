package com.chenyi.langeasy.listener;

/**
 * Created by liyzh on 2016/10/18.
 */
public interface FragmentExchangeListener {
    void toList(int songIndex);

    void query(String condition);

    /**
     * history query
     * @param condition
     */
    void hquery(String condition);

     void toLearning(int songIndex);

    /**
     * history to learn
     * @param sentenceid
     */
    void h2learn(final Integer sentenceid);

}

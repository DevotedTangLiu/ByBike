package com.example.bybike.friends;

import java.util.Comparator;

import com.example.bybike.db.model.UserBean;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<UserBean> {

    public int compare(UserBean o1, UserBean o2) {
        String o1FirstChar = o1.getPinyinname().substring(0, 1);
        String o2FirstChar = o2.getPinyinname().substring(0, 1);

        if (o1FirstChar.equals("@") || o2FirstChar.equals("#")) {
            return -1;
        } else if (o1FirstChar.equals("#") || o2FirstChar.equals("@")) {
            return 1;
        } else {
            return o1FirstChar.compareTo(o2FirstChar);
        }
    }

    
}

package com.braille.os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.everything.providers.android.contacts.Contact;

/**
 * Created by Sudarshan Sunder on 7/16/2016.
 */

public class Utils {

    public static void sort(List<Contact> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j).displayName.compareTo(list.get(j + 1).displayName) > 0) {
                    Collections.swap(list, j, j + 1);
                }
            }
        }
    }

    public static List<Contact> removeDuplicates(List<Contact> list) {
        HashMap<String, Contact> map = new HashMap<>();
        for (Contact obj : list) {
            char[] phoneNo = obj.phone.toCharArray();
            obj.phone = "";
            for (char ch : phoneNo) {
                if (Character.isDigit(ch) || ch == '+') {
                    obj.phone += ch;
                }
            }
            if (map.get(obj.phone) == null)
                map.put(obj.phone, obj);
        }
        List<Contact> newList = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            newList.add((Contact) pair.getValue());
        }
        return newList;
    }
}

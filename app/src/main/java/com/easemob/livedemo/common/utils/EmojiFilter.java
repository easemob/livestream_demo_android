package com.easemob.livedemo.common.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.HashSet;
import java.util.Set;

public class EmojiFilter implements InputFilter {

    private static Set<String> filterSet = null;
    private static Set<Scope> scopeSet = null;

    private class Scope {
        int start;
        int end;

        @Override
        public boolean equals(Object o) {
            if (o instanceof Scope) {
                Scope scope = (Scope) o;
                if (scope.start == start && scope.end == end) {
                    return true;
                }
            }
            return super.equals(o);
        }
    }

    private static void addUnicodeRangeToSet(Set<String> set, int start, int end) {
        if (set == null) {
            return;
        }
        if (start > end) {
            return;
        }


        for (int i = start; i <= end; i++) {
            filterSet.add(new String(new int[]{i}, 0, 1));
        }
    }

    private static void addUnicodeRangeToSet(Set<String> set, int code) {
        if (set == null) {
            return;
        }
        filterSet.add(new String(new int[]{code}, 0, 1));
    }

    static {
        filterSet = new HashSet<String>();
        scopeSet = new HashSet<>();

        // See http://apps.timwhitlock.info/emoji/tables/unicode

        // 1. Emoticons ( 1F601 - 1F64F )
        addUnicodeRangeToSet(filterSet, 0x1F601, 0X1F64F);

        // 2. Dingbats ( 2702 - 27B0 )
        addUnicodeRangeToSet(filterSet, 0x2702, 0X27B0);

        // 3. Transport and map symbols ( 1F680 - 1F6C0 )
        addUnicodeRangeToSet(filterSet, 0X1F680, 0X1F6C0);

        // 4. Enclosed characters ( 24C2 - 1F251 )
        addUnicodeRangeToSet(filterSet, 0X24C2);
        addUnicodeRangeToSet(filterSet, 0X1F170, 0X1F251);

        // 6a. Additional emoticons ( 1F600 - 1F636 )
        addUnicodeRangeToSet(filterSet, 0X1F600, 0X1F636);

        // 6b. Additional transport and map symbols ( 1F681 - 1F6C5 )
        addUnicodeRangeToSet(filterSet, 0X1F681, 0X1F6C5);

        // 6c. Other additional symbols ( 1F30D - 1F567 )
        addUnicodeRangeToSet(filterSet, 0X1F30D, 0X1F567);

        // 5. Uncategorized
        addUnicodeRangeToSet(filterSet, 0X1F004);
        addUnicodeRangeToSet(filterSet, 0X1F0CF);
        // 6c. Other additional symbols ( 1F30D - 1F567 )

        addUnicodeRangeToSet(filterSet, 0X1F300, 0X1F30D);
        addUnicodeRangeToSet(filterSet, 0X1F5FB, 0X1F5FF);
        addUnicodeRangeToSet(filterSet, 0X00A9);
        addUnicodeRangeToSet(filterSet, 0X00AE);
        addUnicodeRangeToSet(filterSet, 0X0023);
        //addUnicodeRangeToSet(filterSet, 0X0030, 0X0039);
        //addUnicodeRangeToSet(filterSet, 0X203C, 0X24C2);
        addUnicodeRangeToSet(filterSet, 0X203C);
        addUnicodeRangeToSet(filterSet, 0X2049);
        addUnicodeRangeToSet(filterSet, 0X20E3);
        addUnicodeRangeToSet(filterSet, 0X2122);
        addUnicodeRangeToSet(filterSet, 0X2139);
        addUnicodeRangeToSet(filterSet, 0X2194, 0X2199);
        addUnicodeRangeToSet(filterSet, 0X21A9, 0X21AA);
        addUnicodeRangeToSet(filterSet, 0X231A, 0X231B);
        addUnicodeRangeToSet(filterSet, 0X23E9, 0X23EC);
        addUnicodeRangeToSet(filterSet, 0X23F0);
        addUnicodeRangeToSet(filterSet, 0X23F3);
        addUnicodeRangeToSet(filterSet, 0X25AA, 0X25AB);
        addUnicodeRangeToSet(filterSet, 0X25FB, 0X25FE);
        addUnicodeRangeToSet(filterSet, 0X2600, 0X26FE);
        addUnicodeRangeToSet(filterSet, 0X2934, 0X2935);
        addUnicodeRangeToSet(filterSet, 0X2B05, 0X2B07);
        addUnicodeRangeToSet(filterSet, 0X2B1B, 0X2B1C);
        addUnicodeRangeToSet(filterSet, 0X2B50);
        addUnicodeRangeToSet(filterSet, 0X2B55);
        addUnicodeRangeToSet(filterSet, 0X3030);
        addUnicodeRangeToSet(filterSet, 0X303D);
        addUnicodeRangeToSet(filterSet, 0X3297);
        addUnicodeRangeToSet(filterSet, 0X3299);
    }

    public EmojiFilter() {
        super();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                               int dend) {
        // check black-list set
//        Iterator<String> iterator = filterSet.iterator();
//        while (iterator.hasNext()) {
//            String filter = iterator.next();
//            if (filter.equals(source.toString())) {
//                LogUtil.e(filter + " length： " + filter.length() +
//                        " ；bytes length： " + filter.getBytes().length);
//                for (int i= 0; i < source.length(); i++){
//                    LogUtil.e(Integer.toHexString(source.charAt(i)));
//                }
//                return "";
//            }
//        }
        if (filterSet.contains(source.toString())) {
            return "";
        }
        return source;
    }
}

package cn.ldm.commons.utils.etc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音，能处理多音字
 */

public class PinyinUtil {

    private static Map<String, List<String>> pinyinMap = new HashMap<String, List<String>>();

    static {
        try {
            if (pinyinMap == null || pinyinMap.size() == 0) {
                initPinyin("/duoyinzi_dic.txt");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将某个字符串的首字母 大写
     *
     * @param str
     * @return
     */
    public static String convertInitialToUpperCase(String str, boolean toUpperCase) {
        if (str == null) {
            return null;
        }
        if (!toUpperCase) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];
            if (i == 0) {
                sb.append(String.valueOf(ch).toUpperCase());
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    /**
     * 查询字符串包含的多音词，如果有多音词返回多条记录，否则返回一条
     *
     * @param str
     * @return
     */
    public static List<String> duoYinAllSpellList(String str) {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        Map<Integer, String[]> map = new HashMap<>(); //多音字的数量
        List list = new ArrayList<>(); //汉字单个音与多音字组成拼音的集合
        StringBuffer sr = new StringBuffer(); //汉字单个音的全拼多个音的占位符,例:重庆丽都（{0}qingli{1}
        try {
            char[] chars = str.toCharArray();
            for (int i = 0, j = 0; i < chars.length; i++) {
                if (chars[i] > 128) {
                    String[] results = PinyinHelper.toHanyuPinyinStringArray(chars[i], defaultFormat);
                    if (results == null) {    //非中文
                        sr.append("");
                    } else if (results.length == 1) {
                        sr.append(filterSpecialSpell(results[0]));
                    } else if (results[0].equals(results[1])) {    //非多音字 有多个音，取第一个
                        sr.append(filterSpecialSpell(results[0]));
                    } else {
                        sr.append("{" + j + "}");
                        for (int y = 0; y < results.length; y++) {
                            results[y] = filterSpecialSpell(results[y]);
                        }
                        map.put(j, results);
                        j++;
                    }
                }
            }
            //如果包含多音词
            if (map != null && map.size() > 0) {
                Map<Integer, String[]> valueMap = new HashMap<>();//多音词两两的组合
                for (Map.Entry entry : map.entrySet()) {
                    int idx = (int) entry.getKey();
                    String[] values = (String[]) entry.getValue();
                    //第一次循环
                    if (idx == 0) {
                        for (int i = 0; i < values.length; i++) {
                            String[] arr = {values[i]};
                            valueMap.put(i, arr);
                        }
                    } else {
                        Map<Integer, String[]> tempMap = new HashMap<>();//临时map
                        int y = 0;//map的key值
                        for (Map.Entry entry1 : valueMap.entrySet()) {
                            String[] arr = (String[]) entry1.getValue();
                            for (int i = 0; i < values.length; i++) {
                                String[] tempArr = new String[arr.length + 1];
                                for (int j = 0; j < idx; j++) {
                                    tempArr[j] = arr[j];
                                }
                                tempArr[idx] = values[i];
                                tempMap.put(y, tempArr);
                                y++;
                            }
                        }
                        valueMap = tempMap;
                    }
                }
                if (valueMap != null && valueMap.size() > 0) {
                    for (Map.Entry entry1 : valueMap.entrySet()) {
                        String[] arr = (String[]) entry1.getValue();
                        list.add(TextUtil.format(sr.toString(), arr));
                    }
                }
            } else {
                list.add(sr.toString());
            }
            return list;
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        return null;
    }

    /**
     * 多音词组成的拼音
     *
     * @param str
     */
    public static String duoYinStringToList(String str) {
        List<String> list = duoYinAllSpellList(str);
        String value = "";
        if (list != null) {
            for (String string : list) {
                value += string + ",";
            }
            value = value.substring(0, value.lastIndexOf(","));
        }
        return value;
    }

    /**
     * 过滤u:
     *
     * @param py
     * @return
     */
    public static String filterSpecialSpell(String py) {
        if (py.contains("u:")) {    //过滤 u:
            return py.replace("u:", "v");
        }
        return py;
    }

    /**
     * 截取拼音首字母
     *
     * @param str
     * @return
     */
    public static String cutFirstPingYing(String str) {
        if (str.length() >= 2) {
            if (cocky.indexOf(str.substring(0, 2)) != -1)
                str = str.substring(0, 2);
            else
                str = str.substring(0, 1);
        }
        return str;
    }

    /**
     * 汉字转拼音 忽略多音
     *
     * @param chinese
     * @return
     */
    public static String chineseToPinyin(String chinese) {
        if (chinese == null || "".equals(chinese)) {
            return "";
        }
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        char[] arr = chinese.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];
            if (ch > 128) { // 非ASCII码
                // 取得当前汉字的所有全拼
                try {
                    String[] results = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);

                    if (results == null) {    //非中文
                        pinyin.append("");
                    } else {
                        String py = results[0];
                        if (py.contains("u:")) {    //过滤 u:
                            py = py.replace("u:", "v");
                        }
                        pinyin.append(convertInitialToUpperCase(py, false));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyin.append(arr[i]);
            }
        }
        return pinyin.toString();
    }

    /**
     * 初始化 所有的多音字词组
     *
     * @param fileName
     */
    public static void initPinyin(String fileName) throws FileNotFoundException {
        // 读取多音字的全部拼音表;
        InputStream file = PinyinUtil.class.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(file));
        String s = null;
        try {
            while ((s = br.readLine()) != null) {
                if (s != null) {
                    String[] arr = s.split("#");
                    String pinyin = arr[0];
                    String chinese = arr[1];
                    if (chinese != null) {
                        String[] strs = chinese.split(" ");
                        List<String> list = Arrays.asList(strs);
                        pinyinMap.put(pinyin, list);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 得到全拼，全部小写
     * @param chinese
     * @return
     */
    public static String convertChineseToPinyinWithLowerCase(String chinese){
        return convertChineseToPinyin(chinese,false);
    }

    /**
     * 得到中文简拼
     */
    public static String getChineseSimpleSpell(String chinese){
        String str = convertChineseToPinyin(chinese,true);
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c:chars){
            if(Character.isUpperCase(c))  sb.append(c);
        }
        if(sb.length() <= 0) return "";
        else return sb.toString().toLowerCase();
    }

    /**
     * @param chinese
     * @return
     */
    public static String convertChineseToPinyin(String chinese,boolean toUpperCase) {
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        char[] arr = chinese.toCharArray();
        for (int i = 0; i < arr.length; i++) {

            char ch = arr[i];

            if (ch > 128) { // 非ASCII码
                // 取得当前汉字的所有全拼
                try {

                    String[] results = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);

                    if (results == null) {    //非中文

                        return "";
                    } else {

                        int len = results.length;

                        if (len == 1) { // 不是多音字
                            String py = results[0];
                            if (py.contains("u:")) {    //过滤 u:
                                py = py.replace("u:", "v");
                                //System.out.println("filter u:" + py);
                            }
                            pinyin.append(convertInitialToUpperCase(py, toUpperCase));

                        } else if (results[0].equals(results[1])) {    //非多音字 有多个音，取第一个
                            pinyin.append(convertInitialToUpperCase(results[0], toUpperCase));
                        } else { // 多音字

                            //System.out.println("多音字：" + ch);

                            int length = chinese.length();

                            boolean flag = false;

                            String s = null;

                            List<String> keyList = null;

                            for (int x = 0; x < len; x++) {

                                String py = results[x];

                                if (py.contains("u:")) {    //过滤 u:
                                    py = py.replace("u:", "v");
                                    //System.out.println("filter u:" + py);
                                }

                                keyList = pinyinMap.get(py);

                                if (i + 3 <= length) {    //后向匹配2个汉字  大西洋
                                    s = chinese.substring(i, i + 3);
                                    if (keyList != null && (keyList.contains(s))) {
                                        //									if (value != null && value.contains(s)) {

                                        //System.out.println("last 2 > " + py);
                                        //										pinyin.append(results[x]);
                                        pinyin.append(convertInitialToUpperCase(py, toUpperCase));
                                        flag = true;
                                        break;
                                    }
                                }

                                if (i + 2 <= length) {    //后向匹配 1个汉字  大西
                                    s = chinese.substring(i, i + 2);
                                    if (keyList != null && (keyList.contains(s))) {

                                        //System.out.println("last 1 > " + py);
                                        //										pinyin.append(results[x]);
                                        pinyin.append(convertInitialToUpperCase(py, toUpperCase));
                                        flag = true;
                                        break;
                                    }
                                }

                                if ((i - 2 >= 0) && (i + 1 <= length)) {    // 前向匹配2个汉字 龙固大
                                    s = chinese.substring(i - 2, i + 1);
                                    if (keyList != null && (keyList.contains(s))) {

                                        //System.out.println("before 2 < " + py);
                                        //										pinyin.append(results[x]);
                                        pinyin.append(convertInitialToUpperCase(py, toUpperCase));
                                        flag = true;
                                        break;
                                    }
                                }

                                if ((i - 1 >= 0) && (i + 1 <= length)) {    // 前向匹配1个汉字   固大
                                    s = chinese.substring(i - 1, i + 1);
                                    if (keyList != null && (keyList.contains(s))) {

                                        //System.out.println("before 1 < " + py);
                                        //										pinyin.append(results[x]);
                                        pinyin.append(convertInitialToUpperCase(py, toUpperCase));
                                        flag = true;
                                        break;
                                    }
                                }

                                if ((i - 1 >= 0) && (i + 2 <= length)) {    //前向1个，后向1个      固大西
                                    s = chinese.substring(i - 1, i + 2);
                                    if (keyList != null && (keyList.contains(s))) {

                                        //System.out.println("before last 1 <> " + py);
                                        //										pinyin.append(results[x]);
                                        pinyin.append(convertInitialToUpperCase(py, toUpperCase));
                                        flag = true;
                                        break;
                                    }
                                }
                            }

                            if (!flag) {    //都没有找到，匹配默认的 读音  大

                                s = String.valueOf(ch);

                                for (int x = 0; x < len; x++) {

                                    String py = results[x];

                                    if (py.contains("u:")) {    //过滤 u:
                                        py = py.replace("u:", "v");
                                        //System.out.println("filter u:");
                                    }

                                    keyList = pinyinMap.get(py);

                                    if (keyList != null && (keyList.contains(s))) {

                                        //System.out.println("default = " + py);
                                        //										pinyin.append(results[x]);	//如果不需要拼音首字母大写 ，直接返回即可
                                        pinyin.append(convertInitialToUpperCase(py, toUpperCase));//拼音首字母 大写
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyin.append(arr[i]);
            }
        }
        return pinyin.toString();
    }

    public static String filterSpecialChar(String str) {
        if (str == null) {
            return "";
        }
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 翘舌音符
     */
    static final String cocky = "sh,ch,zh";

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变 contain:  true带翘舌 false不带翘舌，不走字典
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines, boolean contain) {
        chines = filterSpecialChar(chines);
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                String all = chineseToPinyin(String.valueOf(nameChar[i]));
                if (contain) {
                    if (all.length() >= 2) {
                        if (cocky.indexOf(all.substring(0, 2)) != -1)
                            pinyinName.append(all.substring(0, 2));
                        else
                            pinyinName.append(all.substring(0, 1));
                    } else {
                        pinyinName.append(all);
                    }
                } else {
                    if (all.length() >= 1)
                        pinyinName.append(all.substring(0, 1));
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        return pinyinName.toString();
    }

    public static String converToAllSpell(String chines) {
        chines = filterSpecialChar(chines);
        return duoYinStringToList(chines);
    }

    public static void main(String[] args) {
        String str = "长桥";
        System.out.println(convertChineseToPinyin(str,false));
        System.out.println(converterToFirstSpell(str,false));
        System.out.println(getChineseSimpleSpell(str));
    }
}

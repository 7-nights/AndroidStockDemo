package com.edward.stock.util;

import com.edward.stock.event.SearchSuggestionEvent;
import com.edward.stock.model.BasicStockInfo;
import com.edward.stock.model.RealTimeInfo;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by 朱凌峰 on 7-30.
 */
public class StockUtil {
    private static final String STOCK_RE = "(sz|sh)~.+?\\^";
    private static Pattern patternStock= Pattern.compile(STOCK_RE);
    private static String singleStock;
    private static Matcher matcherStock;


    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    public static void searchStock(String key) {
        String url = "http://smartbox.gtimg.cn/s3/index_app.php?q=", tail = "&t=all";
        final Request request = new Request.Builder()
                .url(url + key + tail)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                ArrayList<BasicStockInfo> results = new ArrayList<BasicStockInfo>();
                String[] temp;
                matcherStock = patternStock.matcher(unicodeToUtf8(response.body().string()));
                while (matcherStock.find()) {
                    singleStock = matcherStock.group();
                    temp = singleStock.split("~");
                    results.add(new BasicStockInfo(temp[1], temp[2], temp[0]));
                }
                EventBus.getDefault().post(new SearchSuggestionEvent(results));
            }
        });
    }

}

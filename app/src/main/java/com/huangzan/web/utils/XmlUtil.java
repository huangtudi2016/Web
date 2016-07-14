package com.huangzan.web.utils;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzan on 16/7/13.
 */
public class XmlUtil {
    private InputStream inputStream;

    public XmlUtil(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    //解析流成list
    public List getAllElement() throws XmlPullParserException, IOException {
        List list = null;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(inputStream, "utf-8");
        //事件
        int eventType = xpp.getEventType();
        String elementName = "";
//        Xml开始
        if (XmlPullParser.START_DOCUMENT==eventType){
            list=new ArrayList();
        }
        //当事件不结束时
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                //一个标签
                case XmlPullParser.START_TAG:
                    //获得标签名称
                    elementName = xpp.getName();
//                    break;
                //获得标签内容
                case XmlPullParser.TEXT:
                    if (elementName.equals("string")) {
                        list.add(xpp.getText());
                    }
                    break;
                //标签结束
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = xpp.next();
        }
        return list;
    }

}
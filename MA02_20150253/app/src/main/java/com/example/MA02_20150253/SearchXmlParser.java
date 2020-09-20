package com.example.MA02_20150253;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class SearchXmlParser {
    public enum TagType { NONE, TITLE, CATEGORY, ADDRESS,  DESCRIPTION};

    public SearchXmlParser() {
    }

    public ArrayList<MySearchDto> parse(String xml) {

        ArrayList<MySearchDto> resultList = new ArrayList();
        MySearchDto dto = null;

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();      //XmlPullParser를 생성하기 위한 factory 준비
            XmlPullParser parser = factory.newPullParser();     // XmlPullParser 생성
            parser.setInput(new StringReader(xml));     //String으로 전달 받은 xml을  XmlPullParser에 설정


            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {       //종료 조건: eventType == 문서의 마지막을 알리는 이벤트, 그 전까지는 계속 이벤트를 구분하여 반복 수행
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:       //시작태그를 읽음. 태그에 따라 다른 작업 수행.
                        if (parser.getName().equals("item")) {
                            dto = new MySearchDto();      //새로운 dto를 생성하는 부분
                        } else if (parser.getName().equals("title")) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals("description")) {
                            if (dto != null) tagType = TagType.CATEGORY;
                        } else if (parser.getName().equals("link")) {
                            if (dto != null) tagType = TagType.ADDRESS;
                        }
                        break;
                    case XmlPullParser.END_TAG:     //종료 태그를 읽음. 리스트에 새로 생성된 dto 추가
                        if (parser.getName().equals("item")) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:       //태그 사이의 값
                        switch(tagType) {       //값의 내용에 따라 dto에 알맞는 내용을 저장.
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case CATEGORY:
                                dto.setDescription(parser.getText());
                                break;
                            case ADDRESS:
                                dto.setLink(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();      //다음 이벤트로 넘어가기
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;      //파싱 결과 반환(ArrayList).
    }
}

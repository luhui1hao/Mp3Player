package com.example.luhui1hao.xml;

import com.example.luhui1hao.model.Mp3Info;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * Created by luhui1hao on 2015/12/5.
 */
public class Mp3ListContentHandler extends DefaultHandler {
    private List<Mp3Info> infos = null;
    private Mp3Info mp3Info = null;
    private String nodeName = null;
    private String type = null;

    public Mp3ListContentHandler(List<Mp3Info> infos) {
        super();
        this.infos = infos;
    }

    public void setInfos(List<Mp3Info> infos) {
        this.infos = infos;
    }

    public List<Mp3Info> getInfos() {
        return infos;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        this.nodeName = localName;
        if (nodeName.equals("resource")) {
            mp3Info = new Mp3Info();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("resource")) {
            infos.add(mp3Info);
        }
        nodeName = "";
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String temp = new String(ch, start, length);
        switch (nodeName) {
            case "id":
                mp3Info.setId(temp);
                break;
            case "mp3":
                type = "mp3";
                break;
            case "lrc":
                type = "lrc";
                break;
            case "name":
                if(type.equals("mp3")){
                    mp3Info.setMp3Name(temp);
                }else if(type.equals("lrc")){
                    mp3Info.setLrcName(temp);
                }
                break;
            case "size":
                if(type.equals("mp3")){
                    mp3Info.setMp3Size(temp);
                }else if(type.equals("lrc")){
                    mp3Info.setLrcSize(temp);
                }
                break;
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
    }
}

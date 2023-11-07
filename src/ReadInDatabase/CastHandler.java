package ReadInDatabase;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastHandler extends DefaultHandler {
    private StringBuilder characters;
    private String currentMovie;
    private String currentStar;

    public List<SimpleEntry<String, String>> getListOfEntries() {
        return listOfEntries;
    }

    List<SimpleEntry<String, String>> listOfEntries;
    public static void main(String[] args) {
        CastHandler cast_spe = new CastHandler();
        cast_spe.runMain();

    }

    private void runMain(){
        parseDocument();
        printData();
    }

    private void printData(){
        for (SimpleEntry<String, String> tuple : listOfEntries) {
            System.out.println("(" + tuple.getKey() + ", " + tuple.getValue() + ")");
        }
        System.out.println("List of Entries:" + listOfEntries.size());
    }

    public void parseDocument(){
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            sp.parse("stanford-movies/casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void startDocument() throws SAXException {
        listOfEntries = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        characters = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("m".equalsIgnoreCase(qName)) {
            if((!currentStar.isEmpty() && !currentMovie.isEmpty() && !currentStar.equals("s a")) ){
                listOfEntries.add(new SimpleEntry<>(currentMovie, currentStar));
            }
        } else if ("f".equalsIgnoreCase(qName)) {
            currentMovie = characters.toString().strip();
            ;        }  else if ("a".equalsIgnoreCase(qName)) {
            currentStar = characters.toString().strip();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(new String(ch, start, length));
    }

}

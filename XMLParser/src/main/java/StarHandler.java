
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class StarHandler extends DefaultHandler {
    private StringBuilder characters;
    private StarData currentStar;

    public List<StarData> getStarDataList() {
        return starList;
    }

    private List<StarData> starList;

    public HashMap<String, StarData> getStarHashMap() {
        return starHashMap;
    }

    private HashMap<String,StarData> starHashMap;

    public static void main(String[] args) {
        StarHandler spe = new StarHandler();
        spe.runMain();
    }

    void runMain(){
        parseDocument();
        printStars();
    }

    private void printStars(){
        for (StarData star: starList) {
            System.out.println(star.toString());
        }
        System.out.println(starList.size());
    }
    public void parseDocument(){
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            File actorsFile = new File("stanford-movies/actors63.xml");
            InputStream inputStream= new FileInputStream(actorsFile);
            Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");

            InputSource is = new InputSource(reader);
            is.setEncoding("ISO-8859-1");
            sp.parse(is, this);


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
        starList = new ArrayList<>();
        starHashMap = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        characters = new StringBuilder();
        if ("actor".equalsIgnoreCase(qName)) {
            currentStar = new StarData();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try{
            if ("actor".equalsIgnoreCase(qName)) {
                if(currentStar.getStage_name().isBlank()){
                    currentStar.setInvalidEntry(true);
                }
                String[] names = currentStar.getStage_name().split(" ");

                if(names.length > 1){
                    StringBuilder firstName = new StringBuilder();
                    for (int i = 0; i < names.length - 1; i++) {
                        firstName.append(names[i]);
                        if (i < names.length - 2) {
                            firstName.append(" ");
                        }
                    }
                    currentStar.setFirst_name(firstName.toString());
                    currentStar.setLast_name(names[names.length -1]);
                }

                if(!currentStar.isInvalidEntry && !starHashMap.containsKey(currentStar.stage_name)){
                    starList.add(currentStar);
                    starHashMap.put(currentStar.stage_name, currentStar);
                }
            } else if ("stagename".equalsIgnoreCase(qName)) {
                currentStar.setStage_name(characters.toString().strip());
            }  else if ("dob".equalsIgnoreCase(qName)) {
                currentStar.setYear(Integer.parseInt(characters.toString().strip()));
            } else if ("firstname".equalsIgnoreCase(qName)){
                currentStar.setFirst_name(characters.toString().strip());
            } else if ("familyname".equalsIgnoreCase(qName)){
                currentStar.setLast_name(characters.toString().strip());
            }
        }catch(Exception NumberFormatException){
            currentStar.setYear(-1);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(new String(ch, start, length));
    }

    public List<StarData> getStar() {
        return starList;
    }
}

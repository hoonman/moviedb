
import java.io.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastHandler extends DefaultHandler {
    private StringBuilder characters;
    private String currentMovie;
    private String currentStar;

    private Set<String> movieDataSet;
    private Set<String> starDataSet;

    public Set<String> getMovieFoundSet() {
        return movieFoundSet;
    }

    public Set<String> getStarFoundSet() {
        return starFoundSet;
    }

    private Set<String> movieFoundSet;
    private Set<String> starFoundSet;

    public int star_not_found = 0;
    public int movie_not_found = 0;
    public int duplicate_entry = 0;

    public CastHandler(Set<String> movieDataSet1, Set<String> starDataSet1) {
        movieDataSet = movieDataSet1;
        starDataSet = starDataSet1;
    }

    public List<SimpleEntry<String, String>> getListOfEntries() {
        return listOfEntries;
    }
    public HashMap<SimpleEntry<String, String>, Boolean> getHashOfEntries() {
        return entriesMap;
    }
    List<SimpleEntry<String, String>> listOfEntries;
    private HashMap<SimpleEntry<String, String>, Boolean> entriesMap;
//    public static void main(String[] args) {
//        CastHandler cast_spe = new CastHandler();
//        cast_spe.runMain();
//
//    }

    private void runMain(){
        parseDocument();
        printData();
    }

    private void printEntries(){
        for (SimpleEntry<String, String> tuple : listOfEntries) {
            System.out.println("(" + tuple.getKey() + ", " + tuple.getValue() + ")");
        }
        System.out.println("List of Entries:" + listOfEntries.size());
    }
    public void printData(){
        System.out.println("List of Stars in Movies Entries:" + listOfEntries.size());
        System.out.println("Duplicate Stars in Movies Entries: "+ duplicate_entry);
        System.out.println("Stars in Movies - Stars not found: "+ star_not_found);
        System.out.println("Stars in Movies - Movies not found: "+ duplicate_entry);
    }

    public void parseDocument(){
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            File castFile = new File("stanford-movies/casts124.xml");
            InputStream inputStream= new FileInputStream(castFile);
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
        listOfEntries = new ArrayList<>();
        entriesMap =  new HashMap<>();
        movieFoundSet = new HashSet<>();
        starFoundSet = new HashSet<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        characters = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("m".equalsIgnoreCase(qName)) {
            String trimmedMovie = currentMovie.trim();
            String trimmedStar = currentStar.trim();
            SimpleEntry<String, String> entry = new SimpleEntry<>(trimmedMovie, trimmedStar);
            // Check if the entry is already in the map to detect duplicates
            if(!trimmedStar.isEmpty() && !trimmedMovie.isEmpty()){
                if(trimmedStar.equals("s a") || !starDataSet.contains(trimmedStar)){
                    star_not_found++;
                } else if (!movieDataSet.contains(trimmedMovie)) {
                    movie_not_found++;
                } else if (entriesMap.containsKey(entry)) {
                    duplicate_entry++;
                } else {
                    movieFoundSet.add(trimmedMovie);
                    starFoundSet.add(trimmedStar);
                    listOfEntries.add(entry);
                    entriesMap.put(entry, true); // Add to the map to track for duplicates
                }
            }


        } else if ("f".equalsIgnoreCase(qName)) {
            currentMovie = characters.toString().strip();
        }  else if ("a".equalsIgnoreCase(qName)) {
            currentStar = characters.toString().strip();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(new String(ch, start, length));
    }

}

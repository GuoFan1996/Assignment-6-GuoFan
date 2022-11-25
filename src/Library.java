import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Library {
    private ArrayList<Song> songs;
    private ArrayList<Album> albums;
    private ArrayList<Artist> artists;

    public Library() {
        songs = new ArrayList<Song>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }
    public boolean findSong(Song s) {
        return songs.contains(s);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void addSong(Song s) {
        songs.add(s);
    }

    public static String getContent(Node n) {
        StringBuilder sb = new StringBuilder();
        Node child = n.getFirstChild();
        sb.append(child.getNodeValue());
        return sb.toString();
    }

    public void readFromXML(String filename) throws ParserConfigurationException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filename));
            Element root = doc.getDocumentElement();

            // parse songs
            NodeList nodesSongs = root.getElementsByTagName("song");
            for (int i = 0; i < nodesSongs.getLength(); i++) {
                NodeList subNodes = nodesSongs.item(i).getChildNodes();
                //parse info of each song
                Song curSong = extractSong(subNodes);
                if (!findSong(curSong)) {
                    songs.add(curSong);
                }
            }

            System.out.println("-----------Now we have following songs parsed------------");
            for (Song s : songs) {
                System.out.printf("Song: %s artist: %s album: %s \n", s.name, s.performer.name, s.album.name);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public Song extractSong(NodeList subNodes) {
        Song curSong = new Song("");
        Artist curArtist;
        Album curAlbum;
        for (int j = 0; j < subNodes.getLength(); j++) {
            Node infoNode = subNodes.item(j);
            if (infoNode.getNodeType() == Node.ELEMENT_NODE) {
                switch (infoNode.getNodeName()) {
                    case "title":
                        curSong.name = getContent(infoNode).strip();
                        break;
                    case "artist":
                        curArtist = new Artist(getContent(infoNode).strip());
                        artists.add(curArtist);
                        curSong.performer = curArtist;
                        break;
                    case "album":
                        curAlbum = new Album(getContent(infoNode).strip());
                        albums.add(curAlbum);
                        curSong.album = curAlbum;
                        break;
                }
            }
        }
        return curSong;
    }

    public void readFromJSON(String filename) throws ParserConfigurationException{
        String s;
        try {
            Scanner sc = new Scanner(new File(filename));
            sc.useDelimiter("\\Z");
            s = sc.next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(s);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray songArray = (JSONArray) jsonObject.get("songs");
            for (Object song : songArray) {
                JSONObject jsong = (JSONObject) song;
                Song curSong = extractSong(jsong);
                if (!findSong(curSong)) {
                    songs.add(curSong);
                }
            }

            System.out.println("-----------Now we have following songs parsed------------");
            for (Song song : songs) {
                System.out.printf("Song name: %s\n artist: %s\n album: %s \n\n", song.name, song.performer.name, song.album.name);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found " + e);
        } catch (ParseException e1) {
            System.out.println("Parser error");
        }
    }

    public Song extractSong(JSONObject jsong) {
        Song curSong = new Song("");
        Album curAlbum = new Album("");
        Artist curArtist = new Artist("");

        curSong.name = jsong.get("title").toString().strip();

        JSONObject jalbum = (JSONObject) jsong.get("album");
        curAlbum.name = jalbum.get("name").toString().strip();
        curSong.album = curAlbum;
        albums.add(curAlbum);

        JSONObject jartist = (JSONObject) jsong.get("artist");
        curArtist.name = jartist.get("name").toString().strip();
        curSong.performer = curArtist;
        artists.add(curArtist);

        return curSong;
    }

    public void removeDuplicates() {
        if (songs.size()<2) {
            return;
        }

        //remove definitely duplicates
        ArrayList<Song> definiteDup = removeDefinite(songs);
        songs.removeAll(definiteDup);

        if (songs.size()<2) {
            return;
        }

        //remove possible duplicates
        ArrayList<Song> posibleDup = removePossible(songs);
        songs.removeAll(posibleDup);

        System.out.println("After removing we have: "+ songs.size() + " songs");

    }

    public ArrayList<Song> removeDefinite(ArrayList<Song> songs) {
        ArrayList<Song> removeSongs = new ArrayList<>();
        for (int i = 0; i < songs.size() - 2; i++) {
            Song song = songs.get(i);
            for (int j = i + 1; j < songs.size() - i - 1; j++) {
                Song other = songs.get(j);
                if (song.equals(other) || song.defiEqual(other)) {
                    if (askUserIsDuplicate(song,other)) {
                        removeSongs.add(other);
                    }
                }
            }
        }
        return removeSongs;
    }

    public ArrayList<Song> removePossible(ArrayList<Song> songs) {
        ArrayList<Song> removeSongs = new ArrayList<>();
        for (int i = 0; i < songs.size() - 1; i++) {
            Song song = songs.get(i);
            for (int j = i + 1; j < songs.size(); j++) {
                Song other = songs.get(j);
                if (song.posbEqual(other)) {
                    if (askUserIsDuplicate(song,other)) {
                        removeSongs.add(other);
                    }
                }
            }
        }
        return removeSongs;
    }

    public boolean askUserIsDuplicate(Song song,Song other) {
        Scanner s = new Scanner(System.in);
        System.out.printf("First song: %s %s %s \n", song.name, song.performer.name, song.album.name);
        System.out.printf("Second song: %s %s %s \n", other.name, other.performer.name, other.album.name);
        System.out.println("Are these two song duplicates? yes/no");

        String answer = s.next();
        if (answer.equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

    public void libraryToXML() throws IOException {
        StringBuilder sb= new StringBuilder();

        sb.append("<songs>");
        for (Song song : songs) {
            sb.append("\n"+song.toXML());
        }
        sb.append("\n</songs>");
        String xmlSource = sb.toString();
        java.io.FileWriter fw = new java.io.FileWriter("library.xml");
        fw.write(xmlSource);
        fw.close();
    }

    public void libraryToJSON() throws IOException {
        StringBuilder sb= new StringBuilder();

        sb.append("{\n \"songs\" : [\n ");
        for( int i = 0 ; i < songs.size() ; i ++){
            sb.append(songs.get(i).toJSON()+"\n" );
            if ( i != songs.size()-1 ){
                sb.append(",\n");
            }
        }
        sb.append("\n] \n}");

        String jsonSource = sb.toString();
        java.io.FileWriter fw = new java.io.FileWriter("library.json");
        fw.write(jsonSource);
        fw.close();
    }

    public static void main(String[] args) throws ParserConfigurationException {
        Library library = new Library();
        library.readFromXML("src/music-library-duplicates.xml");
        library.removeDuplicates();

    }

}

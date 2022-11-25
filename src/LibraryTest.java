import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    Library library;
    @BeforeEach
    void setUp() {
        library = new Library();
        Song yesterday = new Song("Yesterday",50,100,"jazz","happy",true);
        Song Mojito = new Song("Mojito",100,200,"pop","happy",false);
        Song Go = new Song("Go",10,300,"hip pop","angry",true);
        yesterday.setPerformer(new Artist("Beatles"));
        yesterday.setAlbum(new Album("Yesterday"));
        Mojito.setPerformer(new Artist("Jay"));
        Mojito.setAlbum(new Album("the greatest work of arts"));
        Go.setPerformer(new Artist("john"));
        Go.setAlbum(new Album("nothing to do"));

        library.addSong(yesterday);
        library.addSong(Mojito);
        library.addSong(Go);
    }

    @org.junit.jupiter.api.Test
    void readFromXML() throws ParserConfigurationException {
        library.readFromXML("src/music-library.xml");
    }

    @org.junit.jupiter.api.Test
    void readFromJSON() throws ParserConfigurationException {
        library.readFromJSON("src/music-library.json");
    }

    public static InputStream generateUserInput(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    @Test
    void testRemoveDuplicates() throws ParserConfigurationException {
        library.readFromXML("src/music-library-duplicates.xml");
        System.setIn(generateUserInput("yes"));
        //library.removeDuplicates();
    }

    @Test
    void libraryToXML() throws IOException {
        library.libraryToXML();
    }

    @Test
    void libraryToJSON() throws IOException {
        library.libraryToJSON();
    }
}
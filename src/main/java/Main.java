import core.Line;
import core.Parser;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser();
            List<Line> lines = parser.parseLines();
            lines.forEach(line -> {
                System.out.println(line.getLineName());
                line.getStationList().forEach(station -> {
                    System.out.println("\t" + station.getStationName());
                });
            });
            parser.parseToJSON();
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}

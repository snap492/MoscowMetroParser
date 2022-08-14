package core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final String URL = "https://www.moscowmap.ru/metro.html#lines";
    private Document page;

    public Parser() throws IOException {
        page = Jsoup.connect(URL).get();
    }

    public List<Line> parseLines() {
        Elements lines = getElementsByClass(".js-metro-line");
        List<Line> result = new ArrayList<>();
        lines.forEach(line -> {
            String lineNumber = line.attr("data-line");
            Line metroLine = new Line();
            metroLine.setLineNumber(lineNumber);
            metroLine.setLineName(line.text());
            metroLine.setStationList(getLineStations(lineNumber));
            result.add(metroLine);
        });
        return result;
    }

    public void parseToJSON() {
        try {
            String path = "c:\\output\\moscow_metro.json";
            Path of = Path.of(path);
            if (!Files.exists(of)) {
                Files.createDirectories(Paths.get("c:\\output\\"));
                Path file = Files.createFile(of);
            }
            try (
                    BufferedWriter bw = Files.newBufferedWriter(of)) {
                List<Line> lines = parseLines();
                StringBuilder sb = new StringBuilder();
                sb.append("{\n");
                sb.append("\t\"stations\" : {\n");
                for (int i = 0; i < lines.size(); i++) {
                    List<Station> stations = lines.get(i).getStationList();
                    sb.append("\t\t\"").append(lines.get(i).getLineNumber()).append("\" : [\n");
                    for (int j = 0; j < stations.size(); j++) {
                        if (j != stations.size() - 1) {
                            sb.append("\t\t\t\"").append(stations.get(j).getStationName()).append("\",\n");
                        } else {
                            sb.append("\t\t\t\"").append(stations.get(j).getStationName()).append("\"\n");
                        }
                    }
                    if (i < lines.size() - 1) {
                        sb.append("\t\t],\n");
                    } else {
                        sb.append("\t\t]\n");
                    }
                }
                sb.append("\t},\n");
                sb.append("\t\"lines\" : [\n");
                for (int i = 0; i < lines.size(); i++) {
                    sb.append("\t\t{\n");
                    sb.append("\t\t\t\"number\" : \"").append(lines.get(i).getLineNumber()).append("\"\n");
                    sb.append("\t\t\t\"name\" : \"").append(lines.get(i).getLineName()).append("\"\n");
                    if (i < lines.size() - 1) {
                        sb.append("\t\t},\n");
                    } else {
                        sb.append("\t\t}\n");
                    }
                }
                sb.append("\t]\n");
                sb.append("}");
                bw.write(sb.toString());
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Station> getLineStations(String lineNumber) {
        Elements stationNames = page.select("div[data-line = " + lineNumber + "] > p > a > span.name");
        List<Station> stationList = new ArrayList<>();
        stationNames.forEach(st -> {
            Station station = new Station();
            station.setLineNumber(lineNumber);
            station.setStationName(st.text());
            stationList.add(station);
        });
        return stationList;
    }

    private Elements getElementsByClass(String className) {
        return page.select(className);
    }
}

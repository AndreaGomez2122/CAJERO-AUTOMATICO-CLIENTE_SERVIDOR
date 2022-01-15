package log;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Log {

        private BufferedWriter buffered;
        private String ruta;
        private tipo tipo;

        public Log(String ruta) throws IOException {

            this.ruta = ruta;
            this.open(true);
        }

        public Log(String ruta, boolean reset) throws IOException {
            this.ruta = ruta;
            this.open(!reset);
        }

        private void open(boolean append) throws IOException {
            this.buffered = new BufferedWriter(new FileWriter(this.ruta, append));
        }

        public void addLine(tipo tipo, String line) throws IOException {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            String formatoFecha = sdf.format(new Date());
            this.open(true);
            this.buffered.write("[" + formatoFecha + "] " +" [ "+tipo+" ] "+ line + "\n");
            this.close();
        }

        public String[] getLines() throws FileNotFoundException, IOException {

            ArrayList<String> linesFile = new ArrayList<>();

            BufferedReader br = new BufferedReader(new FileReader(this.ruta));

            String line;
            while ((line = br.readLine()) != null) {
                linesFile.add(line);
            }

            br.close();

            String[] lines = new String[linesFile.size()];

            for (int i = 0; i < linesFile.size(); i++) {
                lines[i] = linesFile.get(i);
            }

            return lines;
        }

        public void resetLog() throws IOException{
            this.open(false);
            this.close();
        }

        private void close() throws IOException{
            this.buffered.close();
        }


    }


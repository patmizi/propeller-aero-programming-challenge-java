package com.propelleraero.gnsscompiler.compiler.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TeqcTool {

    private File teqcBinary;

    public TeqcTool() {
        // Unpack bundled teqc binary
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            File binary = new File(classLoader.getResource("teqc").getFile());
            InputStream in = new FileInputStream(binary);

            File teqcBinary = new File("teqc");
            OutputStream out = new FileOutputStream(teqcBinary);

            byte[] buffer = new byte[4096];
            int read = -1;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();

            teqcBinary.setExecutable(true);
            this.teqcBinary = teqcBinary;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Add shutdown hook to cleanup the teqc binary
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                teqcBinary.delete();
            }
        });
    }

    public void mergeRinexFiles(ArrayList<File> files, LocalDateTime fromTime, LocalDateTime toTime, String baseStationId) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String from = formatter.format(fromTime);
        String to = formatter.format(toTime);
        String command = String.format("%s +relax -st %s -e %s %s > %s.obs",
                this.teqcBinary.getAbsolutePath(),
                from,
                to,
                filesToString(files),
                baseStationId
        );

        Process pr = Runtime.getRuntime().exec(command);

        ReadStream in = new ReadStream("stdin", pr.getInputStream());
        ReadStream err = new ReadStream("stderr", pr.getErrorStream());

        in.start();
        err.start();

        try {
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pr.destroy();
        }
    }

    private String filesToString(ArrayList<File> files) {
        StringBuilder out = new StringBuilder();
        for (File file : files) {
            out.append(file.getName()).append(" ");
        }
        return out.toString().trim();
    }

}

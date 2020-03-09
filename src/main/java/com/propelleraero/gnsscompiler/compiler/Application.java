package com.propelleraero.gnsscompiler.compiler;

import com.propelleraero.gnsscompiler.compiler.model.GnssRinexFtpClient;
import com.propelleraero.gnsscompiler.compiler.model.TeqcTool;
import com.propelleraero.gnsscompiler.compiler.utils.DateUtils;
import org.apache.commons.cli.*;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static com.propelleraero.gnsscompiler.compiler.utils.Constants.*;

public class Application {

    public static void main(String[] args) {

        /*
          Configure CLI arg parser
         */
        Options options = new Options();

        Option baseStationIdOption = new Option(
                "b",
                "base_station_id",
                true,
                "The base station ID");
        baseStationIdOption.setRequired(true);
        options.addOption(baseStationIdOption);

        Option fromTimeOption = new Option(
                "f",
                "from_time",
                true,
                "Lower bound time range to grab logs from (ISO8601)");
        fromTimeOption.setRequired(true);
        options.addOption(fromTimeOption);

        Option toTimeOption = new Option(
                "t",
                "to_time",
                true,
                "Upper bound time range to grab logs from (ISO8601)");
        toTimeOption.setRequired(true);
        options.addOption(toTimeOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("gnss-compiler", options);

            System.exit(1);
        }

        /*
        Parse args and run application
         */
        String baseStationId = cmd.getOptionValue(baseStationIdOption.getLongOpt());
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;
        try {
            fromDateTime =
                    DateUtils.stringToDateTime(cmd.getOptionValue(fromTimeOption.getLongOpt()));
            toDateTime =
                    DateUtils.stringToDateTime(cmd.getOptionValue(toTimeOption.getLongOpt()));
        } catch (DateTimeParseException e) {
            System.err.println("Time args are not in ISO8601 format!");
            System.exit(1);
        }

        /*
        Retrieve RINEX files from FTP server
         */
        GnssRinexFtpClient.Builder clientBuilder = new GnssRinexFtpClient.Builder(baseStationId, fromDateTime, toDateTime);
        GnssRinexFtpClient client =
                clientBuilder.dataSourceLocation(DEFAULT_DATASOURCE_LOCATION)
                    .fileLocationFormat(DEFAULT_FILE_LOCATION_FORMAT)
                    .fileNameFormat(DEFAULT_FILENAME_FORMAT)
                    .ftpHost(DEFAULT_FTP_SERVER)
                    .ftpClient(new FTPClient())
                    .build();

        ArrayList<File> rinexFiles = null;
        try {
            rinexFiles = client.getFiles();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        /*
        Merge RINEX files into a single observation file
         */
        TeqcTool merger = new TeqcTool();
        try {
            merger.mergeRinexFiles(rinexFiles, fromDateTime, toDateTime, baseStationId);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}

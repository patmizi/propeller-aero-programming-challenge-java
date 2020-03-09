package com.propelleraero.gnsscompiler.compiler.model;

import com.propelleraero.gnsscompiler.compiler.utils.DataSourceUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import static com.propelleraero.gnsscompiler.compiler.utils.Constants.FILE_EXTENSION;

public class GnssRinexFtpClient {

    private FTPClient ftp;
    private String ftpHost;
    private ArrayList<String> dataSources;

    // TODO: This code looks messy. It can be refactored
    public ArrayList<File> getFiles() throws IOException {
        ftp.connect(ftpHost);

        int responseCode = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(responseCode)) {
            ftp.disconnect();
            throw new ConnectException("FTP server refused connection");
        }

        ftp.login("anonymous", "");
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();

        /*
        Read and decompress observation files
         */
        ArrayList<File> downloadedFiles = new ArrayList<>();
        for (String dataSource : dataSources) {
            String fileName = dataSource
                    .replaceFirst("/", "")
                    .replaceAll("/", "-")
                    .replace(FILE_EXTENSION, "");
            File file = new File(String.format("./%s", fileName));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            InputStream in = ftp.retrieveFileStream(dataSource);
            int fileResponseCode = ftp.getReplyCode();

            if (in == null ||
                    (!FTPReply.isPositivePreliminary(fileResponseCode) && !FTPReply.isPositiveCompletion(fileResponseCode))) {
                System.out.println(
                        String.format("Failed to retrieve %s%n reason: %s", dataSource, ftp.getReplyString()));
                file.delete();
                continue;
            }

            GZIPInputStream gzipStream = new GZIPInputStream(in);
            byte[] buffer = new byte[4096];
            int read = -1;
            while ((read = gzipStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            ftp.completePendingCommand();
            downloadedFiles.add(file);

        }

        return downloadedFiles;

    }

    /*
    Builder Class
     */
    public static class Builder {

        private FTPClient ftp;
        private String ftpHost;
        private String baseStationId;
        private LocalDateTime fromTime;
        private LocalDateTime toTime;
        private String dataSourceLocation;
        private String fileLocationFormat;
        private String fileNameFormat;
        private ArrayList<String> dataSources;

        public Builder(String baseStationId, LocalDateTime fromTime, LocalDateTime toTime) {
            this.baseStationId = baseStationId;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.dataSources = new ArrayList<>();
        }

        public Builder dataSourceLocation(String dataSourceLocation) {
            this.dataSourceLocation = dataSourceLocation;

            return this;
        }

        public Builder fileLocationFormat(String fileLocationFormat) {
            this.fileLocationFormat = fileLocationFormat;

            return this;
        }

        public Builder fileNameFormat(String fileNameFormat) {
            this.fileNameFormat = fileNameFormat;

            return this;
        }

        public Builder ftpHost(String ftpHost) {
            this.ftpHost = ftpHost;

            return this;
        }

        public Builder ftpClient(FTPClient ftp) {
            this.ftp = ftp;

            return this;
        }

        public Builder additionalDataSources(ArrayList<String> dataSources) {
            this.dataSources.addAll(dataSources);

            return this;
        }

        // TODO: Could refactor this code so that this method is not coupled with the data source utilities class
        public GnssRinexFtpClient build() {
            GnssRinexFtpClient client = new GnssRinexFtpClient();
            client.ftpHost = this.ftpHost;
            client.ftp = this.ftp;
            this.dataSources.addAll(
                    DataSourceUtils.generateDataSourceRange(
                            this.baseStationId,
                            this.fromTime,
                            this.toTime,
                            this.dataSourceLocation,
                            this.fileLocationFormat,
                            this.fileNameFormat
                    )
            );
            client.dataSources = this.dataSources;

            return client;
        }

    }

}

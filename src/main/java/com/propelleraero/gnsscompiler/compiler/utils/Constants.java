package com.propelleraero.gnsscompiler.compiler.utils;

public class Constants {

    public static final String DEFAULT_FTP_SERVER = "www.ngs.noaa.gov";
    public static final String DEFAULT_DATASOURCE_LOCATION = "/cors/rinex/";
    public static final String DEFAULT_FILE_LOCATION_FORMAT =
            "%s%d/%s/%s/%s"; // Full location i.e /cors/rinex/2017/257/nybp/nybp257x.17o.gz
    public static final String FILE_EXTENSION = ".gz";
    public static final String DEFAULT_FILENAME_FORMAT = "%s%s%s.%do" + FILE_EXTENSION; // i.e nybp257x.17o.gz


}

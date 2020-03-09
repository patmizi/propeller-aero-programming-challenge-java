package utils;

import com.propelleraero.gnsscompiler.compiler.utils.Constants;
import com.propelleraero.gnsscompiler.compiler.utils.DataSourceUtils;
import com.propelleraero.gnsscompiler.compiler.utils.DateUtils;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;

public class DataSourceUtilsTests {

    @Test
    public void testDataSourceGenerateRange() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("/cors/rinex/2017/257/nybp/nybp257x.17o.gz");
        expected.add("/cors/rinex/2017/258/nybp/nybp258a.17o.gz");
        expected.add("/cors/rinex/2017/258/nybp/nybp258b.17o.gz");

        ArrayList<String> dataSources = DataSourceUtils.generateDataSourceRange(
                "nybp",
                DateUtils.stringToDateTime("2017-09-14T23:11:22Z"),
                DateUtils.stringToDateTime("2017-09-15T01:33:44Z"),
                Constants.DEFAULT_DATASOURCE_LOCATION,
                Constants.DEFAULT_FILE_LOCATION_FORMAT,
                Constants.DEFAULT_FILENAME_FORMAT
        );

        assertArrayEquals(expected.toArray(), dataSources.toArray());
    }

}

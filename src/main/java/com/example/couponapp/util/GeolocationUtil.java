package com.example.couponapp.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Component
@Slf4j
public class GeolocationUtil {

    private static String GEO_COUNTRY_FILE_PATH;

    @Value("${geoloc.country.file.location}")
    public void setGeoCountryFilePath(String geoCountryFilePath) {
        GEO_COUNTRY_FILE_PATH = geoCountryFilePath;
    }

    public static String getGeoCountryFilePath() {
        return GEO_COUNTRY_FILE_PATH;
    }

    public static String extractCountryFromIpAddress(String ipAddress) {
        File ipDatabase = new File(getGeoCountryFilePath());
        try {
            DatabaseReader reader = new DatabaseReader.Builder(ipDatabase).build();
            InetAddress ip = InetAddress.getByName(ipAddress);
            CountryResponse response = reader.country(ip);

            Country country = response.country();
            return country.isoCode();
            //Could be handled better, more time needed
        } catch (IOException | GeoIp2Exception e) {
            log.info("Geolocation had a problem with translating ip to country");
            return "Wrong ip address";
        }
    }
}

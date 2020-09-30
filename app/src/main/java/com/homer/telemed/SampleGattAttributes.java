package com.homer.telemed;

import java.util.HashMap;
import java.util.UUID;

public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    // GATT - Service Types
    // see: https://developer.bluetooth.org/gatt/services/Pages/ServicesHome.aspx?_ga=1.6792625.220697255.1428282736
    public static final String BLOOD_PRESSURE_SERVICE       = "00001810-0000-1000-8000-00805f9b34fb";
    public static final String HEART_RATE_SERVICE           = "0000180d-0000-1000-8000-00805f9b34fb";
    //Used by App
    public final static String BATTERY_LEVEL                = "00002a19-0000-1000-8000-00805f9b34fb";
    public final static String HEART_RATE_MEASUREMENT       = "00002a37-0000-1000-8000-00805f9b34fb";
    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public final static UUID[] SUPPORTED_UUID = { UUID.fromString(HEART_RATE_SERVICE) };
    static {
        // Sample Services.
        attributes.put(BLOOD_PRESSURE_SERVICE,      "Blood Pressure Service");
        attributes.put(HEART_RATE_SERVICE,          "Heart Rate Service");
        // see: https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicsHome.aspx
        attributes.put(HEART_RATE_MEASUREMENT,                 "Heart Rate Measurement");
        attributes.put("00002a49-0000-1000-8000-00805f9b34fb", "Blood Pressure Feature");
        attributes.put("00002a35-0000-1000-8000-00805f9b34fb", "Blood Pressure Measurement");
        attributes.put("00002a38-0000-1000-8000-00805f9b34fb", "Body Sensor Location");
    }

    // See: https://www.bluetooth.org/en-us/specification/assigned-numbers/company-identifiers
    private static HashMap<Integer, String> deviceManufacturer = new HashMap<Integer, String>();
    static {
        deviceManufacturer.put(0,"Ericsson Technology Licensing");
        deviceManufacturer.put(1,"Nokia Mobile Phones");
        deviceManufacturer.put(2,"Intel Corp.");
        deviceManufacturer.put(3,"IBM Corp.");
        deviceManufacturer.put(4,"Toshiba Corp.");
        deviceManufacturer.put(5,"3Com");
        deviceManufacturer.put(6,"Microsoft");
        deviceManufacturer.put(7,"Microsoft");
        deviceManufacturer.put(8,"Motorola");
        deviceManufacturer.put(10,"Cambridge Silicon Radio");
        deviceManufacturer.put(13,"Texas Instruments Inc.");
        deviceManufacturer.put(26,"TTPCom Limited");
        deviceManufacturer.put(29,"Qualcomm");
        deviceManufacturer.put(76,"Apple, Inc.");
        deviceManufacturer.put(67,"PARROT SA");
        deviceManufacturer.put(85,"Plantronics, Inc.");
        deviceManufacturer.put(86,"Sony Ericsson Mobile Communications");
        deviceManufacturer.put(107,"Polar Electro OY");
        deviceManufacturer.put(117,"Samsung Electronics Co. Ltd.");
        deviceManufacturer.put(118,"Creative Technology Ltd.");
        deviceManufacturer.put(120,"Nike, Inc.");
        deviceManufacturer.put(138,"Jawbone");
        deviceManufacturer.put(158,"Bose Corporation");
        deviceManufacturer.put(195,"adidas AG");
        deviceManufacturer.put(196,"​​LG Electronics​");
        deviceManufacturer.put(204,"Beats Electronics");
        deviceManufacturer.put(209,"Polar Electro Europe B.V.​");
        deviceManufacturer.put(214,"Timex Group USA, Inc.");
        deviceManufacturer.put(224,"Google");
        deviceManufacturer.put(301,"Sony");
        deviceManufacturer.put(368,"Roche Diabetes Care AG");
        deviceManufacturer.put(376,"CASIO COMPUTER CO., LTD.");
        deviceManufacturer.put(398,"Fitbit, Inc.");
        deviceManufacturer.put(508,"Wahoo Fitness, LLC​");
        deviceManufacturer.put(515,"​​Kemppi Oy​");
        deviceManufacturer.put(65535,"​​Test​");
    }

    private static HashMap<Integer, String> healthDeviceProfile = new HashMap<Integer, String>();
    static {
        healthDeviceProfile.put(4103,"Blood Pressure Monitor");
    }

    private static HashMap<Integer, String> sensorLocations = new HashMap<Integer, String>();
    static {
        sensorLocations.put(0, "​​Other​");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}

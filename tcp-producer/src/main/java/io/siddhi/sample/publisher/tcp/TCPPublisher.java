/*
 *  Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package io.siddhi.sample.publisher.tcp;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.extension.io.tcp.sink.TCPSink;
import io.siddhi.extension.map.binary.sinkmapper.BinarySinkMapper;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Test client for TCP sink.
 */
public class TCPPublisher {

    private static final Logger log = Logger.getLogger(TCPPublisher.class);

    /**
     * Main method to start the TCP event publisher.
     *
     * @param args no args need to be provided
     */
    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        log.info("Initialize TCP publisher.");
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("sink:tcp", TCPSink.class);
        siddhiManager.setExtension("sinkMapper:binary", BinarySinkMapper.class);

        String context = "taxiRideRequests";

        if (args != null && args.length == 1) {
            context = args[0];
        }

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name(\"TCP-Event-Publisher\")\n" +
                        "@sink(type='tcp', url='tcp://localhost:9892/"+ context +"', @map(type='binary'))\n" +
                        "define stream TaxiRideEventStream(id long, time string, passengerId string, " +
                        "passengerName string, pickUpAddress string, pickUpZone string, \n" +
                        "dropOutAddress string, routingDetails string, expectedFare double, " +
                        "status string, passengerGrade string, additionalNote string);");
        siddhiAppRuntime.start();
        Thread.sleep(2000);

        InputHandler streamHandler = siddhiAppRuntime.getInputHandler("TaxiRideEventStream");
        sendSampleEvents(streamHandler);

        //Wait until first window expires
        Thread.sleep(60000);
        sendSampleEvents(streamHandler);
        streamHandler.send(new Object[]{983L, "2019-09-01 09:58:40", "US-BR-7645", "Melinda", "127 Guernsey St., BK NY",
                "Brooklyn", "121 Dekalb Ave., BK NY", "PU: 127 Guernsey St., BK NY; DO: 121 Dekalb Ave., BK NY",
                28.00D, "Assigned", "Silver", "None"});
        Thread.sleep(1000);
        streamHandler.send(new Object[]{983L, "2019-09-01 09:58:41", "US-BR-7645", "Melinda", "127 Guernsey St., BK NY",
                "Brooklyn", "121 Dekalb Ave., BK NY", "PU: 127 Guernsey St., BK NY; DO: 121 Dekalb Ave., BK NY",
                28.00D, "Cancelled", "Silver", "WT is High"});

        //Wait until first window expires
        Thread.sleep(60000);
        sendSampleEvents(streamHandler);
        streamHandler.send(new Object[]{230L, "2019-09-01 09:58:40", "US-BR-1645", "John", "657 St Marks Ave., BK NY",
                "Brooklyn", "240 South 3rd St., BK NY", "PU: 657 St Marks Ave., BK NY; DO: 240 South 3rd St., BK NY",
                38.00D, "Assigned", "Silver", "None"});
        Thread.sleep(1000);
        streamHandler.send(new Object[]{230L, "2019-09-01 09:58:41", "US-BR-1645", "John", "657 St Marks Ave., BK NY",
                "Brooklyn", "240 South 3rd St., BK NY", "PU: 657 St Marks Ave., BK NY; DO: 240 South 3rd St., BK NY",
                38.00D, "Cancelled", "Silver", "WT is High"});
        Thread.sleep(1000);

        streamHandler.send(new Object[]{840L, "2019-09-01 09:58:40", "US-BR-2645", "Lindy", "1611 47th St., BK NY",
                "Brooklyn", "1048 49th St., BK NY", "PU: 1611 47th St., BK NY; DO: 1048 49th St., BK NY",
                22.00D, "Assigned", "Silver", "None"});
        Thread.sleep(1000);
        streamHandler.send(new Object[]{840L, "2019-09-01 09:58:41", "US-BR-2645", "Lindy", "1611 47th St., BK NY",
                "Brooklyn", "1048 49th St., BK NY", "PU: 1611 47th St., BK NY; DO: 1048 49th St., BK NY",
                22.00D, "Cancelled", "Silver", "WT is High"});

        log.info("TCP Event publisher completed sending events successfully");

    }

    private static void sendSampleEvents(InputHandler streamHandler) throws InterruptedException {
        streamHandler.send(new Object[]{124L, "2019-09-01 09:58:34", "US-BR-1783", "Bob", "Brooklyn Museum, 200 Eastern Pkwy., BK NY",
                "Brooklyn", "1 Brookdale Plaza, BK NY", "PU: Brooklyn Museum, 200 Eastern Pkwy., BK NY; DO: 1 Brookdale Plaza, BK NY; ",
                25.00D, "Assigned", "Premium", "None"});
        Thread.sleep(1000);

        streamHandler.send(new Object[]{124L, "2019-09-01 09:58:35", "US-BR-1783", "Bob", "Brooklyn Museum, 200 Eastern Pkwy., BK NY",
                "Brooklyn", "1 Brookdale Plaza, BK NY", "PU: Brooklyn Museum, 200 Eastern Pkwy., BK NY; DO: 1 Brookdale Plaza, BK NY; ",
                25.00D, "Cancelled", "Silver", "WT is High"});

        Thread.sleep(2000);

        streamHandler.send(new Object[]{342L, "2019-09-01 09:58:37", "US-BR-5683", "Sam", "33 Robert Dr., Short Hills NJ",
                "Brooklyn", "John F Kennedy International Airport, vitona Airlines",
                "PU: 33 Robert Dr., Short Hills NJ; DO: John F Kennedy International Airport, vitona Airlines",
                15.00D, "Assigned", "Silver", "None"});
        Thread.sleep(1000);
        streamHandler.send(new Object[]{342L, "2019-09-01 09:58:38", "US-BR-5683", "Sam", "33 Robert Dr., Short Hills NJ",
                "Brooklyn", "John F Kennedy International Airport, vitona Airlines",
                "PU: 33 Robert Dr., Short Hills NJ; DO: John F Kennedy International Airport, vitona Airlines",
                15.00D, "Cancelled", "Silver", "WT is High"});

        Thread.sleep(2000);

        streamHandler.send(new Object[]{235L, "2019-09-01 09:58:40", "US-BR-4763", "Charles", "60 Glenmore Ave., BK NY",
                "Brooklyn", "2171 Nostrand Ave., BK NY", "PU: 60 Glenmore Ave., BK NY; DO: 2171 Nostrand Ave., BK NY",
                18.00D, "Assigned", "Silver", "None"});
        Thread.sleep(1000);
        streamHandler.send(new Object[]{235L, "2019-09-01 09:58:41", "US-BR-4763", "Charles", "60 Glenmore Ave., BK NY",
                "Brooklyn", "2171 Nostrand Ave., BK NY", "PU: 60 Glenmore Ave., BK NY; DO: 2171 Nostrand Ave., BK NY",
                18.00D, "Cancelled", "Silver", "WT is High"});

        Thread.sleep(2000);

        streamHandler.send(new Object[]{456L, "2019-09-01 09:58:43", "US-BR-9897", "Andria", "128 East 31 St., BK NY",
                "Brooklyn", "369 93rd St., BK NY", "PU: 128 East 31 St., BK NY; DO: 369 93rd St., BK NY",
                28.00D, "Assigned", "Gold", "None"});
        Thread.sleep(1000);
        streamHandler.send(new Object[]{456L, "2019-09-01 09:58:44", "US-BR-9897", "Andria", "128 East 31 St., BK NY",
                "Brooklyn", "369 93rd St., BK NY", "PU: 128 East 31 St., BK NY; DO: 369 93rd St., BK NY",
                28.00D, "Cancelled", "Gold", "WT is High"});
    }
}

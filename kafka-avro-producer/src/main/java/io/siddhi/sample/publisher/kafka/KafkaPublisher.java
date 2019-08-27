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

package io.siddhi.sample.publisher.kafka;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.extension.io.kafka.sink.KafkaSink;
import io.siddhi.extension.map.avro.sinkmapper.AvroSinkMapper;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Test server for Kafka sink.
 */
public class KafkaPublisher {

    private static final Logger log = Logger.getLogger(KafkaPublisher.class);

    /**
     * Main method to start the Kafka event publisher.
     *
     * @param args no args need to be provided
     */
    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        log.info("Initialize Kafka publisher.");
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("sink:kafka", KafkaSink.class);
        siddhiManager.setExtension("sinkMapper:avro", AvroSinkMapper.class);

        String bootstrapServers = "";
        String topic = "";

        if (args != null && args.length == 1) {
            bootstrapServers = args[0];
        } else if (args != null && args.length == 2) {
            bootstrapServers = args[0];
            topic = args[1];
        } else {
            bootstrapServers = "localhost:9092";
            topic = "glucose-readings";
        }

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name(\"Kafka-Event-Publisher\")\n" +
                        "@sink(type='kafka',\n" +
                        "      topic='" + topic + "',\n" +
                        "      partition.no='0',\n" +
                        "      is.binary.message='true',\n" +
                        "      bootstrap.servers='" + bootstrapServers + "',\n" +
                        "      @map(type='avro',schema.def=\"\"\"\n" +
                        "            {\n" +
                        "\t            \"type\": \"record\",\n" +
                        "\t            \"name\": \"glucose_reading\",\n" +
                        "\t            \"namespace\": \"glucose\",\n" +
                        "\t            \"fields\": [{\n" +
                        "\t\t            \"name\": \"locationRoom\",\n" +
                        "\t\t            \"type\": \"string\"\n" +
                        "\t            }, {\n" +
                        "\t\t            \"name\": \"locationBed\",\n" +
                        "\t\t            \"type\": \"string\"\n" +
                        "\t\t        }, {\n" +
                        "\t\t            \"name\": \"timeStamp\",\n" +
                        "\t\t            \"type\": \"string\"\n" +
                        "\t\t        }, {\n" +
                        "\t\t            \"name\": \"sensorID\",\n" +
                        "\t\t            \"type\": \"long\"\n" +
                        "\t\t        }, {\n" +
                        "\t\t            \"name\": \"patientGroup\",\n" +
                        "\t\t            \"type\": \"string\"\n" +
                        "\t\t        }, {\n" +
                        "\t\t            \"name\": \"patientFirstName\",\n" +
                        "\t\t            \"type\": \"string\"\n" +
                        "\t\t        }, {\n" +
                        "\t\t            \"name\": \"patientLastName\",\n" +
                        "\t\t            \"type\": \"string\"\n" +
                        "\t\t        }, {\n" +
                        "\t\t            \"name\": \"sensorValue\",\n" +
                        "\t\t            \"type\": \"double\"  \n" +
                        "\t\t        }, {\n" +
                        "\t\t            \"name\": \"unitOfMeasure\",\n" +
                        "\t\t            \"type\": \"string\" \n" +
                        "    \t        }]\n" +
                        "            }\"\"\"))\n" +
                        "define stream GlucoseReadingStream (locationRoom string, locationBed string, " +
                        "timeStamp string, sensorID long, patientGroup string, \n" +
                        "patientFirstName string, patientLastName string, sensorValue double, " +
                        "unitOfMeasure string);\n" +
                        "\n");
        siddhiAppRuntime.start();
        Thread.sleep(2000);

        Event[] events = new Event[4];
        events[0] = new Event(System.currentTimeMillis(), new Object[]{"L2W1", "B1",
                String.valueOf(System.currentTimeMillis()), 01212L, "Premium", "John2", "Steward", 20D, "MMOL"});
        events[1] = new Event(System.currentTimeMillis(), new Object[]{"L2W1", "B1",
                String.valueOf(System.currentTimeMillis()), 01212L, "Premium", "John2", "Steward", 25D, "MMOL"});
        events[2] = new Event(System.currentTimeMillis(), new Object[]{"L2W1", "B1",
                String.valueOf(System.currentTimeMillis()), 01212L, "Premium", "John2", "Steward", 30D, "MMOL"});
        events[3] = new Event(System.currentTimeMillis(), new Object[]{"L2W1", "B1",
                String.valueOf(System.currentTimeMillis()), 01212L, "Premium", "John2", "Steward", 32D, "MMOL"});
        InputHandler streamHandler = siddhiAppRuntime.getInputHandler("GlucoseReadingStream");

        streamHandler.send(events);

        //Wait until event publish task completes
        Thread.sleep(5000);
    }
}

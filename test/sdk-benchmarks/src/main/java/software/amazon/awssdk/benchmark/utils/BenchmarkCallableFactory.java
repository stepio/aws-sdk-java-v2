/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.benchmark.utils;

import static software.amazon.awssdk.benchmark.utils.BenchmarkUtil.ERROR_JSON_BODY;
import static software.amazon.awssdk.benchmark.utils.BenchmarkUtil.ERROR_XML_BODY;
import static software.amazon.awssdk.benchmark.utils.BenchmarkUtil.JSON_BODY;
import static software.amazon.awssdk.benchmark.utils.BenchmarkUtil.XML_BODY;
import static software.amazon.awssdk.benchmark.utils.BenchmarkUtil.getUri;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.protocolec2.ProtocolEc2Client;
import software.amazon.awssdk.services.protocolquery.ProtocolQueryClient;
import software.amazon.awssdk.services.protocolrestjson.ProtocolRestJsonClient;
import software.amazon.awssdk.services.protocolrestxml.ProtocolRestXmlClient;

/**
 * Factory class to create Callable for the benchmark to consume
 */
public final class BenchmarkCallableFactory {

    private BenchmarkCallableFactory() {
    }

    /**
     * Create Callable based on the client type
     *
     * @param clientType the client type to be used.
     * @return Callable
     */
    public static Callable<?> callableOfHttpClient(String clientType) {
        URI uri = getUri();

        SdkHttpClient sdkHttpClient;
        ProtocolRestJsonClient client;

        if (clientType.equalsIgnoreCase("UrlConnectionHttpClient")) {
            sdkHttpClient = UrlConnectionHttpClient.builder().build();
        } else {
            sdkHttpClient = ApacheHttpClient.builder().build();
        }

        client = ProtocolRestJsonClient.builder()
                                       .endpointOverride(uri)
                                       .httpClient(sdkHttpClient)
                                       .build();

        return () -> client.allTypes(BenchmarkUtil.jsonAllTypeRequest());
    }

    /**
     * Create Callable based on the protocol
     *
     * @param value the protocol value
     * @return Callable
     */
    public static Callable<?> callableOfProtocol(String value) {
        Protocol protocol = Protocol.fromValue(value);
        URI uri = getUri();
        SdkClient client;
        Callable callable;

        switch (protocol) {
            case XML:
                client = ProtocolRestXmlClient.builder()
                                              .endpointOverride(uri)
                                              .httpClient(new MockHttpClient(XML_BODY, ERROR_XML_BODY))
                                              .build();
                callable = () -> ((ProtocolRestXmlClient) client).allTypes(BenchmarkUtil.xmlAllTypeRequest());
                break;
            case EC2:
                client = ProtocolEc2Client.builder()
                                          .endpointOverride(uri)
                                          .httpClient(new MockHttpClient(XML_BODY, ERROR_XML_BODY))
                                          .build();
                callable = () -> ((ProtocolEc2Client) client).allTypes(BenchmarkUtil.ec2AllTypeRequest());
                break;
            case JSON:
                client = ProtocolRestJsonClient.builder()
                                               .endpointOverride(uri)
                                               .httpClient(new MockHttpClient(JSON_BODY, ERROR_JSON_BODY))
                                               .build();
                callable = () -> ((ProtocolRestJsonClient) client).allTypes(BenchmarkUtil.jsonAllTypeRequest());
                break;
            case QUERY:
                client = ProtocolQueryClient.builder()
                                            .endpointOverride(uri)
                                            .httpClient(new MockHttpClient(XML_BODY, ERROR_XML_BODY))
                                            .build();
                callable = () -> ((ProtocolQueryClient) client).allTypes(BenchmarkUtil.queryAllTypeRequest());
                break;
            default:
                throw new IllegalArgumentException("invalid protocol");
        }

        return callable;
    }

    /**
     * Create Callable based on the sdkClient
     *
     * @param client the sdk client
     * @return Callable
     */
    public static Callable<?> callableOfSdkClient(String client) {
        Supplier<?> clientSupplier;

        if (client.equalsIgnoreCase("defaultClient")) {
            clientSupplier = () -> DynamoDbClient.builder()
                                                 .httpClient(ApacheHttpClient.builder().build()).build();
        } else if (client.equalsIgnoreCase("customizedClient")) {
            clientSupplier = () -> DynamoDbClient.builder()
                                                 .region(Region.US_WEST_2)
                                                 .credentialsProvider(StaticCredentialsProvider.create(
                                                     AwsBasicCredentials.create("test", "test")))
                                                 .httpClient(ApacheHttpClient.builder().build())
                                                 .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                                                 .build();
        } else {
            clientSupplier = () -> AmazonDynamoDBClient.builder().build();
        }

        return clientSupplier::get;
    }
}

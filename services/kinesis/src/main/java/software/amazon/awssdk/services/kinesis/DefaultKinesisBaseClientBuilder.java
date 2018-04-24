/*
 * Copyright 2013-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 * 
 * http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package software.amazon.awssdk.services.kinesis;

import javax.annotation.Generated;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.auth.Aws4Signer;
import software.amazon.awssdk.core.auth.StaticSignerProvider;
import software.amazon.awssdk.core.client.builder.DefaultClientBuilder;
import software.amazon.awssdk.core.config.defaults.ClientConfigurationDefaults;
import software.amazon.awssdk.core.config.defaults.ServiceBuilderConfigurationDefaults;
import software.amazon.awssdk.core.runtime.auth.SignerProvider;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.utils.AttributeMap;

/**
 * Internal base class for {@link DefaultKinesisClientBuilder} and {@link DefaultKinesisAsyncClientBuilder}.
 */
@Generated("software.amazon.awssdk:codegen")
@SdkInternalApi
abstract class DefaultKinesisBaseClientBuilder<B extends KinesisBaseClientBuilder<B, C>, C> extends DefaultClientBuilder<B, C> {

    @Override
    protected final String serviceEndpointPrefix() {
        return "kinesis";
    }

    @Override
    protected final ClientConfigurationDefaults serviceDefaults() {
        return ServiceBuilderConfigurationDefaults.builder().defaultSignerProvider(this::defaultSignerProvider)
                .addRequestHandlerPath("software/amazon/awssdk/services/kinesis/execution.interceptors")
                .crc32FromCompressedDataEnabled(false).build();
    }

    @Override
    protected AttributeMap serviceSpecificHttpConfig() {
        return AttributeMap.builder()
                           .put(SdkHttpConfigurationOption.PROTOCOL, Protocol.HTTP2)
                           .build();
    }

    private SignerProvider defaultSignerProvider() {
        Aws4Signer signer = new Aws4Signer();
        signer.setServiceName("kinesis");
        signer.setRegionName(signingRegion().value());
        return StaticSignerProvider.create(signer);
    }
}
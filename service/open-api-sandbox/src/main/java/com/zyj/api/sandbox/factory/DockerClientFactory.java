package com.zyj.api.sandbox.factory;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DockerClientFactory {

    private static final DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                                                                              .build();

    private static final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .build();

    public static DockerClient getDockerClient() {
        return DockerClientImpl.getInstance(config, httpClient);
    }
}

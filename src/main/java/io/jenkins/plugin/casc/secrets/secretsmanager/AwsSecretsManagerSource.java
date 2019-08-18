package io.jenkins.plugin.casc.secrets.secretsmanager;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import hudson.Extension;
import io.jenkins.plugins.casc.SecretSource;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class AwsSecretsManagerSource extends SecretSource {
    public static final String CASC_SECRETSMANAGER_PREFIX = "CASC_SECRETSMANAGER_PREFIX";

    private static final Logger LOG = Logger.getLogger(AwsSecretsManagerSource.class.getName());

    @Override
    public Optional<String> reveal(String key) {

            String resolveKey = key;
            String prefix = getSystemProperty();

            if (prefix != null) {
                    resolveKey = prefix + key;
            }

            try {
                    GetSecretValueRequest request = new GetSecretValueRequest();
                    request.withSecretId(resolveKey).withVersionStage("AWSCURRENT");
                    GetSecretValueResult result = getClient(resolveKey).getSecretValue(request);
                    return Optional.of(result.getSecretString());
            } catch (AWSSecretsManagerException e) {
                    LOG.log(Level.SEVERE, "Error getting secret: " + resolveKey, e);
                    return Optional.empty();
            }
    }

    private AWSSecretsManager getClient(String param) {
            String secretsRegion = getSecretsRegion(param);
            AWSSecretsManagerClientBuilder builder = AWSSecretsManagerClientBuilder.standard().withRegion(secretsRegion);
            builder.setCredentials(new DefaultAWSCredentialsProviderChain());
            return builder.build();
    }

    private String getSystemProperty() {
            return System.getenv(CASC_SECRETSMANAGER_PREFIX);
    }

    private String getSecretsRegion(String param) {
            String resolveKeyRegion = param.split(":")[3];
            try {
                    Regions.fromName(resolveKeyRegion);
            } catch (IllegalArgumentException e){
                    LOG.info("Cannot resolve region from secret value: " + param + ". Will continue with default value from the AWS credentials." + e);
                    resolveKeyRegion = Regions.getCurrentRegion().getName();
            }
            return resolveKeyRegion;
    }
}

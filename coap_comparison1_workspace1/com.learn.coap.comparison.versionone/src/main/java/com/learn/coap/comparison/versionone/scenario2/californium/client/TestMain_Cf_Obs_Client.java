package com.learn.coap.comparison.versionone.scenario2.californium.client;

import java.io.IOException;

import java.security.GeneralSecurityException;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;

/**
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Client {
	private int expectedNumberOfMessages = 30;
	private int numberOfMessages = 0;

	public static void main(String[] args) {
		new TestMain_Cf_Obs_Client().run();
	}

	private void run() {
		
		String myusr_path = System.getProperty("user.dir");
    	String KEY_STORE_LOCATION = "mycerts/californium/client/my_own/myclientakeystore.jks";
    	char[] KEY_STORE_PASSWORD = "CksOneAdmin".toCharArray();
		
    	SslContextUtil.Credentials clientCredentials = null;
    	
		DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

			@Override
			public void applyDefinitions(Configuration config) {
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
				config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			}

		};
		
		
		try {
			clientCredentials = SslContextUtil.loadCredentials(myusr_path + "\\" + KEY_STORE_LOCATION, "myclientakeystorealias", KEY_STORE_PASSWORD, KEY_STORE_PASSWORD);
		} catch (IOException | GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example client", DEFAULTS);
		DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration);
		builder.setCertificateIdentityProvider(new SingleCertificateProvider(clientCredentials.getPrivateKey(), clientCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY));
		builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder().setTrustAllRPKs().build());
		
		DTLSConnector dtlsConnector = new DTLSConnector(builder.build());		// new DTLS Connector

    	CoapClient client = new CoapClient("coaps://127.0.0.1:5684/Resource1");	// new client
		CoapEndpoint.Builder coapEndPointBuilder = new CoapEndpoint.Builder().setConfiguration(configuration).setConnector(dtlsConnector);

		client.setEndpoint(coapEndPointBuilder.build());						// set DTLSConnector into a configuration into CoapEndpoint into CoapClient
		
		CoapHandler  myObserveHandler = new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println(response.getResponseText());
				numberOfMessages = numberOfMessages + 1;
			}

			@Override
			public void onError() {
			}
		};	

        client.observe(myObserveHandler);

		while (numberOfMessages < expectedNumberOfMessages) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		client.shutdown();

	}
}
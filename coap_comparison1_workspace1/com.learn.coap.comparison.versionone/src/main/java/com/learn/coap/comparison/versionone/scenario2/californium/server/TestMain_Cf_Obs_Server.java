package com.learn.coap.comparison.versionone.scenario2.californium.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;


import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
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
 * 
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Server {
	public static void main(String[] args) {

		int DEFAULT_PORT = 5684;
		//final Logger LOG = LoggerFactory.getLogger(TestMain_Cf_Obs_Server.class.getName());
		
		String myusr_path = System.getProperty("user.dir");
		
		String KEY_STORE_LOCATION = "mycerts/californium/server/my_own/mykeystore.jks";
		char[] KEY_STORE_PASSWORD = "SksOneAdmin".toCharArray();
		String TRUST_STORE_LOCATION = "mycerts/californium/server/my_own/mykeystore_truststore.jks";
		char[] TRUST_STORE_PASSWORD = "StsOneAdmin".toCharArray();
		
		SslContextUtil.Credentials serverCredentials = null;
		Certificate[] trustedCertificates = null;
		
		DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

			@Override
			public void applyDefinitions(Configuration config) {
				config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
				config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
			}

		};
		
		try {	
			serverCredentials = SslContextUtil.loadCredentials(myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD, KEY_STORE_PASSWORD);
			trustedCertificates = SslContextUtil.loadTrustedCertificates(myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		
		Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example server", DEFAULTS);
		
		//ref:https://www.rfc-editor.org/rfc/rfc7641#section-4.5
		// A server that transmits notifications mostly in non-confirmable
		//  messages MUST send a notification in a confirmable message instead of
		//  a non-confirmable message at least every 24 hours.  This prevents a
		//  client that went away or is no longer interested from remaining in
		//  the list of observers indefinitely.
		//如果不这样设置, 如果使用的是non-confirmable message, 它默认每100条 就会发送一个CON(带内容)
		//这样会影响我测试NON,所以我改成999, 方便测试, 淡然你也可以改成其他数字
		configuration.set(CoapConfig.NOTIFICATION_CHECK_INTERVAL_COUNT, 999);	
		
		DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(configuration);

		builder.setAddress(new InetSocketAddress(DEFAULT_PORT));	
		builder.setCertificateIdentityProvider(new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY));
		builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder().setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
		
		DTLSConnector dtlsConnector = new DTLSConnector(builder.build());

		CoapEndpoint.Builder coapBuilder = new CoapEndpoint.Builder().setConfiguration(configuration).setConnector(dtlsConnector);
		CoapServer server = new CoapServer();
		server.addEndpoint(coapBuilder.build());								// set DTLSConnector into a configuration into CoapEndpoint into CoapClient
		
		Cf_ObserverResource myobResc1 = new Cf_ObserverResource("Resource1");	//new resource
		myobResc1.setStatusUpdateMaxTimes(50);
		server.add(myobResc1);
		
		myobResc1.startResource();
		server.start();

		while (!myobResc1.isMyDone()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		myobResc1.stopResource();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// destory 可以结束程序, 但是stop不可以
		//server.destroy();
		//server.stop();
		server.destroy();

	}
}
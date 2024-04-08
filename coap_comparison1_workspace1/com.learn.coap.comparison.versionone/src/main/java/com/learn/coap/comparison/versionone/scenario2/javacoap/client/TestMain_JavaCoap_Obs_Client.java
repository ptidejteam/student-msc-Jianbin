package com.learn.coap.comparison.versionone.scenario2.javacoap.client;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;

import javax.net.ssl.TrustManagerFactory;


import com.mbed.coap.client.CoapClient;
import com.mbed.coap.client.CoapClientBuilder;
import com.mbed.coap.client.ObservationListener;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.packet.CoapPacket;
import com.mbed.coap.server.CoapServer;
import com.mbed.coap.transport.javassl.CoapSerializer;
import com.mbed.coap.transport.javassl.SSLSocketClientTransport;

/**
 * 多一个 MyObservationListener, 与 coapserver 的 ssl/tls版本比    这里不需要 KeyManagerFactory
 * @author laipl
 *
 */
public class TestMain_JavaCoap_Obs_Client {
	private int expectedNumberOfMessages			= 30;
	private int numberOfMessages 					= 0;
	
	//private String clientSeq = null;
	
	
	public static void main(String[] args) {
		new TestMain_JavaCoap_Obs_Client().run();
    }
	
	public void run() {

		String serverCaCrt_file					="s_cacert.crt";
		String serverCaCrt_file_dir				="/mycerts/javacoap/client/other_own";
		String serverCaCrt_file_loc = null;
		
		String myusr_path = System.getProperty("user.dir");
		serverCaCrt_file_loc 							= 	myusr_path	+ serverCaCrt_file_dir		+"/" + 	serverCaCrt_file;
	         

        //s_cacert.crt ->FileInputStream->BufferedInputStream-> ca Certificate
        FileInputStream fis= null;
        CertificateFactory cf = null;
        Certificate ca=null;
        InputStream caInput =null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			fis = new FileInputStream(serverCaCrt_file_loc);
			caInput = new BufferedInputStream(fis);

			ca = cf.generateCertificate(caInput);
		} catch (FileNotFoundException | CertificateException e1) {
			e1.printStackTrace();
		} 
		finally {
			try {
				caInput.close();	//关闭 s_cacert.crt 的stream  
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
        
		// KeyStore set ca Certificate -> TrustManagerFactory
		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore=null;
		TrustManagerFactory tmf = null;
		try {
			// Create a KeyStore containing our trusted CAs
			keyStoreType = KeyStore.getDefaultType();
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e3) {
			e3.printStackTrace();
		} 

		
		// finally, create SSL socket factory
		// TrustManagerFactory -> SSLContext
		SSLContext context=null;
		try {
			context = SSLContext.getInstance("TLSv1.3");
			context.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());

		} catch (NoSuchAlgorithmException | KeyManagementException e5) {
			e5.printStackTrace();
		} 	

		
		// CoapServer
		InetSocketAddress inetSocketAddr = new InetSocketAddress("127.0.0.1",5684);
		CoapClient client = null;
		
		//because in javacoap ssl condition, could not easily find a function that open client firstly
		// because it might show connection refused if there is not a server_ssl has opened firstly
		// so that is why I use a trick way to solve this condition
		boolean connected = false;
		// this server is used to Informs client if server is running
		CoapServer coapserver_target=CoapServer.builder().transport(new SSLSocketClientTransport(inetSocketAddr, context.getSocketFactory(), CoapSerializer.UDP, false)).build();
		while(connected==false) {
			try {
				//client = CoapClientBuilder.clientFor(inetSocketAddr,coapserver_target);			//这样还是不行的
				client = CoapClientBuilder.clientFor(inetSocketAddr,coapserver_target.start()); 		//需要start才可以
				connected = true;
			}
			catch(IOException e6) {
				e6.printStackTrace();
			}
		}

		
		
		CompletableFuture<CoapPacket> resp = null;
		try {
			//observer
			resp = client.resource("/Resource1").observe(new MyObservationListener());
			if(resp != null) {
				//用来获取 第一次得到的数据
				System.out.println(resp.get().getPayloadString().toString());
				numberOfMessages = numberOfMessages +1;
			}
		} catch (CoapException | InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}

        //---------------------------------------------
		// 停留一段时间 让server继续运行
        while(numberOfMessages < expectedNumberOfMessages) {
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        //
		client.close();
	}
	
	/**
	 * ObservationListener
	 * ref: java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java
	 * 
	 * @author laipl
	 *
	 */
    public class MyObservationListener implements ObservationListener {

        @Override
        public void onObservation(CoapPacket obsPacket) throws CoapException {
            System.out.println(obsPacket.getPayloadString());
            numberOfMessages = numberOfMessages +1;
        }

        @Override
        public void onTermination(CoapPacket obsPacket) throws CoapException {
        	System.out.println("term!!!!!!!"+obsPacket.getPayloadString());
        }
    }
}

